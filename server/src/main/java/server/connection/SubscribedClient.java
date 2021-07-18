package server.connection;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import protocol.dto.GenericResponse;
import protocol.dto.Response;
import security.EncryptionHandler;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

@Getter
@Setter
@ToString
@Builder
public class SubscribedClient implements Runnable {
    private InetAddress address;
    private String port;
    private byte[] key;

    @Override
    public void run() {
        System.out.println(String.format("Sending Update information to User %s %s! Thread %s", this.address.toString(), this.port, Thread.currentThread().getName()));
        try {
            GenericResponse response = GenericResponse.builder()
                    .responseStatus(true)
                    .responseText(String.format("This is an update for %s %s", address.toString(), port))
                    .build();
            encryptAndSend(response);
        } catch (IOException e) {
            System.out.println("Failed to send packet");
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ConnectionHandler.instance.subscribersQueue.add(this);
    }

    private void encryptAndSend(Serializable response) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        EncryptionHandler encryptionHandler = new EncryptionHandler(key);
        byte[] encryptedResponse = encryptionHandler.encryptSerializableWithHeader(response);
        DatagramPacket packet = new DatagramPacket(encryptedResponse, encryptedResponse.length, address, Integer.parseInt(port));
        socket.send(packet);
    }
}
