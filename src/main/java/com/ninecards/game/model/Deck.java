package com.ninecards.game.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final ArrayList<Card> deck = new ArrayList<>();

    public Deck(){
        this.createDeck();
        this.shuffleDeck();
    }

    private void createDeck() {
        for(Suit suit : Suit.values()) {
            for(Value value : Value.values()) {
                deck.add(new Card(suit, value));
            }
        }
    }

    private void shuffleDeck() {
        Collections.shuffle(deck);
    }

    public int deckSize() {
        return deck.size();
    }


    public Card removeTopCard() {
        return deck.remove(deckSize() - 1);

    }


    public void reshuffleFromDiscard(List<Card> discardPile) {
        deck.addAll(discardPile);
        Collections.shuffle(deck);
        discardPile.clear();
    }

    public boolean isEmpty() {
        return deck.isEmpty();
    }

}
