package com.ninecards.game.model;

public class RoomPlayer {
    private final int id;
    private final String name;

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
