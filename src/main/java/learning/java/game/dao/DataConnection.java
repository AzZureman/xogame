package learning.java.game.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DataConnection  {

    private String user;

    private String password;

    private String url;

    @Autowired
    public DataConnection(@Value("${user}") String user,
                           @Value("${password}") String password,
                           @Value("${url}") String url){
        this.user = user;
        this.password = password;
        this.url = url;
    }

    public Connection get() {
        Connection connection = null;
        try {
            connection =  DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
