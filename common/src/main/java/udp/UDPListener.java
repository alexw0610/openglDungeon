package udp;

import protocol.dto.GenericResponse;
import util.ApplicationProperties;
import util.SerializableUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UDPListener implements Runnable {

    private static final ApplicationProperties applicationProperties = new ApplicationProperties();
    public static final String DEFAULT_UDP_LISTENING_PORT = "defaultUDPListeningPort";

    public BlockingQueue<byte[]> receivedPacketQueue = new LinkedBlockingQueue<>();

    private final String port;
    private final DatagramSocket socket;

    public UDPListener() {
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
                    addPacketToQueue(packet);
                }
                buffer = new byte[65536];
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addPacketToQueue(DatagramPacket packet) {
        byte[] payload = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
        this.receivedPacketQueue.add(payload);
    }

    private int readHeaderToInt(byte[] buffer) {
        return ((buffer[1] & 0xff) << 8) | (buffer[0] & 0xff);
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
