package com.ninecards.game.controller;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ninecards.game.model.Suit;
import com.ninecards.game.service.GameService;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    // POST /game/start?numPlayers=3
    @PostMapping("/start")
    public String startGame(@RequestParam int numPlayers) {
        return gameService.startGame(numPlayers);
    }

    // POST /game/pickup?playerChoice=1  (1 = deck, 2 = discard pile)
    @PostMapping("/pickup")
    public String pickUpCard(@RequestParam int playerChoice) {
        return gameService.pickUpCard(playerChoice);
    }

    // POST /game/set?playerSet=1,2,3
    @PostMapping("/set")
    public String declareSet(@RequestParam String playerSet) {
        return gameService.declareSet(playerSet);
    }

    @PostMapping("/fillset")
    public String fillSet(@RequestParam int cardIdx, @RequestParam Suit suit, @RequestParam(required=false) String position) {
        return gameService.fillSet(cardIdx, suit, position);
    }
    // POST /game/discard?discardIdx=2
    @PostMapping("/discard")
    public String discardCard(@RequestParam int discardIdx) {
        return gameService.discardCard(discardIdx);
    }

    // GET /game/state
    @GetMapping("/state")
    public String getGameState() {
        return gameService.getGameState();
    }

    // GET /game/hand
    @GetMapping("/hand")
    public String getCurrentPlayerHand() {
        return gameService.getCurrentPlayerHand();
    }

    @GetMapping("/getsets")
    public ArrayList<String> getAllSets() {
        return gameService.allSets();
    }
}