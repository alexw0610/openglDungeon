package processor;

import connection.SubscriptionHandler;
import connection.dto.SubscribedClient;
import exception.EncryptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processor.worker.CharacterUpdateProcessorWorker;
import protocol.dto.udp.PlayerUpdateDto;
import protocol.dto.udp.UpdateEncryptionWrapper;
import repository.DatabaseConnection;
import security.EncryptionHandler;
import util.ApplicationProperties;
import util.SerializableUtil;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ClientUpdateProcessor implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ClientUpdateProcessor.class);
    private static final ApplicationProperties applicationProperties = new ApplicationProperties();
    private final BlockingQueue<Runnable> characterUpdatesToProcess = new LinkedBlockingQueue<>();
    private final BlockingQueue<UpdateEncryptionWrapper> receivedUpdates;
    private final DatabaseConnection databaseConnection;

    public ClientUpdateProcessor(BlockingQueue<UpdateEncryptionWrapper> receivedUpdates) {
        this.receivedUpdates = receivedUpdates;
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
            try {
                UpdateEncryptionWrapper clientUpdate = receivedUpdates.take();
                try {
                    processUpdate(clientUpdate);
                } catch (EncryptionException e) {
                    LOG.error("Failed to process update for connectionId: {} Error:{}", clientUpdate.getConnectionId(), e.getMessage());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void processUpdate(UpdateEncryptionWrapper clientUpdate) throws EncryptionException {
        SubscribedClient subscribedClient = SubscriptionHandler.instance.subscribedClients.get(clientUpdate.getConnectionId());
        if (subscribedClient != null) {
            PlayerUpdateDto playerUpdateDto = null;
            playerUpdateDto = decryptPayload(clientUpdate.getEncryptedPayload(), subscribedClient);
            playerUpdateDto.setUserId(subscribedClient.getUserId());
            playerUpdateDto.setCharacterId(subscribedClient.getCharacterId());
            CharacterUpdateProcessorWorker serverPlayerUpdateWorker = new CharacterUpdateProcessorWorker(this.databaseConnection, playerUpdateDto);
            characterUpdatesToProcess.add(serverPlayerUpdateWorker);
        } else {
            LOG.info("Discarding update for unknown connectionId: " + clientUpdate.getConnectionId());
        }
    }

    private static PlayerUpdateDto decryptPayload(byte[] payload, SubscribedClient subscribedClient) throws EncryptionException {
        EncryptionHandler encryptionHandler = new EncryptionHandler(subscribedClient.getEncryptionKey());
        payload = encryptionHandler.decryptByteArray(payload);
        int packetSizeUnpadded = EncryptionHandler.readHeaderToInt(payload);
        byte[] unpaddedPayload = Arrays.copyOfRange(payload, 2, packetSizeUnpadded + 2);
        return (PlayerUpdateDto) SerializableUtil.fromByteArray(unpaddedPayload);
    }
}
