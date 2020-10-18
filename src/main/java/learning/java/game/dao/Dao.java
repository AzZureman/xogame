package learning.java.game.dao;

import java.sql.SQLException;

public interface Dao<Entity, Key> {

    Key create(Entity entity) throws SQLException;

    Entity read(Key key) throws SQLException;

    Key update(Entity entity) throws SQLException;

    boolean delete(Entity entity) throws SQLException;

}
