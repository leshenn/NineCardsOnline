package com.ninecards.game.model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;


import org.springframework.stereotype.Component;

@Component
public class Game {
    // Variables used throughout the game
    private Deck deck = new Deck();
    private List<Player> players = new ArrayList<>();
    private List<Card> discardPile = new ArrayList<>();
    private Card preJoker;
    private Value joker;
    private boolean isRunning = true;
    private int winner;
    private int numOfAlternations = 0;
    private Scanner userInput = new Scanner(System.in);
    private HashMap<Suit, List<Card>> suitSets = new HashMap<>();
    private List<Card> donkeySet = new ArrayList<>();
    
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
        
        /* 
        while(!validateNumPlayers) {
            // System.out.println("Enter the number of players(max 4): ");
            // int numPlayers = userInput.nextInt();

        }
        */
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


    // Take care of all the game logic
    /*public void playGame() {
        checkGameStatus();

        while (isRunning) {
            int playerIdx = currentPlayerTurn();

            playerTurn(playerIdx);
            numOfAlternations++;
        }
    }*/


    // Choose the jokeer
    public Card choosePreJoker() {
        return deck.removeTopCard();
    }

    // Choose the joker for the game
    private Value chooseJoker() {
        Value[] values = Value.values();
        return values[(preJoker.getValue().ordinal() + 1) % values.length];
    }

    /*
   
   
    public void playerTurn(int playerIdx, int playerChoice, String playerSet) {
        //Player curPlayer = players.get(playerIdx);
        
        //System.out.println("This is the pre joker: " + preJoker + "\nThis is the joker: " + joker);

        //playerPickUp(curPlayer, playerChoice);

        //playerMakeSet(curPlayer, playerChoice, playerSet); - can be deleted

        //validateSet(playerSet, curPlayer);
        
        //(curPlayer, playerChoice);
    
    }
    */

    public void playerPickUp(Player curPlayer, int playerChoice) {
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
        Card discardedCard = curPlayer.removeCard(discardIdx - 1);
        discardPile.add(discardedCard);
        
    }

    // public void playerMakeSet(Player curPlayer, int userResponse, String playerSet) {
        //String playerSet = "";
        // userInput.nextLine();
        //int userResponse;
        
        // System.out.println("Do you want to make a set(Yes[1]/No[0]): ");
        // userResponse = userInput.nextInt();

        //if(userResponse == 1) {
            //System.out.println("Choose the index of the cards you want to make the set: ");
            //playerSet = userInput.nextLine();
            //boolean validSet = validateSet(playerSet, curPlayer);
            //System.out.println(validSet);
            //userInput.nextLine();
        //}
    // }

    public boolean validateSet(String playerSet, Player curPlayer) {
        String[] parts = playerSet.split(",");
        List<Card> cardSet = new ArrayList<>();

        for(String part : parts) {
            int idx = Integer.parseInt(part) - 1;
            cardSet.add(curPlayer.getCard(idx));
        }

        boolean isValid = checkSuits(cardSet);

        if (isValid) {
            // Sort indices highest to lowest so removing one doesn't shift the others
            List<Integer> indices = new ArrayList<>();
            for (String part : parts) {
                indices.add(Integer.parseInt(part.trim()) - 1);
            }
            indices.sort(Collections.reverseOrder());
            for (int idx : indices) {
                curPlayer.removeCard(idx);
            }
        }

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

        Suit setSuit = cardSet.get(startIndex).getSuit();
        int cardValue = cardSet.get(startIndex).getValue().getNumericValue();

        for(int i = startIndex + 1; i < cardSet.size(); i++) {
            Card currentCard = cardSet.get(i);
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

        addCardsToSuit(setSuit, cardSet);

        return true;
    }

    public void addCardsToSuit(Suit suit, List<Card> cards) {
        // Check if the suit already exists in the map
        if (suitSets.containsKey(suit)) {
            // If it exists, simply add all the cards to the existing list
            // suitSets.get(suit).addAll(cards);
            throw new IllegalArgumentException("You cannot create a set of a suit that has already been created");
        } else {
            // If it doesn't exist, put the suit and the new list of cards in the map
            suitSets.put(suit, new ArrayList<>(cards));
        }
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
    }

    // Fill into set
    public boolean fillIntoSet(int cardIdx, Player curPlayer) {
        Card card = curPlayer.getCard(cardIdx-1);
        Suit cardSuit = card.getSuit();
        // Value cardValue = card.getValue();

        if(!suitSets.containsKey(cardSuit)) {
            return false;
        }

        List<Card> cards = suitSets.get(cardSuit);

        if(cards.isEmpty()) {
            return false;
        }

        // Check beginning of set
        if (cards.get(0).getValue().getNumericValue() == card.getValue().getNumericValue() + 1) {
            cards.add(0, card); // add to front
            curPlayer.removeCard(cardIdx);
            return true;
        }

        // Check end of set
        if (cards.get(cards.size() - 1).getValue().getNumericValue() == card.getValue().getNumericValue() - 1) {
            cards.add(card); // add to end
            curPlayer.removeCard(cardIdx);
            return true;
        }

        return false;
        
    }

    // This just gets the index of the player so we can deal with them
    public int currentPlayerTurn() {
        return numOfAlternations % players.size();
    }


    public void incrementTurn() {
        numOfAlternations++;
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

    // This is for testing purposes
    public Player getPlayer(int idx) {
        return players.get(idx);
    }
}
