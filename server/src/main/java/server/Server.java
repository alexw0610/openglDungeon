package server;

import server.worker.ClientUpdateProcessor;
import server.worker.ClientUpdateSender;
import server.worker.SSLClientConnectionServer;
import udp.UpdateListener;
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

        UpdateListener clientUpdateListener = new UpdateListener();
        new Thread(clientUpdateListener).start();

        ClientUpdateProcessor clientUpdateProcessor = new ClientUpdateProcessor(clientUpdateListener.receivedUpdates);
        new Thread(clientUpdateProcessor).start();

        ClientUpdateSender clientUpdateSender = new ClientUpdateSender();
        new Thread(clientUpdateSender).start();

        SSLClientConnectionServer sslClientConnectionServer = new SSLClientConnectionServer(clientUpdateListener.getPort());
        new Thread(sslClientConnectionServer).start();
    }

    private static void setSystemProperties() {
        System.setProperty("javax.net.ssl.keyStore", applicationProperties.getResourceRootPath() + applicationProperties.getProperty("keyStore"));
        System.setProperty("javax.net.ssl.keyStorePassword", applicationProperties.getProperty("keyStorePassword"));
    }

    private static void setupDatabase() {
        String url = applicationProperties.getProperty("dbUrl");
        String user = applicationProperties.getProperty("dbUser");
        String password = applicationProperties.getProperty("dbPassword");
        DatabaseConnection databaseConnection = new DatabaseConnection(url, user, password);
        processFilesInDirectory("function", databaseConnection);
        processFilesInDirectory("table", databaseConnection);
        processFilesInDirectory("foreignKey", databaseConnection);
        try {
            databaseConnection.close();
        } catch (SQLException e) {
            System.err.println("Error while gracefully trying to close DB connection!");
            e.printStackTrace();
        }
    }

    private static void processFilesInDirectory(String folderName, DatabaseConnection databaseConnection) {
        File resourceDir = new File(applicationProperties.getResourceRootPath() + "/database/" + folderName);
        for (File file : resourceDir.listFiles()) {
            try {
                String content = new String(Files.readAllBytes(Paths.get(file.getPath())));
                databaseConnection.executeUpdate(content);
            } catch (IOException e) {
                System.err.println(String.format("Error while creating DB Schema with files from /%s Error: %s", folderName, e.getMessage()));
            }
        }
    }

}
