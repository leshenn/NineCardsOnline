package com.ninecards.game.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.ninecards.game.dto.GameMessage;
import com.ninecards.game.dto.GameState;
import com.ninecards.game.model.Game;
import com.ninecards.game.model.Player;
import com.ninecards.game.service.GameService;
import com.ninecards.game.service.RoomManager;

@Controller
public class GameWebSocketController {
    @Autowired
    private GameService gameService;

    @Autowired
    private RoomManager roomManager;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private Game getGame(String roomCode) {
        return roomManager.getRoom(roomCode).getGame();
    }

    @MessageMapping("/pickup")
    public void handlePickup(GameMessage msg) {
        Game game = getGame(msg.roomCode);
        Player currentPlayer = game.getPlayer(game.currentPlayerTurn());
        
        // Ignore if it's not this player's turn
        if (currentPlayer.getId() != msg.playerId) return;
        
        gameService.pickUpCard(msg.playerChoice, game);
        GameState state = gameService.getFullGameState(game);
        messagingTemplate.convertAndSend("/topic/room/" + msg.roomCode, state);
    }
    
    @MessageMapping("/set")
    public void declareSet(GameMessage msg) {
        Game game = getGame(msg.roomCode);
        Player currentPlayer = game.getPlayer(game.currentPlayerTurn());
        if (currentPlayer.getId() != msg.playerId) return;

        gameService.declareSet(msg.cardIndexes, game);

        // Check for winner
        if (currentPlayer.getHand().isEmpty()) {
            messagingTemplate.convertAndSend("/topic/room/" + msg.roomCode,
                (Object)Map.of("event", "GAME_OVER", "winner", currentPlayer.getId()));
            return;
        }
        
        GameState state = gameService.getFullGameState(game);
        messagingTemplate.convertAndSend("/topic/room/" + msg.roomCode, state);
    }
    @MessageMapping("/fillset")
    public void fillSet(GameMessage msg) {
        Game game = getGame(msg.roomCode);
        Player currentPlayer = game.getPlayer(game.currentPlayerTurn());
        if (currentPlayer.getId() != msg.playerId) return;

        gameService.fillSet(msg.cardIdx, msg.suit, msg.position, game);
        GameState state = gameService.getFullGameState(game);
        messagingTemplate.convertAndSend("/topic/room/" + msg.roomCode, state);
    }

    @MessageMapping("/discard")
    public void handleDiscard(GameMessage msg) {
        Game game = getGame(msg.roomCode);
        Player currentPlayer = game.getPlayer(game.currentPlayerTurn());
        if (currentPlayer.getId() != msg.playerId) return;

        gameService.discardCard(msg.discardIdx, game);

        // Check for winner
        if (currentPlayer.getHand().isEmpty()) {
            messagingTemplate.convertAndSend("/topic/room/" + msg.roomCode,
                (Object)Map.of("event", "GAME_OVER", "winner", currentPlayer.getId()));
            return;
        }
        
        GameState state = gameService.getFullGameState(game);
        messagingTemplate.convertAndSend("/topic/room/" + msg.roomCode, state);
    }

    @MessageMapping("/prejoker")
    public void getPreJoker(GameMessage msg) {
        gameService.getPreJoker(getGame(msg.roomCode));
        GameState state = gameService.getFullGameState(getGame(msg.roomCode));
        messagingTemplate.convertAndSend("/topic/room/" + msg.roomCode, state);        
    }

    @MessageMapping("/suitsets`")
    public void getSuitSets(GameMessage msg) {
        gameService.getSuitSets(getGame(msg.roomCode));
        GameState state = gameService.getFullGameState(getGame(msg.roomCode));
        messagingTemplate.convertAndSend("/topic/room/" + msg.roomCode, state);        
    }
    
    @MessageMapping("/donkeysets`")
    public void getDonkeySet(GameMessage msg) {
        gameService.getDonkeySet(getGame(msg.roomCode));
        GameState state = gameService.getFullGameState(getGame(msg.roomCode));
        messagingTemplate.convertAndSend("/topic/room/" + msg.roomCode, state);        
    }

    @MessageMapping("/changephase")
    public void handleChangePhase(GameMessage msg) {
        Game game = getGame(msg.roomCode);
        Player currentPlayer = game.getPlayer(game.currentPlayerTurn());
        if (currentPlayer.getId() != msg.playerId) return;
        
        // Only allow phase change if not in pick phase
        if (game.getTurnPhase().equals("pick")) return;
        
        game.setTurnPhase(msg.phase);
        GameState state = gameService.getFullGameState(game);
        messagingTemplate.convertAndSend("/topic/room/" + msg.roomCode, state);
    }
}
