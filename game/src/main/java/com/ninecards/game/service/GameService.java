package com.ninecards.game.service;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ninecards.game.dto.GameState;
import com.ninecards.game.model.Card;
import com.ninecards.game.model.Game;
import com.ninecards.game.model.Player;
import com.ninecards.game.model.Suit;
import com.ninecards.game.model.Value;

@Service
public class GameService {

    public int  startGame(int numPlayers, Game game) {
         if(numPlayers < 2 || numPlayers > 4) {
             return -1;
         }
         game.initializeGame(numPlayers);
         return game.currentPlayerTurn();
    }

    // current player picks up a card (1 = deck, 2 = discard pile)
    public String pickUpCard(int playerChoice, Game game) {
        if (!game.isRunning()) return "Game is over. Player " + game.getWinner() + " won!";

        Player currentPlayer = game.getPlayer(game.currentPlayerTurn());

        // Fall back to deck if discard pile is empty
        if (playerChoice == 2 && !game.hasDiscardPile()) playerChoice = 1;

        game.playerPickUp(currentPlayer, playerChoice);
        game.setTurnPhase("discard");
        String source = (playerChoice == 1) ? "the deck" : "the discard pile";
        return String.format("Player %d picked up from %s. \nHand:\n %s",
            currentPlayer.getId(), source, currentPlayer.checkHand());
    }

    // validate a set declared by the current player
    public String declareSet(List<Integer> playerSet, Game game) {
        LinkedHashSet<Integer> set = new LinkedHashSet<>(playerSet);
        if (!game.isRunning()) return "Game is over.";

        Player currentPlayer = game.getPlayer(game.currentPlayerTurn());
        boolean valid;
        if(game.getNumberOfSets() != 4) {
            valid = game.validateSet(set, currentPlayer);
        }
        else{
            valid = game.validateDonkeySuit(set, currentPlayer);
        }
        

        return valid
            ? "Valid set! Player " + currentPlayer.getId() + " declared a set."
            : "Invalid set. Cards must be consecutive and of the same suit (jokers are wild) OR all sets are already created.";
    }

    // Allow user to fill into set
    public String fillSet(int cardIdx, Suit suit, String position, Game game) {
        Player currentPlayer = game.getPlayer(game.currentPlayerTurn());
        Card tempCard = currentPlayer.getCard(cardIdx);
        boolean valid = game.fillIntoSet(cardIdx, currentPlayer, suit, position);
        return valid
            ? "You have filled a valid card " + tempCard.toString()
            : "You cannot fill any set, you have choosen an invalid card OR you need to come down before creating a set";
    }

    // current player discards, then we check win and advance the turn
    public String discardCard(int discardIdx, Game game) {
        if (!game.isRunning()) return "Game is over.";

        Player currentPlayer = game.getPlayer(game.currentPlayerTurn());

        if (discardIdx < 0 || discardIdx > currentPlayer.handSize() - 1) {
            return "Invalid index. Choose between 0 and " + (currentPlayer.handSize() - 1);
        }

        // Save the card BEFORE removing it
        String discardedCard = currentPlayer.getCard(discardIdx).toString();

        game.playerDiscard(currentPlayer, discardIdx);
        game.setTurnPhase("pick");

        // Check win BEFORE incrementing so getWinner() still points to the right player
        game.checkGameStatus();
        if (!game.isRunning()) return "Player " + game.getWinner() + " has won the game!";

        game.incrementTurn();

        return String.format("Player %d discarded card %s. Player %d's turn.",
            currentPlayer.getId(),
            discardedCard,
            game.getPlayer(game.currentPlayerTurn()).getId());
    }

    public HashMap<Suit, List<Card>> getSuitSets(Game game) {
        return game.getSuitSets();
    }

    public HashMap<Value, List<Card>> getDonkeySet(Game game) {
        return game.getDonkeySet();
    }


    public String getPreJoker(Game game) {
        return game.getPreJoker().toString();
    }

    public String getJoker(Game game) {
        return game.getJoker().toString();
    }


    public GameState getFullGameState(Game game) {
        return new GameState().getGameState(game);
    }
}