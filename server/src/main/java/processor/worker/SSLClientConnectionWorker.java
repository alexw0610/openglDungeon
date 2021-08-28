package processor.worker;

import connection.RequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.dto.Request;
import protocol.dto.ssl.AuthenticationRequest;
import protocol.dto.ssl.GenericResponse;
import protocol.dto.ssl.ReadyForReceivingRequest;
import repository.DatabaseConnection;
import util.ApplicationProperties;

import javax.net.ssl.SSLSocket;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SSLClientConnectionWorker implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(SSLClientConnectionWorker.class);
    private static final ApplicationProperties applicationProperties = new ApplicationProperties();
    private final SSLSocket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private final RequestProcessor requestProcessor;

    public SSLClientConnectionWorker(SSLSocket socket, String address, String port) {
        this.socket = socket;
        this.socket.setEnabledProtocols(new String[]{"TLSv1.2"});
        String url = applicationProperties.getProperty("dbUrl");
        String user = applicationProperties.getProperty("dbUser");
        String password = applicationProperties.getProperty("dbPassword");
        DatabaseConnection dbConnection = new DatabaseConnection(url, user, password);
        requestProcessor = new RequestProcessor(dbConnection, address, port);
    }

    @Override
    public void run() {
        try {
            createObjectIOStreams();
        } catch (IOException e) {
            LOG.error("Error while creating IO Socket Objects! {}", e.getMessage());
            return;
        }
        try {
            listenForRequests();
        } catch (IOException | ClassNotFoundException e) {
            if (e instanceof EOFException && this.requestProcessor.getServedClient() != null) {
                LOG.info("SSL connection with client finished! \n {}", this.requestProcessor.getServedClient().toString());

            } else {
                LOG.warn("Error while processing Request! {} ", e.getMessage());
            }
        }
        requestProcessor.close();
        try {
            socket.close();
        } catch (IOException e) {
            LOG.warn("Error while gracefully closing socket! {}", e.getMessage());
        }
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
