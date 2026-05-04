package com.ninecards.game.model;

import java.util.ArrayList;

public class Player extends Deck{
    private final int id;
    private final ArrayList<Card> hand = new ArrayList<>();
    private boolean madeSet = false;

    public Player(int id, Deck deck) {
        this.id = id;
        this.dealHand(deck);
    }

    private void dealHand(Deck deck) {
        for(int i = 0; i < 9; i++) {
            hand.add(deck.removeTopCard());
        }
    }

    public void addToHandFromDeck(Deck deck) {
        hand.add(deck.removeTopCard());
    }

    public void addToHandFromDiscard(Card card) {
        hand.add(card);
    }

    public int handSize() {
        return hand.size();
    }

    public ArrayList<String> checkHand() {
        ArrayList<String> playerCards = new ArrayList<>();
        
        for(Card card : hand) {
            playerCards.add(card.toString());
        }
        return playerCards;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public Card getCard(int idx) {
        return hand.get(idx);
    }

    public Card removeCard(int cardIdx) {
        return hand.remove(cardIdx);
    }

    public int getId() {
        return id;
    }

    public void setMadeSet(boolean response) {
        madeSet = response; 
    }

    public boolean getMadeSet() {
        return madeSet;
    }
}
