package learning.java.game.dao;

public interface Dao<Entity, Key> {

    Key create(Entity entity) ;

    Entity read(Key key) ;

    Key update(Entity entity) ;

    boolean delete(Entity entity) ;

}
