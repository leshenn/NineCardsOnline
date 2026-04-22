package com.ninecards.game.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ninecards.game.dto.RoomResponse;
import com.ninecards.game.model.Room;
import com.ninecards.game.model.RoomPlayer;
import com.ninecards.game.model.RoomStatus;
import com.ninecards.game.service.RoomManager;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/room")
public class RoomController {
    private final RoomManager roomManager;
    
    public RoomController(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    // Create a new room
    @PostMapping("/create")
    public RoomResponse createRoom(@RequestParam int maxPlayers) {
        Room room = roomManager.createRoom(maxPlayers);
        RoomPlayer host = new RoomPlayer(0, "Host");
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

        int newPlayerId = room.getPlayers().size(); // 0 taken, so next is 1, then 2...
        RoomPlayer player = new RoomPlayer(newPlayerId, name);
        room.addPlayer(player);

        return new RoomResponse(room.getRoomCode(), newPlayerId);
    }

    // Start the game
    @PostMapping("/start")
    public String startGame(@RequestParam String code) {
        Room room = roomManager.getRoom(code);

        if (room == null) return "Room not found";
        if (room.getStatus() == RoomStatus.STARTED) return "Already started";

        room.getGame().initializeGame(room.getPlayers().size());
        room.setStatus(RoomStatus.STARTED);

        return "Game started";
    }
}
