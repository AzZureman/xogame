package learning.java.game.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import learning.java.game.model.Figure;
import learning.java.game.model.Game;
import learning.java.game.model.PlayerFigure;

public class GameOverMessage {

    @JsonIgnore
    private final Game game;

    private String gameId;

    private PlayerFigure winner;

    private String fields;

    private String message;

    public GameOverMessage(final Game game) {
        this.game = game;
        if (game == null)
            return;
        gameId = game.getId().toString();
        winner = returnWinPlayer(game);
        fields = game.getField().toString();
        message = " Game is over! To continue you need start a new game!";
    }

    private PlayerFigure returnWinPlayer(final Game game) {
        final PlayerFigure player = game.getPlayer1();
        final Figure figure = game.getWinner();
        return player.getFigure() == figure ? player : game.getPlayer2();
    }

    public Game getGame() {
        return game;
    }

    public String getGameId() {
        return gameId;
    }

    public PlayerFigure getWinner() {
        return winner;
    }

    public String getFields() {
        return fields;
    }

    public String getMessage() {
        return message;
    }
}
