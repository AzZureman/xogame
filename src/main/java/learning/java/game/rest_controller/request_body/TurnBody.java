package learning.java.game.rest_controller.request_body;

public class TurnBody {

    private int[] position;

    public int[] getPosition() {
        return position;
    }

    public void setPosition(int[] position) {
        this.position = position;
    }

    public int getX() {
        return position[0];
    }

    public int getY() {
        return position[1];
    }

}