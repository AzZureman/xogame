package learning.java.game.rest.item;

import learning.java.game.model.Game;
import org.springframework.stereotype.Component;

@Component
public class GameMapping {

    public Game gameFromGameTO(final GameTO gameTO) {
        return  new Game(){{
            setId(gameTO.getId());
            setName(gameTO.getName());
            setType(gameTO.getType());
            setPlayer1(gameTO.getPlayer1());
            setPlayer2(gameTO.getPlayer2());
            setField(gameTO.getField());
            setTurn(gameTO.getTurn());
            setWinner(gameTO.getWinner());
        }};
    }

    public GameTO gameTOFromGame(final Game game) {
        return  new GameTO(){{
            setId(game.getId());
            setName(game.getName());
            setType(game.getType());
            setPlayer1(game.getPlayer1());
            setPlayer2(game.getPlayer2());
            setField(game.getField());
            setTurn(game.getTurn());
            setWinner(game.getWinner());
        }};
    }
}
