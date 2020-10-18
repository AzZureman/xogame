package learning.java.game.controller;

import learning.java.game.model.*;

public interface GameController {

    Game newGame(Figure figure, Player player1, Player player2);

    Figure letsPlay(Game game, Point point);

    Figure currentFigure(final Field field);

}
