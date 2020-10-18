package learning.java.game.controller;

import learning.java.game.dao.*;
import learning.java.game.exception.NotFoundExceptions;
import learning.java.game.model.Figure;
import learning.java.game.model.Game;
import learning.java.game.model.Point;
import learning.java.game.rest.request.CreateGameRequest;
import learning.java.game.rest.request.TurnGameRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.UUID;

@Service
public class GameServiceSingle implements GameService {

    @Autowired
    private GameController gameControl;

    @Autowired
    private GamesDao dao;

    @Autowired
    private PlayersDao playersDao;

    @Override
    public Game getGameFromId(String id) throws SQLException {
        UUID key = UUID.fromString(id);
        Game game = dao.read(key);
        if(game == null || game.getId() == null)
            throw new NotFoundExceptions("Not found game with this id: " + key);

        return game;
    }

    @Override
    public Game postNewGame(CreateGameRequest createGameRequest) throws SQLException {
        Figure postFigure = createGameRequest.getSide();

        if (postFigure == null)
            throw new IllegalArgumentException("Sent request with incorrect body. " +
                    "Try - {\"side\":\"X\"}");

        Game game = gameControl.newGame(postFigure);
        game.setTurn(gameControl.currentFigure(game.getField()));
        playersDao.create(game.getPlayer1().getPlayer());
        playersDao.create(game.getPlayer2().getPlayer());
        dao.create(game);
        return game;
    }

    @Override
    public Game turnGameFromId(TurnGameRequest turnGameRequest, String id) throws SQLException {
        if (turnGameRequest == null ) {
            throw new IllegalArgumentException("Sent request with incorrect body." +
                    " Try - {\"position\":\"[2,2]\"}");
        }
        if (id == null)
            throw new IllegalArgumentException("Sent request to incorrect address." +
                    " Try - game/{UUID}/turn");
        Game gameXO = dao.read(UUID.fromString(id));
        if (gameXO == null || gameXO.getId() == null)
            throw new NotFoundExceptions("Game with id: \"" + id + "\" Not found");

        Point point = new Point(turnGameRequest.getX(), turnGameRequest.getY());
        gameControl.letsPlay(gameXO, point);
        dao.update(gameXO);
        return gameXO;
    }


}
