package com.ninecards.game.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class Game {
    // Variables used throughout the game
    private final Deck deck = new Deck();
    private final List<Player> players = new ArrayList<>();
    private final List<Card> discardPile = new ArrayList<>();
    private Card preJoker;
    private Value joker;
    private boolean isRunning = true;
    private int winner;
    private int numOfAlternations = 0;
    private final HashMap<Suit, List<Card>> suitSets = new HashMap<>();
    private final HashMap<Value, List<Card>> donkeySet = new HashMap<>();
    private int deckReshuffleTimes = 0;
    private String turnPhase = "pick"; // starts as pick
    
    // Initialize the game
    public void initializeGame(int numPlayers) {
        createPlayers(numPlayers);
        sortHand();
        preJoker = choosePreJoker();
        joker = chooseJoker();
    }

    // Create the playes and allcate their cards
    public void createPlayers(int numPlayers) {

        if(numPlayers < 2 || numPlayers > 4) {
            throw new IllegalArgumentException("Number of players must be between 2 and 4");        
        }

        players.clear();
        for(int i = 1; i < numPlayers + 1; i++) {
            players.add(new Player(i, deck));
        }
    }

    // Sorts the hand in order of suit then value
    public void sortHand() {
        for(Player player : players) {
            Collections.sort(player.getHand(), (card1, card2) -> {
                int suitCompare = card1.getSuit().compareTo(card2.getSuit());

                if(suitCompare != 0) {
                    return suitCompare;
                }

                return Integer.compare(card1.getValue().getNumericValue(), card2.getValue().getNumericValue());
            });
        }
    }


    // Choose the jokeer
    public Card choosePreJoker() {
        return deck.removeTopCard();
    }

    // Choose the joker for the game
    private Value chooseJoker() {
        Value[] values = Value.values();
        return values[(preJoker.getValue().ordinal() + 1) % values.length];
    }


    public void playerPickUp(Player curPlayer, int playerChoice) {
        if(deck.isEmpty()) {
            deck.reshuffleFromDiscard(discardPile);
            deckReshuffleTimes++;
        }

        if(playerChoice == 1 || discardPile.isEmpty()) {
            curPlayer.addToHandFromDeck(deck);
        }
        else if(playerChoice == 2) {
            Card discardedTopCard = discardPile.remove(discardPile.size() - 1);
            curPlayer.addToHandFromDiscard(discardedTopCard);
        }
        sortHand();
        curPlayer.checkHand();
    }

    public void playerDiscard(Player curPlayer, int discardIdx) {
        //curPlayer.checkHand();
        //System.out.println("What is the index of the card you want to discard: ");
        //int discardIdx = userInput.nextInt();
        Card discardedCard = curPlayer.removeCard(discardIdx);
        discardPile.add(discardedCard);
        
    }

    public boolean validateSet(LinkedHashSet<Integer> playerSet, Player curPlayer) {
        if(playerSet.size() < 3  && !curPlayer.getMadeSet()) return false;
        //String[] parts = playerSet.split(",");
        List<Card> cardSet = new ArrayList<>();

        for(int part : playerSet) {
             //int idx = Integer.parseInt(part);
             // cardSet.add(curPlayer.getCard(idx));
             cardSet.add(curPlayer.getCard(part));
        }

        boolean isValid = checkSuits(cardSet);

        if (isValid) {
            // Sort indices highest to lowest so removing one doesn't shift the others
            List<Integer> indices = new ArrayList<>();
            for (int part : playerSet) {
                //indices.add(Integer.parseInt(part));
                indices.add(part);
            }
            indices.sort(Collections.reverseOrder());
            for (int idx : indices) {
                curPlayer.removeCard(idx);
            }
        }
        
        curPlayer.setMadeSet(isValid);
        return isValid;
    }

    public boolean checkSuits(List<Card> cardSet) {
        int startIndex = -1;
        for(int i = 0; i < cardSet.size(); i++) {
            if(!cardSet.get(i).getValue().equals(joker)) {
                startIndex = i;
                break;
            }
        }   

        
        // This handles the case where the player only has jokers in their set, we can just return true because they can be any card
        if (startIndex == -1) {
            //throw new IllegalArgumentException("You cannot make a set with all jokers");
            return false; // all jokers
        }

        Card firstRealCard = cardSet.get(startIndex);

        // If the first real card is ACE, decide its value based on what follows it
        if (firstRealCard.getValue() == Value.ACE && firstRealCard.getValue() != joker) {
            int jokersBeforeNext = 0;
            for (int i = startIndex + 1; i < cardSet.size(); i++) {
                if (cardSet.get(i).getValue().equals(joker)) {
                    jokersBeforeNext++;
                } else {
                    int nextVal = cardSet.get(i).getValue().getNumericValue();
                    // ACE=1 is valid if nextVal equals 1 + jokers + 1
                    if (nextVal == 1 + jokersBeforeNext + 1) {
                        firstRealCard.getValue().setAceValue(1);
                    } else {
                        return false;
                    }
                    break;
                }
            }
        }


        Suit setSuit = cardSet.get(startIndex).getSuit();
        int cardValue = cardSet.get(startIndex).getValue().getNumericValue();

        for(int i = startIndex + 1; i < cardSet.size(); i++) {
            Card currentCard = cardSet.get(i);

            // Handle ACE appearing later in the set (must follow KING as 14)
            if (currentCard.getValue() != joker && currentCard.getValue() == Value.ACE) {
                if (cardValue == 13) {
                    currentCard.getValue().setAceValue(14);
                } else {
                    return false; // ACE can't fit here
                }
            }

            int currentCardValue = currentCard.getValue().getNumericValue();
            if((currentCard.getSuit().equals(setSuit) && currentCardValue == cardValue + 1) || currentCard.getValue().equals(joker)) {
                //System.out.println("Card " + currentCard + " is valid for the set");
                if(currentCard.getValue().equals(joker)) {
                    cardValue++;
                    continue;
                }
                cardValue = currentCardValue;
            }
            else {
                return false;
            }
        }

        return addCardsToSuit(setSuit, cardSet);
    }

    public boolean addCardsToSuit(Suit suit, List<Card> cards) {
        // Check if the suit already exists in the map
        if (suitSets.containsKey(suit)) {
            // If it exists, simply add all the cards to the existing list
            // suitSets.get(suit).addAll(cards);
            // throw new IllegalArgumentException("You cannot create a set of a suit that has already been created, you have to create a donkey set");
            return false;
        } else {
            // If it doesn't exist, put the suit and the new list of cards in the map
            suitSets.put(suit, new ArrayList<>(cards));
            return true;
        }
    }

    public boolean validateDonkeySuit(LinkedHashSet<Integer> playerSet, Player curPlayer) {
        //String[] parts = playerSet.split(",");
        List<Card> cardSet = new ArrayList<>();

        for(int part : playerSet) {
            //int idx = Integer.parseInt(part);
            cardSet.add(curPlayer.getCard(part));
        }

        if(cardSet.size() != 4 || suitSets.size() != 4 || !suitSets.isEmpty()) {
            return false;
        }

        return checkSameValue(cardSet);
    }

    public boolean checkSameValue(List<Card> cardSet) {
        int startIndex = -1;
        for(int i = 0; i < cardSet.size(); i++) {
            if(!cardSet.get(i).getValue().equals(joker)) {
                startIndex = i;
                break;
            }
        }   

        // This handles the case where the player only has jokers in their set, we can just return true because they can be any card
        if (startIndex == -1) {
            //throw new IllegalArgumentException("You cannot make a set with all jokers");
            return false; // all jokers
        }

        for(Card curCard : cardSet) {
            if(!(curCard.getValue() == cardSet.get(startIndex).getValue() || curCard.getValue() == joker)) {
                return false;
            }
        }

        donkeySet.put(cardSet.get(startIndex).getValue(), new ArrayList<>(cardSet));
        return true;
    }
  

    public boolean hasDiscardPile() {
        return !discardPile.isEmpty();
    }

    // This checks if the game is still being played (the players still have cards in their hand)
    public void checkGameStatus() {
        for(Player player : players) {
            if(player.handSize() == 0) {
                isRunning = false;
                winner = player.getId();
            }
        }

        if(deckReshuffleTimes == 3) {
            isRunning = false;
            winner = -1; // no winner
        }
    }

    public boolean fillIntoSet(int cardIdx, Player curPlayer, Suit suit, String position) {
        if (!curPlayer.getMadeSet()) {
            return false;
        }

        Card card = curPlayer.getCard(cardIdx);
        Suit cardSuit = suit;
        boolean isAce = card.getValue() == Value.ACE;

        // Jokers can fill into any suit that exists
        if (card.getValue() == joker) {
            return tryFillIntoSuit(suit, card, cardIdx, curPlayer, position);
        }

        if (!suitSets.containsKey(cardSuit)) {
            return false;
        }

        if (isAce) {
            // Try ACE as 1 (front), then as 14 (end)
            card.getValue().setAceValue(1);
            if (tryFillIntoSuit(cardSuit, card, cardIdx, curPlayer, position)) return true;

            card.getValue().setAceValue(14);
            if (tryFillIntoSuit(cardSuit, card, cardIdx, curPlayer, position)) return true;

            // Reset ace to default if it didn't fit anywhere
            card.getValue().setAceValue(1);
            return false;
        }

        return tryFillIntoSuit(cardSuit, card, cardIdx, curPlayer, position);
    }

    // Extracted helper — tries to fill a card into a specific suit's set
    private boolean tryFillIntoSuit(Suit cardSuit, Card card, int cardIdx, Player curPlayer, String position) {
        List<Card> cards = suitSets.get(cardSuit);

        if (cards == null || cards.isEmpty()) return false;

        if(card.getValue() == joker) {
            if (position == null || position.isEmpty()) return false; // must specify

            if (position.equalsIgnoreCase("start") && cards.get(0).getValue() != Value.ACE) {
                cards.add(0, card);
                curPlayer.removeCard(cardIdx);
                return true;
            } 
            else if (position.equalsIgnoreCase("end") && cards.get(cards.size() - 1).getValue() != Value.ACE) {
                cards.add(card);
                curPlayer.removeCard(cardIdx);
                return true;
            } 
            else {
                return false;
            }
        }

        Card first = cards.get(0);
        Card last = cards.get(cards.size() - 1);

        // --- Check FRONT ---
        if (first.getValue() == joker) {
            if (checkFront(cards, card)) {
                cards.add(0, card);
                curPlayer.removeCard(cardIdx);
                return true;
            }
        } else if (first.getValue().getNumericValue() == card.getValue().getNumericValue() + 1 && cardSuit == card.getSuit()) {
            cards.add(0, card);
            curPlayer.removeCard(cardIdx);
            return true;
        }

        // --- Check END ---
        if (last.getValue() == joker) {
            if (checkEnd(cards, card)) {
                cards.add(card);
                curPlayer.removeCard(cardIdx);
                return true;
            }
        } else if (last.getValue().getNumericValue() == card.getValue().getNumericValue() - 1 && cardSuit == card.getSuit()) {
            cards.add(card);
            curPlayer.removeCard(cardIdx);
            return true;
        }

        // --- Check joker replacement inside the set ---
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getValue() == joker) {
                Integer leftValue = null;
                for (int l = i - 1; l >= 0; l--) {
                    if (cards.get(l).getValue() != joker) {
                        leftValue = cards.get(l).getValue().getNumericValue();
                        break;
                    }
                }

                Integer rightValue = null;
                for (int r = i + 1; r < cards.size(); r++) {
                    if (cards.get(r).getValue() != joker) {
                        rightValue = cards.get(r).getValue().getNumericValue();
                        break;
                    }
                }

                int newValue = card.getValue().getNumericValue();
                boolean fitsLeft = (leftValue == null) || (newValue > leftValue);
                boolean fitsRight = (rightValue == null) || (newValue < rightValue);

                if (fitsLeft && fitsRight) {
                    Card jokerCard = cards.get(i);
                    cards.set(i, card);
                    curPlayer.removeCard(cardIdx);
                    curPlayer.addToHandFromDiscard(jokerCard);
                    sortHand();
                    return true;
                }
            }
        }

        return false;
    }

    /*
    // Fill into set
    public boolean fillIntoSet(int cardIdx, Player curPlayer) {
        if(!curPlayer.getMadeSet()) {
            return false;
        }

        Card card = curPlayer.getCard(cardIdx-1);
        Suit cardSuit = card.getSuit();
        // Value cardValue = card.getValue();

        if(!suitSets.containsKey(cardSuit) && card.getValue() != joker) {
            return false;
        }

        List<Card> cards = suitSets.get(cardSuit);

        if(cards.isEmpty()) {
            return false;
        }

        Card first = cards.get(0);
        Card last = cards.get(cards.size() - 1);

        // Check beginning of set
        if(first.getValue() == joker) {
            if(checkFront(cards, card)) {
                cards.add(0, card);
                curPlayer.removeCard(cardIdx);
                return true;
            }
        }
        else if (first.getValue().getNumericValue() == card.getValue().getNumericValue() + 1) {
            cards.add(0, card); // add to front
            curPlayer.removeCard(cardIdx);
            return true;
        }

        // Check end of set
        if(last.getValue() == joker) {
            if(checkEnd(cards, card)) {
                cards.add(card);
                curPlayer.removeCard(cardIdx);
                return true;
            }
        }
        else if (last.getValue().getNumericValue() == card.getValue().getNumericValue() - 1) {
            cards.add(card); // add to end
            curPlayer.removeCard(cardIdx);
            return true;
        }
        
        // Check if the set contains a joker
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getValue() == joker) {

                // Find left real card
                Integer leftValue = null;
                for (int l = i - 1; l >= 0; l--) {
                    if (cards.get(l).getValue() != joker) {
                        leftValue = cards.get(l).getValue().getNumericValue();
                        break;
                    }
                }

                // Find right real card
                Integer rightValue = null;
                for (int r = i + 1; r < cards.size(); r++) {
                    if (cards.get(r).getValue() != joker) {
                        rightValue = cards.get(r).getValue().getNumericValue();
                        break;
                    }
                }

                int newValue = card.getValue().getNumericValue();

                boolean fitsLeft = (leftValue == null) || (newValue > leftValue);
                boolean fitsRight = (rightValue == null) || (newValue < rightValue);

                if (fitsLeft && fitsRight) {
                    Card jokerCard = cards.get(i); // store joker
                    cards.set(i, card); // place your card
                    curPlayer.removeCard(cardIdx - 1); // FIX index
                    curPlayer.addToHandFromDiscard(jokerCard); // give joker back to player , it says discard pile, but it takes a card as a parameter
                    sortHand();
                    return true;
                }
            }
        }
        
        return false;
        
    }
    */


    public boolean checkFront(List<Card> cards, Card card) {
        int jokerCount = 0;
        int targetCard = -1;
        //Card targetCard;
        for(Card setCard : cards) {
            if(setCard.getValue() == joker) {
                jokerCount++;
            }
            else {
                targetCard = setCard.getNumericValue();
                break;
            }
        }

        return targetCard - 1 - jokerCount == card.getNumericValue();
    }

    public boolean checkEnd(List<Card> cards, Card card) {
        int jokerCount = 0;
        int targetCard = -1;
        //Card targetCard;
        for(int i = cards.size() - 1; i > -1; i--) {
            Card setCard = cards.get(i);
            if(setCard.getValue() == joker) {
                jokerCount++;
            }
            else {
                targetCard = setCard.getNumericValue();
                break;
            }
        }

        return targetCard + 1 + jokerCount == card.getNumericValue();
    }

    // This just gets the index of the player so we can deal with them
    public int currentPlayerTurn() {
        return numOfAlternations % players.size();
    }


    public void incrementTurn() {
        numOfAlternations++;
    }

    public ArrayList<String> printSets() {
        ArrayList<String> allSets = new ArrayList<>();
        for(Map.Entry<Suit, List<Card>> entry : suitSets.entrySet()) {
            allSets.add(entry.getKey() + " -> " + entry.getValue());
        }

        for(Map.Entry<Value, List<Card>> entry : donkeySet.entrySet()) {
            allSets.add(entry.getKey() + " -> " + entry.getValue());
        }

        return allSets;
    }

    public HashMap<Suit, List<Card>> getSuitSets() {
        return suitSets;
    }

    public HashMap<Value, List<Card>> getDonkeySet() {
        return donkeySet;
    }

    public Card getPreJoker() {
        return preJoker;
    }

    public Value getJoker() {
        return joker;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public int getWinner() {
        return winner;
    }

    public List<Card> getDiscardPile() {
        return discardPile;
    }

    public int getNumberOfSets() {
        return suitSets.size();
    }

    // This is for testing purposes
    public Player getPlayer(int idx) {
        return players.get(idx);
    }

    public List<Player> getPlayers() { return players; }

    public String getTurnPhase() { return turnPhase; }
    public void setTurnPhase(String turnPhase) { this.turnPhase = turnPhase; }
}
