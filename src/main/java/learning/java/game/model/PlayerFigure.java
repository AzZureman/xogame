package learning.java.game.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PlayerFigure {

    @JsonIgnore
    private int id;

    private Player player;

    private Figure figure;

    public PlayerFigure(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Figure getFigure() {
        return figure;
    }

    public void setFigure(Figure figure) {
        this.figure = figure;
    }
}
