package learning.java.game.controller;

import learning.java.game.model.Game;
import learning.java.game.rest.request.CreateGameRequest;
import learning.java.game.rest.request.TurnGameRequest;

public interface GameService {

    Game postNewGame(CreateGameRequest createGameRequest);

    Game getGameFromId(String id);

    Game turnGameFromId(TurnGameRequest turnGameRequest, String id);
}
