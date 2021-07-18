package server.repository;

import server.DatabaseConnection;

import java.sql.ResultSet;

public class UserRepository {

    public static final String SELECT_USERNAME_PASSWORD = "SELECT username, password " +
            "FROM accounts " +
            "WHERE username = ? " +
            "AND password = ?;";

    public static ResultSet getByUsernameAndPassword(DatabaseConnection connection, String username, String password) {
        return connection.executeQuery(SELECT_USERNAME_PASSWORD, username, password);

    }
}
