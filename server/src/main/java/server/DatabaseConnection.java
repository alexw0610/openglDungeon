package server;

import java.sql.*;

public class DatabaseConnection {
    private Connection connection;

    public DatabaseConnection(String url, String user, String password) {
        try {
            connection = DriverManager.getConnection(url, user, password);

            if (connection != null) {
                System.out.println("Connected to the PostgreSQL server successfully.");
            } else {
                System.out.println("Failed to make connection!");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void close() throws SQLException {
        this.connection.close();
    }

    public ResultSet executeQuery(String query, String... arg) {
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            for (int i = 0; i < arg.length; i++) {
                statement.setString(i + 1, arg[i]);
            }
            return statement.executeQuery();
        } catch (SQLException throwables) {
            System.err.println("Error while executing Query!");
            throwables.printStackTrace();
            return null;
        }
    }

    public ResultSet executeQuery(String query, int arg) {
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            statement.setInt(1, arg);
            return statement.executeQuery();
        } catch (SQLException throwables) {
            System.err.println("Error while executing Query!");
            throwables.printStackTrace();
            return null;
        }
    }

    public void executeUpdate(String sql) {
        try {
            this.connection.createStatement().executeUpdate(sql);
        } catch (SQLException throwables) {
            System.err.println("Error while executing Query!");
            throwables.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
