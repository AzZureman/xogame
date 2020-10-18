package learning.java.game.rest;

import learning.java.game.controller.GameService;
import learning.java.game.rest.item.GameMapping;
import learning.java.game.rest.item.GameTO;
import learning.java.game.rest.request.CreateGameRequest;
import learning.java.game.rest.request.TurnGameRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("game")
public class GameRestController {

    @Autowired
    GameService gameService;

    @Autowired
    GameMapping mapping;

    @GetMapping("{id}")
    @ResponseBody
    public GameTO gameFromId(@PathVariable String id) {
        return mapping.gameTOFromGame(gameService.getGameFromId(id));
    }

    @PostMapping
    public @ResponseBody GameTO postGame(@RequestBody CreateGameRequest createGameRequest) {
        return mapping.gameTOFromGame(gameService.postNewGame(createGameRequest));
    }

    @PostMapping("{id}/turn")
    public  @ResponseBody GameTO turn(@RequestBody TurnGameRequest turnGameRequest,
                                     @PathVariable String id) {

        return mapping.gameTOFromGame(gameService.turnGameFromId(turnGameRequest, id));
    }
}

