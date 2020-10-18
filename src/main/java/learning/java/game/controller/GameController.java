package learning.java.game.controller;

import learning.java.game.model.Field;
import learning.java.game.model.Figure;
import learning.java.game.model.Game;
import learning.java.game.model.Point;

public interface GameController {

    Game newGame(Figure figure);

    Figure letsPlay(Game game, Point point);

    Figure currentFigure(final Field field);

}
