package learning.java.game.controller;

import learning.java.game.exception.GameOverException;
import learning.java.game.model.*;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.function.BiFunction;

@Component
public class GameControllerSingle implements GameController {

    public Game newGame(Figure figure, Player player1, Player player2) {
        return new Game(){{
            setType("singlePlayer");
            setName("XO");
            setPlayer1(new PlayerFigure(){{
                setPlayer(player1);
                setFigure(figure);
            }});
            setPlayer2(new PlayerFigure(){{
                setPlayer(new Player("AI"){{
                    setId(null);
                }});
                setFigure(oppositeFig(figure));
            }});
            setField(new Field(3));
            setTurn(Figure.X);
        }};
    }

    private Figure oppositeFig(Figure figure) {
        return Figure.X == figure ? Figure.O : Figure.X;
    }

    public Figure letsPlay(Game game, Point point) {
        Field field = game.getField();
        if (game.getPlayer1().getFigure() == Figure.X) {
            applyFigure(field, point);
            randomMove(field);
            game.setWinner(checkAndSetWinner(game));
            return game.getWinner();
        }
        randomMove(field);
        applyFigure(field, point);
        game.setWinner(checkAndSetWinner(game));
        return game.getWinner();
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

    private final Figure X = Figure.X;
    private final Figure O = Figure.O;

    private Figure checkAndSetWinner(final Game game) {
        final Figure figure = checkLineWinner(game);
        if (figure != null) {
            game.setWinner(figure);
            throw new GameOverException(game);
        }
        return null;
    }

    private Figure checkLineWinner(final Game game) {
        int fieldSize = game.getField().getSize();
        for (int i = 0; i < fieldSize; i++) {
            final Figure figure = checkLine(game, i, (x, y) -> new Point(x, y));
            if (figure != null)
                return figure;
        }

        for (int i = 0; i < fieldSize; i++) {
            final Figure figure2 = checkLine(game, i, (x, y) -> new Point(y, x));
            if (figure2 != null)
                return figure2;
        }

        final Figure figure3 = checkLine(game, -1, (x, y) -> new Point(y, y));
        if (figure3 != null)
            return figure3;

        final Figure figure4 = checkLine(game, fieldSize - 1, (x, y) -> new Point(x-y, y));
        if (figure4 != null)
            return figure4;

       return null;
    }

    private Figure checkLine(final Game game, int i, final BiFunction<Integer, Integer, Point> biFunction) {
        int counterO = 0;
        int counterX = 0;
        Field field = game.getField();
        int fieldSize = field.getSize();
        for (int j = 0; j < fieldSize; j++) {
            Point point = biFunction.apply(i, j);
            if ((X).equals(field.getFigure(point)))
                counterX++;
            if ((O).equals(field.getFigure(point)))
                counterO++;
            if (counterX == fieldSize)
                return X;
            if (counterO == fieldSize)
                return O;
        }
        return null;
    }
}
