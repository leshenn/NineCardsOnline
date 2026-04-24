package com.ninecards.game.model;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private final String roomCode;          
    private final int maxPlayers;           
    private RoomStatus status;        
    private final List<RoomPlayer> players; 
    private Game game;                

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
