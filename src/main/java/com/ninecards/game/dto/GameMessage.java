package com.ninecards.game.dto;

import java.util.List;

import com.ninecards.game.model.Suit;

public class GameMessage {
    public Integer playerChoice;
    public Integer discardIdx;
    public List<Integer> cardIndexes;
    public Integer cardIdx;
    public Suit suit;
    public String position;
    public String roomCode;
    public Integer playerId;
    public String phase;
    public boolean madeSet;

    public GameMessage() {}
}
