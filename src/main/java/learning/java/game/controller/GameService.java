package learning.java.game.controller;

import learning.java.game.model.Game;
import learning.java.game.rest.request.CreateGameRequest;
import learning.java.game.rest.request.TurnGameRequest;

import java.sql.SQLException;

public interface GameService {

    Game postNewGame(CreateGameRequest createGameRequest) throws SQLException;

    Game getGameFromId(String id) throws SQLException;

    Game turnGameFromId(TurnGameRequest turnGameRequest, String id) throws SQLException;
}
