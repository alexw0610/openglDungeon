package service;

import exception.UDPServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processor.ClientUpdateProcessor;
import processor.ClientUpdateSender;
import processor.SSLClientConnectionServer;
import repository.DatabaseConnection;
import udp.UpdateListener;
import util.ApplicationProperties;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Server {

    private static Logger LOG;
    private static final ApplicationProperties applicationProperties = new ApplicationProperties();
    private static final String DEFAULT_UDP_PORT = "defaultUDPPort";

    public static void main(String[] args) {
        setSystemProperties();
        LOG = LoggerFactory.getLogger(Server.class);
        setupDatabase();

        UpdateListener clientUpdateListener = null;
        try {
            clientUpdateListener = new UpdateListener(applicationProperties.getProperty(DEFAULT_UDP_PORT));
            new Thread(clientUpdateListener).start();
        } catch (UDPServerException e) {
            LOG.error("Failed to create UDP listener. {}", e.getMessage());
            System.exit(1);
        }

        ClientUpdateProcessor clientUpdateProcessor = new ClientUpdateProcessor(clientUpdateListener.receivedUpdates);
        new Thread(clientUpdateProcessor).start();

        ClientUpdateSender clientUpdateSender = new ClientUpdateSender();
        new Thread(clientUpdateSender).start();

        SSLClientConnectionServer sslClientConnectionServer = new SSLClientConnectionServer(clientUpdateListener.getAddress(), clientUpdateListener.getPort());
        new Thread(sslClientConnectionServer).start();

        LOG.info("Server startup finished");
        LOG.info("Listening to udp updates @ {}:{}", clientUpdateListener.getAddress(), clientUpdateListener.getPort());
        LOG.info("Listening to SSL requests @ {}:{}", clientUpdateListener.getAddress(), sslClientConnectionServer.getPort());
        LOG.info("Using database connection {} {} {}",
                applicationProperties.getProperty("dbUrl"),
                applicationProperties.getProperty("dbUser"),
                applicationProperties.getProperty("dbPassword"));
    }

    private static void setSystemProperties() {
        System.setProperty("javax.net.ssl.keyStore", applicationProperties.getFileFromResourcePath(applicationProperties.getProperty("keyStore")).getPath());
        System.setProperty("javax.net.ssl.keyStorePassword", applicationProperties.getProperty("keyStorePassword"));
        System.setProperty("logback.configurationFile", applicationProperties.getProperty("logback.configurationFile"));
    }

    private static void setupDatabase() {
        String url = applicationProperties.getProperty("dbUrl");
        String user = applicationProperties.getProperty("dbUser");
        String password = applicationProperties.getProperty("dbPassword");
        DatabaseConnection databaseConnection = new DatabaseConnection(url, user, password);
        processFilesInDirectory("function", databaseConnection);
        processFilesInDirectory("table", databaseConnection);
        processFilesInDirectory("foreignKey", databaseConnection);
        databaseConnection.close();
    }

    private static void processFilesInDirectory(String folderName, DatabaseConnection databaseConnection) {
        File resourceDir = applicationProperties.getFileFromResourcePath("database/" + folderName);
        for (File file : resourceDir.listFiles()) {
            try {
                String content = new String(Files.readAllBytes(Paths.get(file.getPath())));
                databaseConnection.execute(content);
            } catch (IOException e) {
                LOG.error(String.format("Error while creating DB Schema with files from /%s Error: %s", folderName, e.getMessage()));
            }
        }
    }

}
