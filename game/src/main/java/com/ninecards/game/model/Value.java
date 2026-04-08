package com.ninecards.game.model;

public enum Value {
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    JACK(11),
    QUEEN(12),
    KING(13),
    ACE(14);

    private int numericValue;
    Value(int numericValue) { this.numericValue = numericValue; }
    public int getNumericValue() { return numericValue; }
    public void setAceValue(int newValue) {ACE.numericValue = newValue; }
    
    public static Value fromNumericValue(int numericValue) {
        for(Value value : Value.values()) {
            if(value.getNumericValue() == numericValue) {
                return value;
            }
        }
        return null; // not found
    }
}