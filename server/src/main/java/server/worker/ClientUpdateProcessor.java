package server.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.dto.udp.UpdateEncryptionWrapper;
import protocol.dto.update.PlayerUpdateDto;
import security.EncryptionHandler;
import server.connection.SubscribedClient;
import server.connection.SubscriptionHandler;
import server.protocol.runnable.CharacterUpdateProcessorWorker;
import server.repository.DatabaseConnection;
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
                processUpdate(clientUpdate);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void processUpdate(UpdateEncryptionWrapper clientUpdate) {
        SubscribedClient subscribedClient = SubscriptionHandler.instance.subscribedClients.get(clientUpdate.getConnectionId());
        if (subscribedClient != null) {
            PlayerUpdateDto playerUpdateDto = decryptPayload(clientUpdate.getEncryptedPayload(), subscribedClient);
            playerUpdateDto.setUserId(subscribedClient.getUserId());
            playerUpdateDto.setCharacterId(subscribedClient.getCharacterId());
            CharacterUpdateProcessorWorker serverPlayerUpdateWorker = new CharacterUpdateProcessorWorker(this.databaseConnection, playerUpdateDto);
            characterUpdatesToProcess.add(serverPlayerUpdateWorker);
        } else {
            LOG.info("Discarding update for unknown connectionId: " + clientUpdate.getConnectionId());
        }
    }

    private static PlayerUpdateDto decryptPayload(byte[] payload, SubscribedClient subscribedClient) {
        EncryptionHandler encryptionHandler = new EncryptionHandler(subscribedClient.getEncryptionKey());
        payload = encryptionHandler.decryptByteArray(payload);
        int packetSizeUnpadded = EncryptionHandler.readHeaderToInt(payload);
        byte[] unpaddedPayload = Arrays.copyOfRange(payload, 2, packetSizeUnpadded + 2);
        return (PlayerUpdateDto) SerializableUtil.fromByteArray(unpaddedPayload);
    }
}
