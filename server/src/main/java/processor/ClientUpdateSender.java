package processor;

import connection.SubscriptionHandler;
import connection.dto.SubscribedClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processor.worker.CharacterUpdateSenderWorker;
import repository.DatabaseConnection;
import util.ApplicationProperties;

import java.util.concurrent.*;

public class ClientUpdateSender implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ClientUpdateSender.class);
    private static final ApplicationProperties applicationProperties = new ApplicationProperties();
    private final BlockingQueue<Runnable> characterUpdatesToProcess = new LinkedBlockingQueue<>();
    private final DatabaseConnection databaseConnection;
    private boolean isClosing = false;

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

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> updateWorkerFactory = scheduledExecutorService.scheduleAtFixedRate(() -> {
            for (SubscribedClient client : SubscriptionHandler.instance.subscribedClients.values()) {
                CharacterUpdateSenderWorker worker = new CharacterUpdateSenderWorker(databaseConnection, client);
                this.characterUpdatesToProcess.add(worker);
            }
        }, 10, 10, TimeUnit.MILLISECONDS);
        while (!isClosing) {
            if (characterUpdatesToProcess.size() > 10) {
                LOG.info("Client update sender has {} updates queued", characterUpdatesToProcess.size());
            }
        }
        updateWorkerFactory.cancel(false);
    }

    public void close() {
        this.isClosing = true;
    }
}
