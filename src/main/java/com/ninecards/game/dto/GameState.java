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
    public HashMap<Integer, List<String>> allHands;
    public String joker;
    public String preJoker;
    public String topDiscard;
    public boolean isRunning;
    public int winner;
    public HashMap<Suit, List<Card>> suitSets;
    public HashMap<Value, List<Card>> donkeySets;
    public String turnPhase; 
    public boolean madeSet;

    public GameState getGameState(Game game) {
        Player currentPlayer = game.getPlayer(game.currentPlayerTurn());

        GameState state = new GameState();
        state.currentPlayer = currentPlayer.getId();

        // Build a map of ALL players' hands
        state.allHands = new HashMap<>();
        for (Player p : game.getPlayers()) {
            state.allHands.put(p.getId(), p.checkHand());
        }

        state.joker = game.getJoker().toString();
        state.preJoker = game.getPreJoker().toString();
        state.topDiscard = game.hasDiscardPile()
            ? game.getDiscardPile().get(game.getDiscardPile().size() - 1).toString()
            : "BACK CLUBS";
        state.isRunning = game.isRunning();
        state.winner = game.getWinner();
        state.suitSets = game.getSuitSets();
        state.donkeySets = game.getDonkeySet();
        state.turnPhase = game.getTurnPhase();
        state.madeSet = currentPlayer.getMadeSet();
        
        return state;
    }
}