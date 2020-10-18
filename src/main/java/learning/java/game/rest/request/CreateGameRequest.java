package learning.java.game.rest.request;

import learning.java.game.model.Figure;
import learning.java.game.model.Player;

import java.util.UUID;

public class CreateGameRequest {

    private Figure sidePlayer1;

    private UUID playerId1;

    private UUID playerId2;

    public Figure getSidePlayer1() {
        return sidePlayer1;
    }

    public void setSidePlayer1(Figure sidePlayer1) {
        this.sidePlayer1 = sidePlayer1;
    }

    public UUID getPlayerId1() {
        return playerId1;
    }

    public void setPlayerId1(UUID playerId1) {
        this.playerId1 = playerId1;
    }

    public UUID getPlayerId2() {
        return playerId2;
    }

    public void setPlayerId2(UUID playerId2) {
        this.playerId2 = playerId2;
    }
}
