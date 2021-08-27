package server.worker;

import protocol.dto.udp.UpdateEncryptionWrapper;
import protocol.dto.update.PlayerUpdateDto;
import security.EncryptionHandler;
import server.DatabaseConnection;
import server.connection.SubscribedClient;
import server.connection.SubscriptionHandler;
import server.protocol.runnable.CharacterUpdateProcessorWorker;
import util.ApplicationProperties;
import util.SerializableUtil;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ClientUpdateProcessor implements Runnable {
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
                SubscribedClient subscribedClient = SubscriptionHandler.instance.subscribedClients.get(clientUpdate.getConnectionId());
                if (subscribedClient != null) {
                    byte[] payload = clientUpdate.getEncryptedPayload();
                    EncryptionHandler encryptionHandler = new EncryptionHandler(subscribedClient.getEncryptionKey());
                    payload = encryptionHandler.decryptByteArray(payload);
                    int packetSizeUnpadded = EncryptionHandler.readHeaderToInt(payload);
                    byte[] unpaddedPayload = Arrays.copyOfRange(payload, 2, packetSizeUnpadded + 2);
                    PlayerUpdateDto playerUpdateDto = (PlayerUpdateDto) SerializableUtil.fromByteArray(unpaddedPayload);
                    playerUpdateDto.setUserId(subscribedClient.getUserId());
                    playerUpdateDto.setCharacterId(subscribedClient.getCharacterId());
                    CharacterUpdateProcessorWorker serverPlayerUpdateWorker = new CharacterUpdateProcessorWorker(this.databaseConnection, playerUpdateDto);
                    characterUpdatesToProcess.add(serverPlayerUpdateWorker);
                } else {
                    System.out.println("Discarding update for unknown connectionId: " + clientUpdate.getConnectionId());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
