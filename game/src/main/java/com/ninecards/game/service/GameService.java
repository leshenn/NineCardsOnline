package com.ninecards.game.service;

//import javax.smartcardio.Card;

import org.springframework.stereotype.Service;

import com.ninecards.game.model.Game;
// import com.ninecards.game.model.Card;
import com.ninecards.game.model.Player;

@Service
public class GameService {

    private final Game game;

    public GameService(Game game) {
        this.game = game;
    }

    public String startGame(int numPlayers) {
        game.initializeGame(numPlayers);
        return String.format(
            "Game started with %d players. Pre-joker: %s | Joker rank: %s",
            numPlayers,
            game.getPreJoker(),
            game.getJoker()
        );
    }

    // Step 1: current player picks up a card (1 = deck, 2 = discard pile)
    public String pickUpCard(int playerChoice) {
        if (!game.isRunning()) return "Game is over. Player " + game.getWinner() + " won!";

        Player currentPlayer = game.getPlayer(game.currentPlayerTurn());

        // Fall back to deck if discard pile is empty
        if (playerChoice == 2 && !game.hasDiscardPile()) playerChoice = 1;

        game.playerPickUp(currentPlayer, playerChoice);

        String source = (playerChoice == 1) ? "the deck" : "the discard pile";
        return String.format("Player %d picked up from %s. Hand: %s",
            currentPlayer.getId(), source, currentPlayer.getHand());
    }

    // Step 2 (optional): validate a set declared by the current player
    // playerSet is a comma-separated list of card indices e.g. "1,2,3"
    public String declareSet(String playerSet) {
        if (!game.isRunning()) return "Game is over.";

        Player currentPlayer = game.getPlayer(game.currentPlayerTurn());
        boolean valid = game.validateSet(playerSet, currentPlayer);

        return valid
            ? "Valid set! Player " + currentPlayer.getId() + " declared a set."
            : "Invalid set. Cards must be consecutive and of the same suit (jokers are wild).";
    }

    public String fillSet(int cardIdx) {
        Player currentPlayer = game.getPlayer(game.currentPlayerTurn());
        boolean valid = game.fillIntoSet(cardIdx, currentPlayer);
        return valid
            ? "You have filled a valid card " + currentPlayer.getCard(cardIdx-1).toString()
            : "You cannot fill any set, you have choosen an invalid card";
    }

    // Step 3: current player discards, then we check win and advance the turn
    public String discardCard(int discardIdx) {
        if (!game.isRunning()) return "Game is over.";

        Player currentPlayer = game.getPlayer(game.currentPlayerTurn());

        if (discardIdx < 1 || discardIdx > currentPlayer.handSize()) {
            return "Invalid index. Choose between 1 and " + currentPlayer.handSize();
        }

        // Save the card BEFORE removing it
        String discardedCard = currentPlayer.getCard(discardIdx - 1).toString();

        game.playerDiscard(currentPlayer, discardIdx);

        // Check win BEFORE incrementing so getWinner() still points to the right player
        game.checkGameStatus();
        if (!game.isRunning()) return "Player " + game.getWinner() + " has won the game!";

        game.incrementTurn();

        return String.format("Player %d discarded card %s. Player %d's turn.",
            currentPlayer.getId(),
            discardedCard,
            game.getPlayer(game.currentPlayerTurn()).getId());
    }

    // Returns a snapshot of the current game state
    public String getGameState() {
        if (!game.isRunning()) return "Game over. Winner: Player " + game.getWinner();

        Player currentPlayer = game.getPlayer(game.currentPlayerTurn());
        String topDiscard = game.hasDiscardPile()
            ? game.getDiscardPile().get(game.getDiscardPile().size() - 1).toString()
            : "empty";

        return String.format("Turn: Player %d | Hand size: %d | Joker: %s | Top discard: %s",
            currentPlayer.getId(),
            currentPlayer.handSize(),
            game.getJoker(),
            topDiscard);
    }

    public String getCurrentPlayerHand() {
        Player currentPlayer = game.getPlayer(game.currentPlayerTurn());
        return "Player " + currentPlayer.getId() + "'s hand: " + currentPlayer.getHand();
    }
}