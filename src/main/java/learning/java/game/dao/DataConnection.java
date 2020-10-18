package learning.java.game.dao;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DataConnection {

    private static final String user = "postgres";

    private static final String password = "90224";

    private static final String url = "jdbc:postgresql://localhost:5432/xogame";

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
