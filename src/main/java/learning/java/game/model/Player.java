package learning.java.game.model;

import java.util.UUID;

public class Player {

    private UUID id;

    private String name = "godOfXO";

    public Player() {
    }

    public Player(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

}