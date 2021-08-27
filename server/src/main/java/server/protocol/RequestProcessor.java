package server.protocol;

import lombok.SneakyThrows;
import protocol.dto.ssl.AuthenticationRequest;
import protocol.dto.ssl.GenericResponse;
import protocol.dto.ssl.ReadyForReceivingRequest;
import server.DatabaseConnection;
import server.connection.SubscribedClient;
import server.connection.SubscriptionHandler;
import server.repository.UserRepository;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class RequestProcessor {

    private static final String ENCRYPTION_KEY = "ENCRYPTION_KEY";
    private static final String UDP_LISTENING_PORT = "UDP_LISTENING_PORT";
    private static final String CONNECTION_ID = "CONNECTION_ID";
    private final DatabaseConnection connection;
    private final SecureRandom secureRandom;
    private final String udpRecPort;
    private boolean isAuthenticated = false;
    private final byte[] encryptionKey = new byte[16];
    private String userAccountId;

    @SneakyThrows
    public RequestProcessor(DatabaseConnection connection, String udpRecPort) {
        this.connection = connection;
        this.secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
        this.udpRecPort = udpRecPort;
    }

    public GenericResponse processAuthenticationRequest(AuthenticationRequest authenticationRequest) {
        try {
            ResultSet resultSet = UserRepository.getByUsernameAndPassword(connection, authenticationRequest.getUsername(), authenticationRequest.getPassword());
            //TODO: get character for user
            GenericResponse genericResponse;
            if (resultSet.next()) {
                this.userAccountId = resultSet.getString("user_account_id");
                Map<String, String> parameters = new HashMap<>();
                parameters.put(ENCRYPTION_KEY, getEncodedEncryptionKey());
                genericResponse = GenericResponse.builder()
                        .responseStatus(true)
                        .responseText(String.format("Hello %s, successfully authenticated.", authenticationRequest.getUsername()))
                        .responseParameters(parameters)
                        .build();
                this.isAuthenticated = true;
            } else {
                genericResponse = GenericResponse.builder()
                        .responseStatus(false)
                        .responseText("Wrong username and/or password!")
                        .build();
            }
            return genericResponse;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return GenericResponse.builder()
                .responseStatus(false)
                .responseText("Authentication service currently unavailable!")
                .build();
    }

    public GenericResponse processReadyForReceivingRequest(ReadyForReceivingRequest readyForReceivingRequest, InetAddress clientAddress) {
        if (isAuthenticated && userAccountId != null) {
            if (UserRepository.setUserAccountActive(connection, this.userAccountId, clientAddress.getHostAddress(), readyForReceivingRequest.getReceivingPort())) {
                SubscribedClient client = SubscribedClient.builder()
                        .clientAddress(clientAddress)
                        .clientPort(readyForReceivingRequest.getReceivingPort())
                        .encryptionKey(this.encryptionKey)
                        .characterId(Integer.parseInt(this.userAccountId))
                        .userId(Integer.parseInt(this.userAccountId))
                        .build();
                SubscriptionHandler.instance.subscribedClients.put(client.hashCode(), client);
                Map<String, String> parameters = new HashMap<>();
                parameters.put(UDP_LISTENING_PORT, udpRecPort);
                parameters.put(CONNECTION_ID, String.valueOf(client.hashCode()));
                return GenericResponse.builder()
                        .responseStatus(true)
                        .responseText("UDP address/port successfully registered! Send your updates to the port UDP_LISTENING_PORT")
                        .responseParameters(parameters)
                        .build();
            } else {
                return GenericResponse.builder()
                        .responseStatus(false)
                        .responseText("Error while registering UDP address/port!")
                        .build();
            }
        }
        return GenericResponse.builder()
                .responseStatus(false)
                .responseText("Error while registering UDP address/port!")
                .build();
    }

    private String getEncodedEncryptionKey() {
        this.secureRandom.nextBytes(encryptionKey);
        byte[] encodedRandomBytes = Base64.getEncoder().encode(encryptionKey);
        return new String(encodedRandomBytes, StandardCharsets.UTF_8);
    }

    public void close() throws SQLException {
        this.connection.close();
    }
}
