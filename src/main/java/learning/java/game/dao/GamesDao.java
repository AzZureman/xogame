package learning.java.game.dao;

import learning.java.game.model.*;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.UUID;

@Component
public class GamesDao implements Dao<Game, UUID> {

    @Override
    public UUID create(Game game) throws SQLException {
        UUID id = UUID.randomUUID();
        game.setId(id);
        try (Connection connection = getConnection()) {
            return execute(SQLGames.INSERT, connection, statement -> {
                statement.setObject(1, id);
                statement.setString(2, game.getType());
                statement.setString(3, game.getName());
                statement.setString(4, game.getTurn().toString());
                if (game.getWinner() == null)
                    statement.setString(5, null);
                else
                    statement.setString(5, game.getWinner().toString());
                statement.setInt(6, createFields(game.getField(), connection));
                statement.executeQuery();

                createPlayersFigure(game.getPlayer1(), id, connection);
                createPlayersFigure(game.getPlayer2(), id, connection);
                return id;
            });
        }
    }

    private int createPlayersFigure(PlayerFigure player, UUID gameId, Connection connection) throws SQLException {
        return execute(SQLGames.INSERT_PLAYERS_FIGURE, connection, statement -> {
            statement.setObject(1, player.getPlayer().getId());
            statement.setString(2, player.getFigure().toString());
            statement.setObject(3,gameId);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt("id");
        });
    }

    private int createFields(Field field, Connection connection) throws SQLException {
        return execute(SQLGames.INSERT_FIELDS, connection, statement -> {
            Array array = connection.createArrayOf("varchar", field.getFigures());
            statement.setInt(1, field.getSize());
            statement.setArray(2, array);
            ResultSet result = statement.executeQuery();
            result.next();
            return result.getInt("id");
        });
    }

    @Override
    public Game read(UUID uuid) throws SQLException {
        Game game = new Game();
        int fieldId = 0;
        try (Connection connection = getConnection()){
            return execute(SQLGames.SELECT, connection, statement -> {
                statement.setObject(1, uuid);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()){
                    game.setId((UUID) resultSet.getObject("game_id"));
                    game.setName(resultSet.getString("name"));
                    game.setType(resultSet.getString("type"));
                    game.setTurn(Figure.figureFromString(resultSet.getString("turn")));
                    game.setWinner(Figure.figureFromString(resultSet.getString("winner")));
                    game.setField(readFields(resultSet.getInt("field_id"), connection));
                    if (game.getPlayer1() == null) //first iteration
                        game.setPlayer1(readPlayerFigures(resultSet, connection));
                    else //second iteration
                        game.setPlayer2(readPlayerFigures(resultSet,connection));
                }
                return game;
            });
        }
    }

    private PlayerFigure readPlayerFigures(ResultSet resultSet, Connection connection) throws SQLException {
        return new PlayerFigure(){{
            setId(resultSet.getInt("id"));
            setPlayer(readPlayer((UUID) resultSet.getObject("player_id"), connection ));
            setFigure(Figure.figureFromString(resultSet.getString("figure")));
        }};
    }

    private Player readPlayer(UUID id, Connection connection) throws SQLException {
        return execute(SQLGames.SELECT_PLAYER, connection, statement -> {
            statement.setObject(1, id);
            ResultSet resultSet1 = statement.executeQuery();
            resultSet1.next();
            return new Player(){{
                setId(UUID.fromString(resultSet1.getString("id")));
                setName(resultSet1.getString("name"));
            }};
        });
    }

    private Field readFields(int id, Connection connection) throws SQLException {
        final Field[] field = new Field[1];
        return execute(SQLGames.SELECT_FIELDS, connection, statement -> {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            field[0] = new Field(resultSet.getInt("size"));
            field[0].setId(resultSet.getInt("id"));

            Array a = resultSet.getArray("figures");
            field[0].setFigures(arrayFiguresFromString((String[][]) a.getArray()));
            return field[0];
        });
    }

    @Override
    public UUID update(Game game) throws SQLException {
        final UUID id = game.getId();
        try (Connection connection = getConnection()) {
            return  execute(SQLGames.UPDATE, connection, statement ->{
                statement.setString(1, game.getTurn().toString());

                Figure winner = game.getWinner();
                statement.setString(2, winner == null ? null : winner.toString());
                statement.setObject(3, game.getId());
                statement.executeQuery();
                updateField(game, connection);
                return id;
            });
        }
    }

    private int updateField(Game game, Connection connection) throws SQLException {
        int id = game.getField().getId();
        return execute(SQLGames.UPDATE_FIELDS, connection, statement -> {
            Array array = connection.createArrayOf("varchar", game.getField().getFigures());
            statement.setArray(1,array);
            statement.setInt(2, id);
            statement.executeQuery();
            return id;
        });
    }

    @Override
    public boolean delete(Game game) throws SQLException {
        UUID id = game.getId();
        try(Connection connection = getConnection()) {
            return execute(SQLGames.DELETE, connection, statement -> {
                statement.setObject(1, id);
                deletePlayerFigure(id, connection);
                return statement.executeQuery().next();
            });
        }
    }

    private boolean deletePlayerFigure(UUID id, Connection connection) throws SQLException {
        return execute(SQLGames.DELETE_PLAYERS_FIGURE, connection, statement -> {
            statement.setObject(1, id);
            return statement.executeQuery().next();
        });
    }

    private Connection getConnection() throws SQLException {
        return DataConnection.get();
    }

    private <R> R execute(SQLGames sql,
                          Connection connection,
                          SQLFunction<PreparedStatement, R> statementTFunction)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql.QUERY)) {
            return statementTFunction.apply(statement);
        }
    }

    private Figure[][] arrayFiguresFromString (String[][] strArray) {
        if(!(isArrayRectangular(strArray)))
            throw new IllegalArgumentException("The array not is rectangular");
        int length = strArray.length;
        int width = strArray[0].length;
        Figure[][] resultFigure = new Figure[length][width];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                resultFigure[i][j] = Figure.figureFromString(strArray[i][j]);
            }
        }
        return resultFigure;
    }

    private boolean isArrayRectangular(Object[][] objects) {
        int size = objects[0].length;
        for (int i = 1; i < objects.length; i++)
            if (objects[i].length != size)
                return false;
        return true;
    }

    private enum SQLGames {
        INSERT("INSERT INTO games (id, type, name, turn, winner, field_id) " +
                "VALUES (?,?,?,?,?,?) RETURNING id"),
        INSERT_PLAYERS_FIGURE("INSERT INTO player_figures (player_id ,figure, game_id)" +
                " VALUES (?,?,?) RETURNING id"),
        INSERT_FIELDS("INSERT INTO fields(size, figures) VALUES (?, ?) RETURNING id"),
        SELECT("SELECT * FROM player_figures AS p LEFT JOIN games AS g ON g.id = p.game_id WHERE g.id = (?)"),
        SELECT_PLAYER("SELECT * FROM players WHERE id = ?"),
        SELECT_FIELDS("SELECT * FROM fields WHERE id = ?"),
        UPDATE("UPDATE games SET turn = (?), winner = (?) WHERE id = (?) RETURNING id"),
        UPDATE_FIELDS("UPDATE fields SET figures = ? WHERE id = ? RETURNING id"),
        DELETE("DELETE FROM games  WHERE id = ? RETURNING id"),
        DELETE_PLAYERS_FIGURE("DELETE FROM player_figures WHERE game_id = ? RETURNING id");

        String QUERY;

        SQLGames(String QUERY) {
            this.QUERY = QUERY;
        }
    }
}
