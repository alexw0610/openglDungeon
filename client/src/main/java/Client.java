import dto.ssl.AuthenticationRequest;
import dto.ssl.GenericResponse;
import dto.ssl.ReadyForReceivingRequest;
import engine.Engine;
import engine.enumeration.PrimitiveMeshShape;
import engine.enumeration.ShaderType;
import engine.handler.SceneHandler;
import engine.object.Camera;
import engine.object.GameObject;
import engine.object.Hitbox;
import engine.object.Player;
import engine.object.enums.HitboxType;
import exception.UDPServerException;
import org.apache.commons.lang3.StringUtils;
import processor.CharacterUpdateSender;
import processor.ServerUpdateProcessor;
import udp.UpdateListener;
import udp.UpdateSender;
import util.ApplicationProperties;
import util.ParameterUtil;

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
        engine.start();

        Player player = new Player(PrimitiveMeshShape.QUAD, ShaderType.DEFAULT);
        player.setHitbox(new Hitbox(HitboxType.AABB, 0.5));
        player.setRenderLayer((short) 2);
        SceneHandler.getInstance().setPlayer(player);

        GameObject floor = new GameObject(PrimitiveMeshShape.QUAD, ShaderType.DEFAULT, new Hitbox(HitboxType.AABB, 5.0), 0, 0);
        floor.setScale(10);
        floor.setTextureKey("stone_rough_purple_dark_no_highlights");
        floor.setRenderLayer((short) 0);
        floor.setSurface(true);
        SceneHandler.getInstance().addObject("floor", floor);

        Camera.CAMERA.setLookAtTarget(player);

        if (!Boolean.parseBoolean(applicationProperties.getProperty("offlineMode"))) {
            try {
                establishServerConnection();
            } catch (UDPServerException e) {
                System.err.println("Failed to create server connection! " + e.getMessage());
                System.exit(1);
            }
        }

    }

    private static void establishServerConnection() throws UDPServerException {
        SSLServerConnection sslServerConnection = new SSLServerConnection();
        startAuthenticationProcess(sslServerConnection);

        UpdateListener updateListener = new UpdateListener(applicationProperties.getProperty(DEFAULT_UDP_PORT));
        new Thread(updateListener).start();

        registerUdpListener(updateListener, sslServerConnection);

        UpdateSender updateSender = new UpdateSender(getInetAddressFromName(serverUdpUpdateHost), Integer.parseInt(serverUdpUpdatePort), encryptionKey);
        CharacterUpdateSender characterUpdateSender = new CharacterUpdateSender(SceneHandler.getInstance().getPlayer(), connectionId, updateSender);
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
            System.err.println("Error while registering UDP Socket with the server. Request was not successful " + genericResponse.getResponseText());
            System.exit(1);
        }
        serverUdpUpdatePort = getParameterOrExit(genericResponse.getResponseParameters(), SERVER_UDP_PORT);
        serverUdpUpdateHost = getParameterOrExit(genericResponse.getResponseParameters(), SERVER_UDP_HOST);
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
