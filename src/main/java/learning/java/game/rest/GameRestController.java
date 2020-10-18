package learning.java.game.rest;

import learning.java.game.controller.GameService;
import learning.java.game.model.Game;
import learning.java.game.rest.request.CreateGameRequest;
import learning.java.game.rest.request.TurnGameRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("game")
public class GameRestController {

    @Autowired
    GameService gameService;

    @GetMapping("{id}")
    @ResponseBody
    public Game gameFromId(@PathVariable String id) throws SQLException {
        return gameService.getGameFromId(id);
    }

    @PostMapping
    public @ResponseBody Game postGame(@RequestBody CreateGameRequest createGameRequest) throws SQLException {
        return gameService.postNewGame(createGameRequest);
    }

    @PostMapping("{id}/turn")
    public  @ResponseBody Game turn(@RequestBody TurnGameRequest turnGameRequest,
                                     @PathVariable String id) throws SQLException {

        return gameService.turnGameFromId(turnGameRequest, id);
    }


}

