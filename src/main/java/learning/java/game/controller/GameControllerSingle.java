package learning.java.game.controller;

import learning.java.game.model.*;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component("gameControllerSingleBean")
public class GameControllerSingle implements GameController {

    public Game newGame(Figure figure, Player player1, Player nullPlayer) {
        return new Game(){{
            setType("singlePlayer");
            setName("XO");
            setPlayer1(new PlayerFigure(){{
                setPlayer(player1);
                setFigure(figure);
            }});
            setPlayer2(getAIPlayer(figure));
            setField(new Field(3));
            setTurn(Figure.X);
        }};
    }

    private Figure oppositeFig(Figure figure) {
        return Figure.X == figure ? Figure.O : Figure.X;
    }

    /**
     *
     * @param playersFigure must be figure which play present player,
     *                      because AI player take opposite figure
     */
    public PlayerFigure getAIPlayer(final Figure playersFigure){
        return new PlayerFigure(){{
            setPlayer( new Player("AI"){{
               setId(null);
            }});
            setFigure(oppositeFig(playersFigure));
        }};
    }

    public void letsPlay(Game game, Point point) {
        Field field = game.getField();
        if (game.getPlayer1().getFigure() == Figure.X) {
            applyFigure(field, point);
            randomMove(field);
        } else {
            randomMove(field);
            applyFigure(field, point);
        }
        checkAndSetWinner(game);
    }

    private void randomMove(final Field field) {
        Random random = new Random();
        Figure figure = currentFigure(field);
        int size = field.getSize();
        Point point = new Point(random.nextInt(size), random.nextInt(size));

        try {
            field.setFigure(point, figure);
        } catch (IllegalArgumentException e) {
            randomMove(field);
        }
    }

    private void applyFigure(final Field field, final Point point) {
        field.setFigure(point, currentFigure(field));
    }

    public Figure currentFigure(final Field field) {
        if (field.getCounterFigure() % 2 == 1) return Figure.O;
        return Figure.X;
    }
}
