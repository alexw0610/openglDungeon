package server.protocol;

import lombok.SneakyThrows;
import protocol.dto.ssl.AuthenticationRequest;
import protocol.dto.ssl.GenericResponse;
import protocol.dto.ssl.ReadyForReceivingRequest;
import server.connection.SubscribedClient;
import server.connection.SubscriptionHandler;
import server.repository.DatabaseConnection;
import server.repository.UserRepository;
import server.repository.dto.UserDto;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
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
    private int userAccountId;
    private SubscribedClient servedClient;

    @SneakyThrows
    public RequestProcessor(DatabaseConnection connection, String udpRecPort) {
        this.connection = connection;
        this.secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
        this.udpRecPort = udpRecPort;
    }

    public GenericResponse processAuthenticationRequest(AuthenticationRequest authenticationRequest) {

        UserDto user = UserRepository.getByUsernameAndPassword(connection, authenticationRequest.getUsername(), authenticationRequest.getPassword());
        //TODO: get character for user
        GenericResponse genericResponse;
        if (user != null) {
            this.userAccountId = user.getUserAccountId();
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
                    .responseText("Failed to authenticate. Wrong username and/or password")
                    .build();
        }
        return genericResponse;
    }

    public GenericResponse processReadyForReceivingRequest(ReadyForReceivingRequest readyForReceivingRequest, InetAddress clientAddress) {
        if (this.isAuthenticated) {
            if (UserRepository.setUserAccountActive(connection, this.userAccountId, clientAddress.getHostAddress(), readyForReceivingRequest.getReceivingPort())) {
                servedClient = SubscribedClient.builder()
                        .clientAddress(clientAddress)
                        .clientPort(readyForReceivingRequest.getReceivingPort())
                        .encryptionKey(this.encryptionKey)
                        .characterId(this.userAccountId)
                        .userId(this.userAccountId)
                        .build();
                SubscriptionHandler.instance.subscribedClients.put(servedClient.hashCode(), servedClient);
                Map<String, String> parameters = new HashMap<>();
                parameters.put(UDP_LISTENING_PORT, this.udpRecPort);
                parameters.put(CONNECTION_ID, String.valueOf(servedClient.hashCode()));
                return GenericResponse.builder()
                        .responseStatus(true)
                        .responseText("UDP address/port successfully registered!")
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
                .responseText("Error while registering UDP address/port! You are not authenticated!")
                .build();
    }

    private String getEncodedEncryptionKey() {
        this.secureRandom.nextBytes(encryptionKey);
        byte[] encodedRandomBytes = Base64.getEncoder().encode(encryptionKey);
        return new String(encodedRandomBytes, StandardCharsets.UTF_8);
    }

    public void close() {
        this.connection.close();
    }

    public SubscribedClient getServedClient() {
        return this.servedClient;
    }
}
