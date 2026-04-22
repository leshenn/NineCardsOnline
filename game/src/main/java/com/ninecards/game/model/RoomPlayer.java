package com.ninecards.game.model;

public class RoomPlayer {
    private int id;
    private String name;

    public RoomPlayer(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
