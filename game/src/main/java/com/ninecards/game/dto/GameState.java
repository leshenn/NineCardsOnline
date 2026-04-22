package com.ninecards.game.dto;

import java.util.HashMap;
import java.util.List;

import com.ninecards.game.model.Card;
import com.ninecards.game.model.Game;
import com.ninecards.game.model.Player;
import com.ninecards.game.model.Suit;
import com.ninecards.game.model.Value;

public class GameState {
    public int currentPlayer;
    public List<String> hand;
    public String joker;
    public String preJoker;
    public String topDiscard;
    public boolean isRunning;
    public int winner;
    public HashMap<Suit, List<Card>> suitSets;
    public HashMap<Value, List<Card>> donkeySets;

    public GameState getGameState(Game game) {
        Player currentPlayer = game.getPlayer(game.currentPlayerTurn());

        GameState state = new GameState();

        state.currentPlayer = game.currentPlayerTurn();
        state.hand = currentPlayer.checkHand();
        state.joker = game.getJoker().toString();
        state.preJoker = game.getPreJoker().toString();

        state.topDiscard = game.hasDiscardPile()
            ? game.getDiscardPile().get(game.getDiscardPile().size() - 1).toString()
            : "BACK CLUBS";

        state.isRunning = game.isRunning();
        state.winner = game.getWinner();
        state.suitSets = game.getSuitSets();
        state.donkeySets = game.getDonkeySet();

        return state;
    }
}
