package learning.java.game.controller;

import learning.java.game.dao.GamesDao;
import learning.java.game.dao.PlayersDao;
import learning.java.game.exception.NotFoundExceptions;
import learning.java.game.model.Figure;
import learning.java.game.model.Game;
import learning.java.game.model.Player;
import learning.java.game.model.Point;
import learning.java.game.rest.request.CreateGameRequest;
import learning.java.game.rest.request.TurnGameRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("gameServiceTwoBean")
public class GameServiceTwo implements GameService {

    @Autowired
    @Qualifier("gameControllerTwoBean")
    private GameController gameControl;

    @Autowired
    private GamesDao dao;

    @Autowired
    private PlayersDao playersDao;

    @Override
    public Game getGameFromId(String id) {
        UUID key = UUID.fromString(id);
        Game game = dao.read(key);
        if(game == null || game.getId() == null)
            throw new NotFoundExceptions("Not found game with this id: " + key);

        return game;
    }

    @Override
    public Game postNewGame(CreateGameRequest createGameRequest) {
        Figure postFigure = createGameRequest.getSidePlayer1();
        Player player1 = playersDao.read(createGameRequest.getPlayerId1());
        Player player2 = playersDao.read(createGameRequest.getPlayerId2());
        if (postFigure == null || player1 == null || player2 == null)
            throw new IllegalArgumentException("Sent request with incorrect body. " +
                    "With incorrect key, try \"sidePlayer1:\", \"playerId1:\", \"playerId2:\"");

        Game game = gameControl.newGame(postFigure, player1, player2);
        game.setTurn(gameControl.currentFigure(game.getField()));
        dao.create(game);
        return game;
    }

    @Override
    public Game turnGameFromId(TurnGameRequest turnGameRequest, String id) {
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
