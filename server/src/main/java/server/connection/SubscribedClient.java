package server.connection;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.net.InetAddress;
import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
public class SubscribedClient {

    private int userId;
    private int characterId;
    private InetAddress clientAddress;
    private String clientPort;
    private byte[] encryptionKey;


    @Override
    public int hashCode() {
        return Objects.hash(clientAddress, encryptionKey);
    }
}
