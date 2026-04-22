package com.ninecards.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.ninecards.game.dto.GameMessage;
import com.ninecards.game.dto.GameState;
import com.ninecards.game.service.GameService;

@Controller
public class GameWebSocketController {
    @Autowired
    private GameService gameService;


    @MessageMapping("/pickup")
    @SendTo("/topic/gamestate")
    public GameState handlePickup(GameMessage msg) {
        gameService.pickUpCard(msg.playerChoice);
        return gameService.getFullGameState();
    }

    @MessageMapping("/discard")
    @SendTo("/topic/gamestate")
    public GameState handleDiscard(GameMessage msg) {
        gameService.discardCard(msg.discardIdx);
        return gameService.getFullGameState();
    }
}
