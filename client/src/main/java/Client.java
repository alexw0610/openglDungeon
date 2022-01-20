import dto.ssl.AuthenticationRequest;
import dto.ssl.GenericResponse;
import dto.ssl.ReadyForReceivingRequest;
import engine.Engine;
import engine.component.CameraComponent;
import engine.component.TransformationComponent;
import engine.entity.EntityBuilder;
import engine.service.ZoneGenerator;
import exception.UDPServerException;
import org.apache.commons.lang3.StringUtils;
import org.joml.Vector2d;
import processor.CharacterUpdateSender;
import processor.ServerUpdateProcessor;
import udp.UdpSocket;
import udp.UpdateListener;
import udp.UpdateSender;
import util.ApplicationProperties;
import util.ParameterUtil;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

public class Client {
    private static final ApplicationProperties applicationProperties = new ApplicationProperties();

    private static final String ENCRYPTION_KEY = "ENCRYPTION_KEY";
    private static final String SERVER_UDP_HOST = "UDP_LISTENING_ADDRESS";
    private static final String SERVER_UDP_PORT = "UDP_LISTENING_PORT";
    private static final String CONNECTION_ID = "CONNECTION_ID";
    private static final String DEFAULT_UDP_PORT = "defaultUDPPort";

    private static String serverUdpUpdatePort;
    private static String serverUdpUpdateHost;
    private static int connectionId;
    private static byte[] encryptionKey;

    public static void main(String[] args) {
        Engine engine = new Engine();
        Vector2d startPosition = ZoneGenerator.generate("sewer_city");
        EntityBuilder.builder()
                .fromTemplate("player")
                .at(startPosition.x(), startPosition.y())
                .buildAndInstantiate();
        EntityBuilder.builder()
                .withComponent(new TransformationComponent())
                .withComponent(new CameraComponent())
                .at(startPosition.x(), startPosition.y())
                .buildAndInstantiate();
        engine.start();

        if (!Boolean.parseBoolean(applicationProperties.getProperty("offlineMode"))) {
            try {
                System.out.println("Connecting with server..");
                establishServerConnection();
                System.out.println("Connection established. Sending updates to " + serverUdpUpdateHost + ":" + serverUdpUpdatePort);
            } catch (UDPServerException e) {
                System.err.println("Failed to create server connection! " + e.getMessage());
                System.exit(1);
            }
        }
    }

    private static void establishServerConnection() throws UDPServerException {
        SSLServerConnection sslServerConnection = new SSLServerConnection();
        startAuthenticationProcess(sslServerConnection);

        DatagramSocket udpSocket = UdpSocket.createSocket(applicationProperties.getProperty(DEFAULT_UDP_PORT));

        UpdateListener updateListener = new UpdateListener(udpSocket);
        new Thread(updateListener).start();
        registerUdpListener(updateListener, sslServerConnection);

        UpdateSender updateSender = new UpdateSender(getInetAddressFromName(serverUdpUpdateHost), Integer.parseInt(serverUdpUpdatePort), encryptionKey, udpSocket);
        CharacterUpdateSender characterUpdateSender = new CharacterUpdateSender(connectionId, updateSender);
        new Thread(characterUpdateSender).start();

        ServerUpdateProcessor serverUpdateProcessor = new ServerUpdateProcessor(updateListener.receivedUpdates, encryptionKey, connectionId);
        new Thread(serverUpdateProcessor).start();
        sslServerConnection.close();
    }

    private static void startAuthenticationProcess(SSLServerConnection serverConnection) {
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .username(applicationProperties.getProperty("userName"))
                .password(applicationProperties.getProperty("password"))
                .build();
        GenericResponse genericResponse = serverConnection.sendAndAwait(authenticationRequest);
        if (genericResponse.isFailed()) {
            System.err.println("Error while authenticating with the server. Response does not contain encryption key.");
            serverConnection.close();
            System.exit(1);
        }
        encryptionKey = getParameterDecodedOrExit(genericResponse.getResponseParameters(), ENCRYPTION_KEY);
    }

    private static void registerUdpListener(UpdateListener udpListener, SSLServerConnection serverConnection) {
        String listeningPort = udpListener.getPort();
        ReadyForReceivingRequest readyForReceivingRequest = ReadyForReceivingRequest.builder()
                .receivingPort(listeningPort)
                .characterName(applicationProperties.getProperty("characterName"))
                .build();
        GenericResponse genericResponse = serverConnection.sendAndAwait(readyForReceivingRequest);
        if (genericResponse.isFailed()) {
            System.err.println("Error while registering UDP Socket with the server. Request was not successful: " + genericResponse.getResponseText());
            System.exit(1);
        }
        serverUdpUpdatePort = getParameterOrExit(genericResponse.getResponseParameters(), SERVER_UDP_PORT);
        serverUdpUpdateHost = applicationProperties.getProperty("serverHost");
        connectionId = Integer.parseInt(getParameterOrExit(genericResponse.getResponseParameters(), CONNECTION_ID));
    }

    private static String getParameterOrExit(Map<String, String> responseParameters, String parameterName) {
        String parameterValue = ParameterUtil.getParameterIfExists(responseParameters, parameterName);
        if (StringUtils.isBlank(parameterValue)) {
            System.err.println("Error while communicating with the server. Response does not contain parameter " + parameterName);
            System.exit(1);
            return null;
        }
        return parameterValue;
    }

    private static byte[] getParameterDecodedOrExit(Map<String, String> responseParameters, String parameterName) {
        byte[] parameterValue = ParameterUtil.getParameterIfExistsDecoded(responseParameters, parameterName);
        if (parameterValue == null) {
            System.err.println("Error while communicating with the server. Response does not contain parameter " + parameterName);
            System.exit(1);
            return null;
        }
        return parameterValue;
    }

    private static InetAddress getInetAddressFromName(String address) {
        InetAddress serverAddress = null;
        try {
            serverAddress = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            System.err.println("Failed to get InetAddress from name: " + address);
            System.exit(1);
        }
        return serverAddress;
    }
}
