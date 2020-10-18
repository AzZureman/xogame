package learning.java.game.dao;

import learning.java.game.controller.GameControllerSingle;
import learning.java.game.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.UUID;

@Component
public class GamesDao implements Dao<Game, UUID> {

    @Autowired
    private DataConnection dataConnection;

    @Override
    public UUID create(Game game) {
        UUID id = UUID.randomUUID();
        game.setId(id);
            return   execute(SQLGames.INSERT, (connection, statement) -> {
                statement.setObject(1, id);
                statement.setString(2, game.getType());
                statement.setString(3, game.getName());
                statement.setString(4, game.getTurn().toString());
                if (game.getWinner() == null)
                    statement.setString(5, null);
                else
                    statement.setString(5, game.getWinner().toString());
                statement.setInt(6, createFields(game.getField()));
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();

                createPlayersFigure(game.getPlayer1(), id);
                return UUID.fromString(resultSet.getString("id"));
            });
    }

    private int createPlayersFigure(PlayerFigure player, UUID gameId) {
        return execute(SQLGames.INSERT_PLAYERS_FIGURE, (connection, statement) -> {
            statement.setObject(1, player.getPlayer().getId());
            statement.setString(2, player.getFigure().toString());
            statement.setObject(3,gameId);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt("id");
        });
    }

    private int createFields(Field field) {
        return execute(SQLGames.INSERT_FIELDS, (connection, statement) -> {
            Array array = connection.createArrayOf("varchar", field.getFigures());
            statement.setInt(1, field.getSize());
            statement.setArray(2, array);
            ResultSet result = statement.executeQuery();
            result.next();
            return result.getInt("id");
        });
    }

    @Override
    public Game read(UUID uuid) {
        GameControllerSingle controllerSingle = new GameControllerSingle();
            return  execute(SQLGames.SELECT, (connection, statement) -> {
                statement.setObject(1, uuid);
                ResultSet resultSet = statement.executeQuery();
                Game game = null;
                while (resultSet.next()) {
                    game = controllerSingle
                            .newGame(Figure.figureFromString(resultSet.getString("figure")), null, null);
                    game.setId((UUID) resultSet.getObject("game_id"));
                    game.setName(resultSet.getString("name"));
                    game.setType(resultSet.getString("type"));
                    game.setTurn(Figure.figureFromString(resultSet.getString("turn")));
                    game.setWinner(Figure.figureFromString(resultSet.getString("winner")));
                    game.setField(readFields(resultSet.getInt("field_id")));
                    game.setPlayer1(readPlayerFigures(resultSet, connection));
                }
                return game;
            });
    }

    private PlayerFigure readPlayerFigures(ResultSet resultSet, Connection connection) {
        return new PlayerFigure(){{
            try {
                setId(resultSet.getInt("id"));
                setPlayer(readPlayer((UUID) resultSet.getObject("player_id")));
                setFigure(Figure.figureFromString(resultSet.getString("figure")));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }};
    }

    private Player readPlayer(UUID id) {
        return execute(SQLGames.SELECT_PLAYER, (connection, statement) -> {
            statement.setObject(1, id);
            ResultSet resultSet1 = statement.executeQuery();
            resultSet1.next();
            return new Player(resultSet1.getString("name")){{
                setId(UUID.fromString(resultSet1.getString("id")));
            }};
        });
    }

    private Field readFields(int id) {
        final Field[] field = new Field[1];

        return execute(SQLGames.SELECT_FIELDS, (connection, statement) -> {
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
    public UUID update(Game game) {
        final UUID id = game.getId();
            return  execute(SQLGames.UPDATE, (connection, statement) -> {
                statement.setString(1, game.getTurn().toString());
                Figure winner = game.getWinner();
                statement.setString(2, winner == null ? null : winner.toString());
                statement.setObject(3, game.getId());
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                updateField(game);
                return UUID.fromString(resultSet.getString("id"));
            });
    }

    private int updateField(Game game) {
        int id = game.getField().getId();
        return execute(SQLGames.UPDATE_FIELDS, (connection, statement) -> {
            Array array = connection.createArrayOf("varchar", game.getField().getFigures());
            statement.setArray(1,array);
            statement.setInt(2, id);
            statement.executeQuery();
            return id;
        });
    }

    @Override
    public boolean delete(Game game) {
        UUID id = game.getId();
        return execute(SQLGames.DELETE, (connection, statement) -> {
                statement.setObject(1, id);
                deletePlayerFigure(id);
                return statement.executeQuery().next();
            });
    }

    private boolean deletePlayerFigure(UUID id){
        return execute(SQLGames.DELETE_PLAYERS_FIGURE, (connection, statement) -> {
            statement.setObject(1, id);
            return statement.executeQuery().next();
        });
    }

    private Connection getConnection(){
       return dataConnection.get();
    }

    private <R> R execute(SQLGames sql,
                          SQLBiFunction<Connection, PreparedStatement, R> statementTFunction) {
        R result = null;
        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql.QUERY)) {
            result = statementTFunction.apply(connection, statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
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
