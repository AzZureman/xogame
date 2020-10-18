package learning.java.game.dao;

import learning.java.game.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class PlayersDao implements Dao<Player, UUID> {

    @Autowired
    private DataConnection dataConnection;

    @Override
    public UUID create(Player player) {
        return execute(SQLPlayer.INSERT, statement -> {
            statement.setObject(1, player.getId());
            statement.setString(2, player.getName());

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return UUID.fromString(resultSet.getString("id"));
        });
    }

    @Override
    public Player read(UUID uuid) {
        final Player player = new Player("");
        return execute(SQLPlayer.SELECT, statement -> {
            statement.setObject(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                player.setId((UUID) resultSet.getObject("id"));
                player.setName(resultSet.getString("name"));
            }
            return player;
        });
    }

    @Override
    public UUID update(Player player) {
        return execute(SQLPlayer.UPDATE, statement -> {
            statement.setObject(2,player.getId());
            statement.setString(1, player.getName());
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return (UUID) resultSet.getObject("id");
        });
    }

    @Override
    public boolean delete(Player player) {
        return execute(SQLPlayer.DELETE, statement -> {
            statement.setObject(1, player.getId());
            return statement.executeQuery().next();
        });
    }

    private <R> R execute(SQLPlayer sql,
                          SQLFunction<PreparedStatement, R> statementRSQLFunction) {
        R result = null;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.QUERY)) {
            result = statementRSQLFunction.apply(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Connection getConnection() {
        return dataConnection.get();
    }

    private enum SQLPlayer {
        INSERT("INSERT INTO players(id, name) VALUES (?,?) RETURNING id"),
        SELECT("SELECT * FROM players WHERE id = ?"),
        UPDATE("UPDATE players SET name = ? WHERE id = ? RETURNING id"),
        DELETE("DELETE FROM players WHERE id = ? RETURNING id");

        String QUERY;

        SQLPlayer(String QUERY) {
            this.QUERY = QUERY;
        }
    }
}

