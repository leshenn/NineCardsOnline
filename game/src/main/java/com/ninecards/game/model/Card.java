package com.ninecards.game.model;

public class Card {
    private final Suit suit;
    private final Value value;
    //private Integer aceValue;

    public Card(Suit suit, Value value) {
        this.suit = suit;
        this.value = value;
    }

    public Suit getSuit() {
        return suit;
    }

    
    public Value getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value + " " + suit;
    }

    public int getNumericValue() {
        //if (value == Value.ACE) {
            //return aceValue != null ? aceValue : 14; // resolved or default
        //}
        return value.getNumericValue(); // all other cards just use the enum
    }
}
