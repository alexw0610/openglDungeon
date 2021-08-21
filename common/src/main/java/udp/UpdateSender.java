package udp;

import protocol.dto.udp.UpdateEncryptionWrapper;
import security.EncryptionHandler;
import util.SerializableUtil;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UpdateSender {
    private final InetAddress address;
    private final int port;
    private final byte[] encryptionKey;

    public UpdateSender(InetAddress address, int port, byte[] encryptionKey) {
        this.address = address;
        this.port = port;
        this.encryptionKey = encryptionKey;
    }

    public void sendUpdate(Serializable objectToSend, int connectionId) {
        try {
            byte[] encryptedPayload = encryptObjectToSend(objectToSend);
            UpdateEncryptionWrapper wrapper = wrapEnrcyptedPayload(encryptedPayload, connectionId);
            sendWrapper(wrapper);
        } catch (IOException e) {
            System.err.println("Failed to send packet to client " + address.toString());
        }
    }

    private byte[] encryptObjectToSend(Serializable objectToSend) {
        EncryptionHandler encryptionHandler = new EncryptionHandler(encryptionKey);
        return encryptionHandler.encryptSerializableWithHeader(objectToSend);
    }

    private UpdateEncryptionWrapper wrapEnrcyptedPayload(byte[] encryptedPayload, int connectionId) {
        return UpdateEncryptionWrapper.builder()
                .connectionId(connectionId)
                .encryptedPayload(encryptedPayload)
                .build();
    }

    private void sendWrapper(Serializable wrapper) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        byte[] payload = SerializableUtil.toByteArray(wrapper);
        DatagramPacket packet = new DatagramPacket(payload, payload.length, address, this.port);
        socket.send(packet);
    }

}
