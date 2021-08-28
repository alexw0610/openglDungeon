package server.connection;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.net.InetAddress;
import java.util.Objects;

@Getter
@Setter
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

    public int getConnectionId() {
        return hashCode();
    }

    @Override
    public String toString() {
        return "SubscribedClient{" +
                "userId=" + userId +
                ", characterId=" + characterId +
                ", clientAddress=" + clientAddress +
                ", clientPort='" + clientPort + "'" +
                ", connectionId=" + hashCode() +
                '}';
    }
}
