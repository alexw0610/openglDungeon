package udp;

import exception.UDPServerException;
import protocol.dto.udp.UpdateEncryptionWrapper;
import util.SerializableUtil;

import java.io.IOException;
import java.io.Serializable;
import java.net.*;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UpdateListener implements Runnable {

    public BlockingQueue<UpdateEncryptionWrapper> receivedUpdates = new LinkedBlockingQueue<>();
    private final String address;
    private final String port;
    private final DatagramSocket socket;

    public UpdateListener(String defaultPort) throws UDPServerException {
        this.socket = createSocket(defaultPort);
        this.port = String.valueOf(this.socket.getLocalPort());
        try {
            this.address = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new UDPServerException("Failed to get local address: " + e.getMessage());
        }
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
        }
    }

    public String getPort() {
        return this.port;
    }

    public String getAddress() {
        return this.address;
    }

    private static DatagramSocket createSocket(String defaultPort) throws UDPServerException {
        DatagramSocket sock;
        try {
            sock = new DatagramSocket(Integer.parseInt(defaultPort));
            return sock;
        } catch (SocketException e) {
            try {
                sock = new DatagramSocket();
                return sock;
            } catch (SocketException socketException) {
                throw new UDPServerException("Failed to create UDP listener socket. Default port was " + defaultPort);
            }
        }
    }
}
