package learning.java.game.controller;

import learning.java.game.model.*;
import org.springframework.stereotype.Component;

@Component("gameControllerTwoBean")
public class GameControllerTwo implements GameController{

    @Override
    public Game newGame(Figure figure, Player player1, Player player2) {
        return new Game(){{
            setType("twoPlayer");
            setName("XO");
            setPlayer1(new PlayerFigure(){{
                setPlayer(player1);
                setFigure(figure);
            }});
            setPlayer2(new PlayerFigure(){{
                setPlayer(player2);
                setFigure(oppositeFig(figure));
            }});
            setField(new Field(3));
            setTurn(Figure.X);
        }};
    }

    private Figure oppositeFig(Figure figure) {
        return Figure.X == figure ? Figure.O : Figure.X;
    }

    @Override
    public void letsPlay(Game game, Point point) {
        Field field = game.getField();
        Figure figure = game.getTurn();
        game.setTurn(oppositeFig(figure));
        field.setFigure(point, figure);
        checkAndSetWinner(game);
    }

    @Override
    public Figure currentFigure(Field field) {
        if (field.getCounterFigure() % 2 == 1) return Figure.O;
        return Figure.X;
    }




}
