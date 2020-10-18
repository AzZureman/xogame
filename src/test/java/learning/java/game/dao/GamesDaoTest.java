package learning.java.game.dao;

import learning.java.game.controller.GameControllerSingle;
import learning.java.game.model.*;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.UUID;

class GamesDaoTest {

    private final PlayersDao playersDao = new PlayersDao();

    private final GamesDao dao = new GamesDao();

    private final GameControllerSingle gameController = new GameControllerSingle();

    private final LinkedList<UUID> gameKeySet = new LinkedList<>();

    @Test
    void create() {
        Player player = new Player("Max");
        playersDao.create(player);
        Game game = gameController.newGame(Figure.X, player, null);
        gameKeySet.add(dao.create(game));
    }

    @Test
    void read() {
        create();
        Game game =  dao.read(gameKeySet.getLast());

    }

    @Test
    void update() {
        create();
        Game game = dao.read(gameKeySet.getLast());
        game.setWinner(Figure.O);
        game.getField().setFigure(new Point(1, 0), Figure.X);
        dao.update(game);
        System.out.println(game.getField().toString());
    }

    @Test
    void delete() {
        create();
        UUID id = gameKeySet.getLast();
        Game game = dao.read(id);

        dao.delete(game);
        dao.read(id);
        dao.read(id);
    }
}