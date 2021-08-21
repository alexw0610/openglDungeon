package server.repository;

import server.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    public static final String SELECT_USERNAME_PASSWORD = "SELECT user_account_id, username, password " +
            "FROM accounts " +
            "WHERE username = ? " +
            "AND password = ?;";

    public static final String INSERT_CONNECTED_USER = "INSERT INTO CONNECTED_ACCOUNTS " +
            "(user_account_id, connected_ip_address, connected_udp_port) " +
            "VALUES " +
            "(?, ?, ?)" +
            "ON CONFLICT (user_account_id) " +
            "DO UPDATE SET " +
            "connected_ip_address = EXCLUDED.connected_ip_address, " +
            "connected_udp_port = EXCLUDED.connected_udp_port;";

    public static ResultSet getByUsernameAndPassword(DatabaseConnection connection, String username, String password) {
        return connection.executeQuery(SELECT_USERNAME_PASSWORD, username, password);
    }

    public static boolean setUserAccountActive(DatabaseConnection connection, String user_account_id, String ip_address, String udp_port) {
        try {
            PreparedStatement statement = connection.getConnection().prepareStatement(INSERT_CONNECTED_USER);
            statement.setInt(1, Integer.parseInt(user_account_id));
            statement.setString(2, ip_address);
            statement.setString(3, udp_port);
            statement.executeUpdate();
            return true;
        } catch (SQLException throwables) {
            System.err.println("Error while executing Query!");
            throwables.printStackTrace();
            return false;
        }
    }
}
