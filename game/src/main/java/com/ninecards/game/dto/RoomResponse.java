package com.ninecards.game.dto;

public class RoomResponse {
    public String roomCode;
    public int playerId;
    public int roomMax;
    public String hostName;

    public RoomResponse(String roomCode, int playerId, int roomMax, String hostName) {
        this.roomCode = roomCode;
        this.playerId = playerId;
        this.roomMax = roomMax;
        this.hostName = hostName;
    }
}