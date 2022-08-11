package processor;

import dto.ssl.GenericResponse;
import dto.tcp.InstanceAuthenticationRequest;
import dto.tcp.TcpEncryptionWrapper;
import engine.Engine;
import exception.EncryptionException;
import org.apache.commons.lang3.RandomStringUtils;
import security.EncryptionHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
        InstanceUpdateListener instanceUpdateListener = new InstanceUpdateListener(encryptionHandler, objectInputStream, engine);
        new Thread(instanceUpdateListener).start();
        InstanceUpdateSender instanceUpdateSender = new InstanceUpdateSender(encryptionHandler, objectOutputStream, engine, connectionId);
        new Thread(instanceUpdateSender).start();
    }

    private void createObjectIOStreams() throws IOException {
        this.objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
        this.objectInputStream = new ObjectInputStream(this.socket.getInputStream());
    }
}
