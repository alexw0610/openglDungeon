package server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.repository.DatabaseConnection;
import server.worker.ClientUpdateProcessor;
import server.worker.ClientUpdateSender;
import server.worker.SSLClientConnectionServer;
import udp.UpdateListener;
import util.ApplicationProperties;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Server {

    private static Logger LOG;
    private static final ApplicationProperties applicationProperties = new ApplicationProperties();

    public static void main(String[] args) {
        setSystemProperties();
        LOG = LoggerFactory.getLogger(Server.class);
        setupDatabase();

        UpdateListener clientUpdateListener = new UpdateListener();
        new Thread(clientUpdateListener).start();

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
        System.setProperty("javax.net.ssl.keyStore", applicationProperties.getResourceRootPath() + applicationProperties.getProperty("keyStore"));
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
        File resourceDir = new File(applicationProperties.getResourceRootPath() + "/database/" + folderName);
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
