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

    public GameMessage(int playerChoice, int discardIdx, List<Integer> playerSet, int cardIdx, Suit suit, String position) {
        this.playerChoice = playerChoice;
        this.discardIdx = discardIdx;
        this.playerSet = playerSet;
        this.cardIdx = cardIdx;
        this.suit = suit;
        this.position = position;
    }
}
