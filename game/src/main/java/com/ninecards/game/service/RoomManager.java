package com.ninecards.game.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.ninecards.game.model.Room;

@Component
public class RoomManager {
    private final Map<String, Room> rooms = new HashMap<>();

    // 1. Generate a unique code
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

    // 2. Create a new room
    public Room createRoom(int maxPlayers) {
        String code = generateCode();
        Room room = new Room(code, maxPlayers);
        rooms.put(code, room);
        return room;
    }

    // 3. Find an existing room
    public Room getRoom(String code) {
        return rooms.get(code);
    }
}
