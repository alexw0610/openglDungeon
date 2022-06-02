package processor;

import dto.Request;
import dto.ssl.GenericResponse;
import dto.tcp.InstanceAuthenticationRequest;
import dto.tcp.TcpEncryptionWrapper;
import dto.udp.PlayerUpdateDto;
import engine.Engine;
import engine.component.AIComponent;
import engine.component.PlayerTag;
import engine.component.TransformationComponent;
import engine.component.ZoneChangeComponent;
import engine.entity.Entity;
import exception.EncryptionException;
import org.apache.commons.lang3.RandomStringUtils;
import org.joml.Vector2d;
import security.EncryptionHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.List;

public class InstanceServerConnection implements Runnable {
    private final byte[] encryptionKey;
    private final int connectionId;
    private final Engine engine;
    private final String tcpPort;
    private final String tcpHost;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private Socket socket;

    public InstanceServerConnection(byte[] encryptionKey, int connectionId, Engine engine, String serverUdpUpdateHost, String serverTcpUpdatePort) {
        this.engine = engine;
        this.encryptionKey = encryptionKey;
        this.connectionId = connectionId;
        this.tcpPort = serverTcpUpdatePort;
        this.tcpHost = serverUdpUpdateHost;
    }

    @Override
    public void run() {
        EncryptionHandler encryptionHandler = new EncryptionHandler(this.encryptionKey);
        try {
            this.socket = new Socket(this.tcpHost, Integer.parseInt(this.tcpPort));
            String secret = RandomStringUtils.randomAlphanumeric(16);
            InstanceAuthenticationRequest instanceAuthenticationRequest =
                    InstanceAuthenticationRequest.builder()
                            .connectionId(this.connectionId)
                            .secret(secret)
                            .encryptedPayload(encryptionHandler.encryptSerializable(secret))
                            .build();
            createObjectIOStreams();
            this.objectOutputStream.writeObject(instanceAuthenticationRequest);
            this.objectOutputStream.flush();
            Object response = this.objectInputStream.readObject();
            if (response instanceof TcpEncryptionWrapper) {
                TcpEncryptionWrapper wrapper = (TcpEncryptionWrapper) response;
                GenericResponse genericResponse = (GenericResponse) encryptionHandler.decryptByteArrayToObject(wrapper.getEncryptedPayload());
                if (!genericResponse.isFailed()) {
                    System.out.println(genericResponse.getResponseText());
                } else {
                    System.err.println("Failed to create tcp server connection! Response failed!");
                    System.exit(1);
                }
            } else {
                System.err.println("Failed to create tcp server connection! Received wrong response!");
                System.exit(1);
            }
        } catch (IOException | ClassNotFoundException | EncryptionException e) {
            System.err.println("Failed to create tcp server connection! " + e.getMessage());
            System.exit(1);
        }
        System.out.println("TCP authentication finished");
        while (true) {
            try {
                Request request;
                while ((request = (Request) this.objectInputStream.readObject()) != null) {
                    Class<?> objectType = ((TcpEncryptionWrapper) request).getObjectType();
                    if (PlayerUpdateDto.class.equals(objectType)) {
                        PlayerUpdateDto playerUpdateDto = (PlayerUpdateDto) encryptionHandler.decryptByteArrayToObject(((TcpEncryptionWrapper) request).getEncryptedPayload());
                        System.out.println("Local/Server player delta: " + new Vector2d(playerUpdateDto.getPositionX(), playerUpdateDto.getPositionY()).distance(engine.getEntityHandler().getEntityWithComponent(PlayerTag.class).getComponentOfType(TransformationComponent.class).getPosition()));
                        if (engine.getNavHandler().getNavMap().getSeed() != playerUpdateDto.getZoneId()) {
                            ZoneChangeComponent zoneChangeComponent = new ZoneChangeComponent((int) playerUpdateDto.getZoneId());
                            engine.getEntityHandler().getEntityWithComponent(PlayerTag.class).addComponent(zoneChangeComponent);
                            System.out.println("changed zone from " + engine.getNavHandler().getNavMap().getSeed() + " to " + playerUpdateDto.getZoneId());
                        }
                    } else if (List.class.equals(objectType)) {
                        Serializable retrieveObject = encryptionHandler.decryptByteArrayToObject(((TcpEncryptionWrapper) request).getEncryptedPayload());
                        if (retrieveObject instanceof List) {
                            List<Entity> synchronizedEntities = (List<Entity>) retrieveObject;
                            addOrUpdateSynchronizedEntities(synchronizedEntities);
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException | EncryptionException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

    }

    private void addOrUpdateSynchronizedEntities(List<Entity> synchronizedEntities) {
        for (Entity synchronizedEntity : synchronizedEntities) {
            if (engine.getEntityHandler().getObject(String.valueOf(synchronizedEntity.getEntityId())) == null) {
                System.out.println("Added synchronized entity!");
                engine.getEntityHandler().addObject(String.valueOf(synchronizedEntity.getEntityId()), synchronizedEntity);
            } else {
                Entity entity = engine.getEntityHandler().getObject(String.valueOf(synchronizedEntity.getEntityId()));
                entity.getComponentOfType(AIComponent.class).setPathToTarget(synchronizedEntity.getComponentOfType(AIComponent.class).getPathToTarget());
                entity.getComponentOfType(AIComponent.class).setCurrentState(synchronizedEntity.getComponentOfType(AIComponent.class).getCurrentState());
            }
        }
    }

    private void createObjectIOStreams() throws IOException {
        this.objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
        this.objectInputStream = new ObjectInputStream(this.socket.getInputStream());
    }
}
