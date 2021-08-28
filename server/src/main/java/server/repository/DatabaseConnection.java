package server.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class DatabaseConnection {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseConnection.class);
    private Connection connection;

    public DatabaseConnection(String url, String user, String password) {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            LOG.error("Failed to create database connection {}", e.getMessage());
        }
    }

    public void close() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            LOG.warn("Error while gracefully trying to close DB connection! {}", e.getMessage());
        }
    }

    public ResultSet execute(String query, Object... args) {
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            setArguments(statement, args);
            return statement.executeQuery();
        } catch (SQLException e) {
            LOG.warn("Error while executing query! Query:\"{}\"\n Error:{}", query, e.getMessage());
            return null;
        }
    }

    public boolean executeUpdate(String query, Object... args) {
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            setArguments(statement, args);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            LOG.warn("Error while executing update! Query:\"{}\"\n Error:{}", query, e.getMessage());
            return false;
        }
    }

    public void execute(String query) {
        try {
            this.connection.createStatement().executeUpdate(query);
        } catch (SQLException e) {
            LOG.warn("Error while executing Update! Query:\"{}\"\n Error:{}", query, e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    private static void setArguments(PreparedStatement statement, Object... args) throws SQLException {
        short count = 1;
        for (Object arg : args) {
            statement.setObject(count, arg);
            count++;
        }

    }
}
