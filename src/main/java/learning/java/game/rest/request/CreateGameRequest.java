package learning.java.game.rest.request;

import learning.java.game.model.Figure;
import learning.java.game.model.Player;

public class CreateGameRequest {

    private Figure side;

    private Player player1;

    private Player player2;

    public Figure getSide() {
        return side;
    }

    public void setSide(Figure side) {
        this.side = side;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }
}
