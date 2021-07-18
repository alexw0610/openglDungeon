package server;

import server.worker.AuthenticationServer;
import server.worker.UpdateServer;
import util.ApplicationProperties;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

public class Server {

    private static final ApplicationProperties applicationProperties = new ApplicationProperties();

    public static void main(String[] args) {
        setSystemProperties();
        setupDatabase();

        AuthenticationServer authenticationServer = new AuthenticationServer();
        new Thread(authenticationServer).start();

        UpdateServer updateServer = new UpdateServer();
        new Thread(updateServer).start();

    }

    private static void setupDatabase() {
        String url = applicationProperties.getProperty("dbUrl");
        String user = applicationProperties.getProperty("dbUser");
        String password = applicationProperties.getProperty("dbPassword");
        DatabaseConnection databaseConnection = new DatabaseConnection(url, user, password);
        File databaseResourceDir = new File(applicationProperties.getResourceRootPath() + "/database");
        for (File file : databaseResourceDir.listFiles()) {
            try {
                String content = new String(Files.readAllBytes(Paths.get(file.getPath())));
                databaseConnection.executeUpdate(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            databaseConnection.close();
        } catch (SQLException e) {
            System.err.println("Error while gracefully trying to close DB connection!");
            e.printStackTrace();
        }
    }

    private static void setSystemProperties() {
        System.setProperty("javax.net.ssl.keyStore", applicationProperties.getResourceRootPath() + applicationProperties.getProperty("keyStore"));
        System.setProperty("javax.net.ssl.keyStorePassword", applicationProperties.getProperty("keyStorePassword"));
    }
}
