package learning.java.game.controller;

import learning.java.game.exception.GameOverException;
import learning.java.game.model.*;

import java.util.function.BiFunction;

public interface GameController {

    Game newGame(Figure figure, Player player1, Player player2);

    void letsPlay(Game game, Point point);

    Figure currentFigure(final Field field);

    default  Figure checkAndSetWinner(final Game game) {
        final Figure figure = checkLineWinner(game);
        if (figure != null) {
            game.setWinner(figure);
            throw new GameOverException(game);
        }
        return null;
    }

    default Figure checkLineWinner(final Game game) {
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

    default Figure checkLine(final Game game, int i, final BiFunction<Integer, Integer, Point> biFunction) {
        int counterO = 0;
        int counterX = 0;
        Field field = game.getField();
        int fieldSize = field.getSize();
        for (int j = 0; j < fieldSize; j++) {
            Point point = biFunction.apply(i, j);
            if ((Figure.X).equals(field.getFigure(point)))
                counterX++;
            if ((Figure.O).equals(field.getFigure(point)))
                counterO++;
            if (counterX == fieldSize)
                return Figure.X;
            if (counterO == fieldSize)
                return Figure.O;
        }
        return null;
    }

}
