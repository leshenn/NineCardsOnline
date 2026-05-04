package com.ninecards.game.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.ninecards.game.model.Room;
import com.ninecards.game.model.RoomPlayer;
import com.ninecards.game.model.RoomStatus;

@Component
public class RoomManager {
    private final Map<String, Room> rooms = new HashMap<>();

    // Generate a unique code
    private String generateCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        String code;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
            code = sb.toString();
        } while (rooms.containsKey(code)); // keep trying if code exists
        return code;
    }

    // Create a new room
    public Room createRoom(int maxPlayers) {
        String code = generateCode();
        Room room = new Room(code, maxPlayers);
        rooms.put(code, room);
        return room;
    }

    // Find an existing room
    public Room getRoom(String code) {
        return rooms.get(code);
    }

    public RoomPlayer joinRoom(String code, String playerName) {
        Room room = rooms.get(code);

        if (room == null) throw new RuntimeException("Room not found");
        if (room.isFull()) throw new RuntimeException("Room is full");
        if (room.getStatus() == RoomStatus.STARTED) throw new RuntimeException("Game already started");

        // Player IDs are just 1, 2, 3, 4 based on join order
        int playerId = room.getPlayers().size() + 1;
        RoomPlayer player = new RoomPlayer(playerId, playerName);
        room.addPlayer(player);
        return player;
    }

    public Map<String, Room> getAllRooms() {
        return rooms;
    }
}
