package com.ninecards.game.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ninecards.game.dto.GameState;
import com.ninecards.game.dto.RoomResponse;
import com.ninecards.game.model.Game;
import com.ninecards.game.model.Room;
import com.ninecards.game.model.RoomPlayer;
import com.ninecards.game.model.RoomStatus;
import com.ninecards.game.service.GameService;
import com.ninecards.game.service.RoomManager;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/room")
public class RoomController {
    private final RoomManager roomManager;
    private final SimpMessagingTemplate messagingTemplate; // add this
    private final GameService gameService;
    
    public RoomController(RoomManager roomManager, SimpMessagingTemplate messagingTemplate, GameService gameService) {
        this.roomManager = roomManager;
        this.messagingTemplate = messagingTemplate;
        this.gameService = gameService;
    }

    // Create a new room
    @PostMapping("/create")
    public RoomResponse createRoom(@RequestParam int maxPlayers, @RequestParam String playerName) {
        Room room = roomManager.createRoom(maxPlayers);
        RoomPlayer host = new RoomPlayer(1, playerName); // use playerName instead of "Host"
        room.addPlayer(host);
        return new RoomResponse(room.getRoomCode(), host.getId());
    }

    // Join an existing room
    @PostMapping("/join")
    public RoomResponse joinRoom(@RequestParam String code, @RequestParam String name) {
        Room room = roomManager.getRoom(code);

        if (room == null) return null;           // room doesn't exist
        if (room.isFull()) return null;          // room is full
        if (room.getStatus() == RoomStatus.STARTED) return null; // game already started

        int newPlayerId = room.getPlayers().size() + 1; // 0 taken, so next is 1, then 2...
        RoomPlayer player = new RoomPlayer(newPlayerId, name);
        room.addPlayer(player);

        // Tell everyone in the room someone joined
        messagingTemplate.convertAndSend("/topic/room/" + code,
            (Object)Map.of("event", "PLAYER_JOINED", "players", room.getPlayers().size(), "max", room.getMaxPlayers()));
        

        return new RoomResponse(room.getRoomCode(), newPlayerId);
    }

    // Start the game
    @PostMapping("/start")
    public String startGame(@RequestParam String code) {
        Room room = roomManager.getRoom(code);

        if (room == null) return "Room not found";
        if (room.getStatus() == RoomStatus.STARTED) return "Already started";

        Game game = new Game();
        room.setGame(game);
        game.initializeGame(room.getPlayers().size());
        room.setStatus(RoomStatus.STARTED);

        // First tell everyone the game started
        messagingTemplate.convertAndSend("/topic/room/" + code,
            (Object) Map.of("event", "GAME_STARTED"));

        // Then immediately send the full game state
        GameState state = gameService.getFullGameState(game);
        messagingTemplate.convertAndSend("/topic/room/" + code, (Object) state);

        return "Game started";
    }

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> listRooms() {
        Map<String, Room> allRooms = roomManager.getAllRooms();
        
        List<Map<String, Object>> result = allRooms.values().stream()
            .filter(room -> room.getStatus() != RoomStatus.STARTED) // only show open rooms
            .map(room -> {
                Map<String, Object> r = new HashMap<>();
                r.put("roomCode", room.getRoomCode());
                r.put("playerCount", room.getPlayers().size());
                r.put("maxPlayers", room.getMaxPlayers());
                return r;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }


}
