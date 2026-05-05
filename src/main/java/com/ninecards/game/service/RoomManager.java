package com.ninecards.game.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.ninecards.game.model.Room;
import com.ninecards.game.model.RoomPlayer;
import com.ninecards.game.model.RoomStatus;

@Component
public class RoomManager {
    private final Map<String, Room> rooms = new HashMap<>();
    private final Map<String, String> sessionToRoom = new ConcurrentHashMap<>();
    private final Map<String, Integer> roomPlayerCount = new ConcurrentHashMap<>();

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
        } while (rooms.containsKey(code));
        return code;
    }

    public Room createRoom(int maxPlayers) {
        String code = generateCode();
        Room room = new Room(code, maxPlayers);
        rooms.put(code, room);
        return room;
    }

    public Room getRoom(String code) {
        return rooms.get(code);
    }

    public RoomPlayer joinRoom(String code, String playerName) {
        Room room = rooms.get(code);
        if (room == null) throw new RuntimeException("Room not found");
        if (room.isFull()) throw new RuntimeException("Room is full");
        if (room.getStatus() == RoomStatus.STARTED) throw new RuntimeException("Game already started");

        int playerId = room.getPlayers().size() + 1;
        RoomPlayer player = new RoomPlayer(playerId, playerName);
        room.addPlayer(player);
        return player;
    }

    public Map<String, Room> getAllRooms() {
        return rooms;
    }

    // ── Session tracking ──────────────────────────────────────────────────

    public void registerSession(String sessionId, String roomCode) {
        sessionToRoom.put(sessionId, roomCode);
        roomPlayerCount.merge(roomCode, 1, Integer::sum);
    }

    public String getRoomCodeBySessionId(String sessionId) {
        return sessionToRoom.get(sessionId);
    }

    public void decrementPlayerCount(String roomCode) {
        roomPlayerCount.merge(roomCode, -1, Integer::sum);
    }

    public int getConnectedPlayerCount(String roomCode) {
        return roomPlayerCount.getOrDefault(roomCode, 0);
    }

    public void deleteRoom(String roomCode) {
        rooms.remove(roomCode);
        roomPlayerCount.remove(roomCode);
        sessionToRoom.values().removeIf(code -> code.equals(roomCode));
    }
}
