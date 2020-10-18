package learning.java.game.rest.item;

import learning.java.game.model.Field;
import learning.java.game.model.Figure;
import learning.java.game.model.PlayerFigure;

import java.util.UUID;

public class GameTO {

    private UUID id;

    private String type;

    private String name;

    private PlayerFigure player1;

    private PlayerFigure player2;

    private Field field;

    private Figure turn;

    private Figure winner;

    public GameTO() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlayerFigure getPlayer1() {
        return player1;
    }

    public void setPlayer1(PlayerFigure player1) {
        this.player1 = player1;
    }

    public PlayerFigure getPlayer2() {
        return player2;
    }

    public void setPlayer2(PlayerFigure player2) {
        this.player2 = player2;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Figure getTurn() {
        return turn;
    }

    public void setTurn(Figure turn) {
        this.turn = turn;
    }

    public Figure getWinner() {
        return winner;
    }

    public void setWinner(Figure winner) {
        this.winner = winner;
    }
}
