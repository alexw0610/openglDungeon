package server.worker;

import server.DatabaseConnection;
import server.connection.SubscribedClient;
import server.connection.SubscriptionHandler;
import server.protocol.runnable.CharacterUpdateSenderWorker;
import util.ApplicationProperties;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ClientUpdateSender implements Runnable {

    private static final ApplicationProperties applicationProperties = new ApplicationProperties();

    private final BlockingQueue<Runnable> characterUpdatesToProcess = new LinkedBlockingQueue<>();
    private final DatabaseConnection databaseConnection;

    public ClientUpdateSender() {
        String url = applicationProperties.getProperty("dbUrl");
        String user = applicationProperties.getProperty("dbUser");
        String password = applicationProperties.getProperty("dbPassword");
        this.databaseConnection = new DatabaseConnection(url, user, password);
    }

    @Override
    public void run() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 32, 10L, TimeUnit.SECONDS, characterUpdatesToProcess);
        executor.prestartAllCoreThreads();

        while (true) {
            for (SubscribedClient client : SubscriptionHandler.instance.subscribedClients.values()) {
                CharacterUpdateSenderWorker worker = new CharacterUpdateSenderWorker(databaseConnection, client);
                this.characterUpdatesToProcess.add(worker);
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
