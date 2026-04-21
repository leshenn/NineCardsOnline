package com.ninecards.game.model;

import java.util.List;

public class GameState {
    public int currentPlayer;
    public List<String> hand;
    public String joker;
    public String preJoker;
    public String topDiscard;
    public boolean isRunning;
    public int winner;

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

        return state;
    }
}
