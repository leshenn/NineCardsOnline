package com.ninecards.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.ninecards.game.dto.GameMessage;
import com.ninecards.game.dto.GameState;
import com.ninecards.game.dto.RoomResponse;
import com.ninecards.game.model.Game;
import com.ninecards.game.service.GameService;
import com.ninecards.game.service.RoomManager;

@Controller
public class GameWebSocketController {
    @Autowired
    private GameService gameService;

    @Autowired
    private RoomManager roomManager;

    // helper so you dont repeat this line in every method
    private Game getGame(String roomCode) {
        return roomManager.getRoom(roomCode).getGame();
    }

    @MessageMapping("/pickup")
    @SendTo("/topic/gamestate")
    public GameState handlePickup(GameMessage msg, RoomResponse response) {
        gameService.pickUpCard(msg.playerChoice, getGame(response.roomCode));
        return gameService.getFullGameState(getGame(response.roomCode));
    }

    @MessageMapping("/discard")
    @SendTo("/topic/gamestate")
    public GameState handleDiscard(GameMessage msg,  RoomResponse response) {
        gameService.discardCard(msg.discardIdx, getGame(response.roomCode));
        return gameService.getFullGameState(getGame(response.roomCode));
    }
}
