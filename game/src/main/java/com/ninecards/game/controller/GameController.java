package com.ninecards.game.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ninecards.game.dto.GameState;
import com.ninecards.game.model.Card;
import com.ninecards.game.model.Game;
import com.ninecards.game.model.Suit;
import com.ninecards.game.model.Value;
import com.ninecards.game.service.GameService;
import com.ninecards.game.service.RoomManager;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;
    private final RoomManager roomManager;

    public GameController(GameService gameService, RoomManager roomManager) {
        this.gameService = gameService;
        this.roomManager = roomManager;
    }

    // helper so you dont repeat this line in every method
    private Game getGame(String roomCode) {
        return roomManager.getRoom(roomCode).getGame();
    }

    // POST /game/pickup?playerChoice=1  (1 = deck, 2 = discard pile)
    @PostMapping("/pickup")
    public String pickUpCard(@RequestParam int playerChoice, @RequestParam String roomCode) {
        return gameService.pickUpCard(playerChoice, getGame(roomCode));
    }

    // POST /game/set?playerSet=1,2,3
    @PostMapping("/set")
    public String declareSet(@RequestBody List<Integer> playerSet, @RequestParam String roomCode) {
        return gameService.declareSet(playerSet, getGame(roomCode));
    }

    @PostMapping("/fillset")
    public String fillSet(@RequestParam int cardIdx, @RequestParam Suit suit, @RequestParam(required=false) String position, @RequestParam String roomCode) {
        return gameService.fillSet(cardIdx, suit, position, getGame(roomCode));
    }
    // POST /game/discard?discardIdx=2
    @PostMapping("/discard")
    public String discardCard(@RequestParam int discardIdx, @RequestParam String roomCode) {
        return gameService.discardCard(discardIdx, getGame(roomCode));
    }

    @GetMapping("/prejoker")
    public String getPreJoker(@RequestParam String roomCode) {
        return gameService.getPreJoker(getGame(roomCode));
    }

    @GetMapping("/joker")
    public String getJoker(@RequestParam String roomCode) {
        return gameService.getJoker(getGame(roomCode));
    }

    @GetMapping("/fullgamestate")
    public GameState getFullGameState(@RequestParam String roomCode) {
        return gameService.getFullGameState(getGame(roomCode));
    }

    @GetMapping("/suitsets")
    public HashMap<Suit, List<Card>> getSuitSets(@RequestParam String roomCode) {
        return gameService.getSuitSets(getGame(roomCode));
    }

    @GetMapping("/donkeysets")
    public HashMap<Value, List<Card>> getDonkeySets(@RequestParam String roomCode) {
        return gameService.getDonkeySet(getGame(roomCode));
    }
}