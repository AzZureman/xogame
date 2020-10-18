package learning.java.game.exception;

import learning.java.game.model.Game;

public class GameOverException extends RuntimeException {
    private final Game game;

    public GameOverException(final Game game) {
        this.game = game;
    }

    public Game getGame(){
        return game;
    }

}
