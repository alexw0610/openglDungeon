package server.worker;

import protocol.dto.Request;
import protocol.dto.ssl.AuthenticationRequest;
import protocol.dto.ssl.GenericResponse;
import protocol.dto.ssl.ReadyForReceivingRequest;
import server.DatabaseConnection;
import server.protocol.RequestProcessor;
import util.ApplicationProperties;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;

public class SSLClientConnectionWorker implements Runnable {

    private static final ApplicationProperties applicationProperties = new ApplicationProperties();
    private final SSLSocket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private final RequestProcessor requestProcessor;

    public SSLClientConnectionWorker(SSLSocket socket, String udpRecPort) {
        this.socket = socket;
        this.socket.setEnabledProtocols(new String[]{"TLSv1.2"});
        String url = applicationProperties.getProperty("dbUrl");
        String user = applicationProperties.getProperty("dbUser");
        String password = applicationProperties.getProperty("dbPassword");
        DatabaseConnection dbConnection = new DatabaseConnection(url, user, password);
        requestProcessor = new RequestProcessor(dbConnection, udpRecPort);


    }

    @Override
    public void run() {
        System.out.println(String.format("%s starting.", Thread.currentThread().getName()));
        try {
            createObjectIOStreams();
        } catch (IOException e) {
            System.err.println("Error while creating IO Socket Objects! " + e.getMessage());
            ;
            return;
        }
        try {
            listenForRequests();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error while processing Request! " + e.getMessage());
        }

        try {
            requestProcessor.close();
        } catch (SQLException e) {
            System.err.println("Error while gracefully trying to close DB connection! " + e.getMessage());
        }

        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error while gracefully closing socket! " + e.getMessage());
        }
        System.out.println(String.format("%s closing.", Thread.currentThread().getName()));
    }

    private void listenForRequests() throws IOException, ClassNotFoundException {
        Request request;
        while ((request = (Request) this.objectInputStream.readObject()) != null) {
            if (request instanceof AuthenticationRequest) {
                AuthenticationRequest authenticationRequest = (AuthenticationRequest) request;
                GenericResponse genericResponse = requestProcessor.processAuthenticationRequest(authenticationRequest);
                this.objectOutputStream.writeObject(genericResponse);

            } else if (request instanceof ReadyForReceivingRequest) {
                ReadyForReceivingRequest readyForReceivingRequest = (ReadyForReceivingRequest) request;
                GenericResponse genericResponse = requestProcessor.processReadyForReceivingRequest(readyForReceivingRequest, this.socket.getInetAddress());
                this.objectOutputStream.writeObject(genericResponse);
            }
        }
    }

    private void createObjectIOStreams() throws IOException {
        this.objectInputStream = new ObjectInputStream(socket.getInputStream());
        this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
    }

}
