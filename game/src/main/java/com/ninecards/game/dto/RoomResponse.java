package com.ninecards.game.dto;

public class RoomResponse {
    public String roomCode;
    public int playerId;

    public RoomResponse(String roomCode, int playerId) {
        this.roomCode = roomCode;
        this.playerId = playerId;
    }
}