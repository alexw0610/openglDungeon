package server.worker;

import protocol.dto.AuthenticationRequest;
import protocol.dto.ReadyForReceivingRequest;
import protocol.dto.Request;
import protocol.dto.GenericResponse;
import server.DatabaseConnection;
import server.connection.ConnectionHandler;
import server.connection.SubscribedClient;
import server.protocol.RequestProcessor;
import util.ApplicationProperties;
import util.ParameterUtil;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;

public class SSLClientConnectionWorker implements Runnable {

    private static final ApplicationProperties applicationProperties = new ApplicationProperties();
    public static final String ENCRYPTION_KEY = "ENCRYPTION_KEY";
    private final SSLSocket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    private final RequestProcessor requestProcessor;

    public SSLClientConnectionWorker(SSLSocket socket) {
        this.socket = socket;
        this.socket.setEnabledProtocols(new String[]{"TLSv1.2"});
        String url = applicationProperties.getProperty("dbUrl");
        String user = applicationProperties.getProperty("dbUser");
        String password = applicationProperties.getProperty("dbPassword");
        DatabaseConnection dbConnection = new DatabaseConnection(url, user, password);
        requestProcessor = new RequestProcessor(dbConnection);

    }

    @Override
    public void run() {
        System.out.println(String.format("%s starting.", Thread.currentThread().getName()));
        try {
            createObjectIOStreams();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return;
        }
        try {
            listenForRequests();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error while processing Request!");
            e.printStackTrace();
        }

        try {
            requestProcessor.close();
        } catch (SQLException e) {
            System.err.println("Error while gracefully trying to close DB connection!");
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error while gracefully closing socket!");
            e.printStackTrace();
        }
        System.out.println(String.format("%s closing.", Thread.currentThread().getName()));
    }

    private void listenForRequests() throws IOException, ClassNotFoundException {
        Request request;
        boolean isAuth = false;
        boolean isSubscribed = false;
        byte[] key = null;
        while (!isSubscribed && (request = (Request) this.objectInputStream.readObject()) != null) {
            if (request instanceof AuthenticationRequest) {
                AuthenticationRequest authenticationRequest = (AuthenticationRequest) request;
                System.out.println(String.format("%s: Received request from %s of type %s. %s %s",
                        Thread.currentThread().getName(),
                        socket.getInetAddress(),
                        AuthenticationRequest.class.getName(),
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword())
                );
                GenericResponse genericResponse = requestProcessor.processAuthenticationRequest(authenticationRequest);
                this.objectOutputStream.writeObject(genericResponse);
                isAuth = true;
                key = ParameterUtil.getParameterIfExistsDecoded(genericResponse.getResponseParameters(),ENCRYPTION_KEY);
            } else if (request instanceof ReadyForReceivingRequest && isAuth && key != null) {
                ReadyForReceivingRequest readyForReceivingRequest = (ReadyForReceivingRequest) request;
                SubscribedClient client = SubscribedClient.builder()
                        .address(this.socket.getInetAddress())
                        .port(readyForReceivingRequest.getReceivingPort())
                        .key(key)
                        .build();
                ConnectionHandler.instance.subscribersQueue.add(client);
                isSubscribed = true;
            }
        }
    }

    private void createObjectIOStreams() throws IOException {
        this.objectInputStream = new ObjectInputStream(socket.getInputStream());
        this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
    }

}
