package com.ninecards.game.model;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String roomCode;          // "ABC123"
    private int maxPlayers;           // 2, 3 or 4
    private RoomStatus status;        // WAITING or STARTED
    private List<RoomPlayer> players; // who has joined
    private Game game;                // the actual game, null until started

    public Room(String roomCode, int maxPlayers) {
        this.roomCode = roomCode;
        this.maxPlayers = maxPlayers;
        this.status = RoomStatus.WAITING; // always starts as waiting
        this.players = new ArrayList<>();
        this.game = null; // no game yet
    }

    public String getRoomCode() { return roomCode; }
    public int getMaxPlayers() { return maxPlayers; }
    public RoomStatus getStatus() { return status; }
    public List<RoomPlayer> getPlayers() { return players; }
    public Game getGame() { return game; }
    public void setStatus(RoomStatus status) { this.status = status; }
    public void setGame(Game game) { this.game = game; }

    public void addPlayer(RoomPlayer player) {
        players.add(player);
    }

    public boolean isFull() {
        return players.size() >= maxPlayers;
    }
}
