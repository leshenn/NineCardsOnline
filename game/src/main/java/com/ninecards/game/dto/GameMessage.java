package com.ninecards.game.dto;

import java.util.List;

import com.ninecards.game.model.Suit;

public class GameMessage {
    public int playerChoice;
    public int discardIdx;
    public List<Integer> playerSet;
    public int cardIdx;
    public Suit suit;
    public String position;
}
