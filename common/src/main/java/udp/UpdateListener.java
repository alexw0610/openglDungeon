package udp;

import protocol.dto.udp.UpdateEncryptionWrapper;
import util.ApplicationProperties;
import util.SerializableUtil;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UpdateListener implements Runnable {

    private static final ApplicationProperties applicationProperties = new ApplicationProperties();
    public static final String DEFAULT_UDP_LISTENING_PORT = "defaultUDPListeningPort";

    public BlockingQueue<UpdateEncryptionWrapper> receivedUpdates = new LinkedBlockingQueue<>();

    private final String port;
    private final DatagramSocket socket;

    public UpdateListener() {
        this.socket = createSocket();
        this.port = String.valueOf(this.socket.getLocalPort());
    }

    @Override
    public void run() {
        byte[] buffer = new byte[65536];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        while (true) {
            try {
                this.socket.receive(packet);
                if (packet.getLength() > 0) {
                    validateUpdateEncryptionWrapper(packet);
                }
                buffer = new byte[65536];
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void validateUpdateEncryptionWrapper(DatagramPacket packet) {
        byte[] payload = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
        Serializable wrapper = SerializableUtil.fromByteArray(payload);
        if (wrapper instanceof UpdateEncryptionWrapper) {
            this.receivedUpdates.add((UpdateEncryptionWrapper) wrapper);
            System.out.println("Update received from " + packet.getSocketAddress().toString() + " " + ((UpdateEncryptionWrapper) wrapper).getConnectionId());
        }
    }

    public String getPort() {
        return this.port;
    }

    public String getAddress() {
        return this.socket.getLocalAddress().toString();
    }

    private static DatagramSocket createSocket() {
        DatagramSocket sock;
        try {
            sock = new DatagramSocket(Integer.parseInt(applicationProperties.getProperty(DEFAULT_UDP_LISTENING_PORT)));
            return sock;
        } catch (SocketException e) {
            System.err.println(String.format("Error while creating UDP listener socket on default port %s", applicationProperties.getProperty(DEFAULT_UDP_LISTENING_PORT)));
            try {
                sock = new DatagramSocket();
                System.out.println(String.format("UDP listener socket created on alternative port %d", sock.getLocalPort()));
                return sock;
            } catch (SocketException socketException) {
                System.err.println("Failed to create UDP listener socket. Exiting.");
                System.exit(1);
                return null;
            }
        }
    }
}
