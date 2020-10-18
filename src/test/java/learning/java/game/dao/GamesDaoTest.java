package learning.java.game.dao;

import learning.java.game.controller.GameControllerSingle;
import learning.java.game.model.Figure;
import learning.java.game.model.Game;
import learning.java.game.model.Point;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GamesDaoTest {

    private final PlayersDao playersDao = new PlayersDao();

    private final GamesDao dao = new GamesDao();

    private final GameControllerSingle gameController = new GameControllerSingle();

    private final LinkedList<UUID> gameKeySet = new LinkedList<>();

    @Test
    void create() throws SQLException {
        Game game = gameController.newGame(Figure.X);
        playersDao.create(game.getPlayer1().getPlayer());
        playersDao.create(game.getPlayer2().getPlayer());
        gameKeySet.add(dao.create(game));
    }

    @Test
    void read() throws SQLException {
        create();
        Game game =  dao.read(gameKeySet.getLast());

    }

    @Test
    void update() throws  SQLException {
        create();
        Game game = dao.read(gameKeySet.getLast());
        game.setWinner(Figure.O);
        game.getField().setFigure(new Point(1, 0), Figure.X);
        dao.update(game);
        System.out.println(game.getField().toString());
    }

    @Test
    void delete() throws SQLException {
        create();
        UUID id = gameKeySet.getLast();
        Game game = dao.read(id);

        System.out.println(dao.read(id).getId());
        dao.delete(game);
        System.out.println(dao.read(id).getId());
    }
}