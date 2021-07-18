package server.protocol;

import lombok.SneakyThrows;
import protocol.dto.AuthenticationRequest;
import protocol.dto.GenericResponse;
import server.DatabaseConnection;
import server.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class RequestProcessor {

    public static final String ENCRYPTION_KEY = "ENCRYPTION_KEY";
    private final DatabaseConnection connection;

    private final SecureRandom secureRandom;

    @SneakyThrows
    public RequestProcessor(DatabaseConnection connection) {
        this.connection = connection;
        this.secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");

    }

    public GenericResponse processAuthenticationRequest(AuthenticationRequest authenticationRequest) {
        try {
            ResultSet resultSet = UserRepository.getByUsernameAndPassword(connection, authenticationRequest.getUsername(), authenticationRequest.getPassword());
            GenericResponse genericResponse;
            if (resultSet.next()) {
                Map<String, String> parameters = new HashMap<>();
                parameters.put(ENCRYPTION_KEY, getEncodedEncryptionKey());
                genericResponse = GenericResponse.builder()
                        .responseStatus(true)
                        .responseText(String.format("Hello %s, successfully authenticated.", authenticationRequest.getUsername()))
                        .responseParameters(parameters)
                        .build();
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

    private String getEncodedEncryptionKey() {
        byte[] randomBytes = new byte[16];
        this.secureRandom.nextBytes(randomBytes);
        byte[] encodedRandomBytes = Base64.getEncoder().encode(randomBytes);
        return new String(encodedRandomBytes, StandardCharsets.UTF_8);
    }

    public void close() throws SQLException {
        this.connection.close();
    }
}
