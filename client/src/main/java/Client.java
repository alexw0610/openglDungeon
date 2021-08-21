import engine.Engine;
import engine.enumeration.PrimitiveMeshShape;
import engine.enumeration.ShaderType;
import engine.handler.RenderHandler;
import engine.handler.SceneHandler;
import engine.object.Camera;
import engine.object.GameObject;
import engine.object.Player;
import protocol.dto.ssl.AuthenticationRequest;
import protocol.dto.ssl.GenericResponse;
import protocol.dto.ssl.ReadyForReceivingRequest;
import udp.UpdateListener;
import udp.UpdateSender;
import util.ApplicationProperties;
import util.ParameterUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Client {
    private static final ApplicationProperties applicationProperties = new ApplicationProperties();
    private static final String ENCRYPTION_KEY = "ENCRYPTION_KEY";
    private static final String UDP_LISTENING_PORT = "UDP_LISTENING_PORT";
    private static final String CONNECTION_ID = "CONNECTION_ID";


    public static void main(String[] args) {


        Engine engine = new Engine();
        engine.start();
        Player player = new Player("player", PrimitiveMeshShape.QUAD, ShaderType.DEFAULT, 0.5, 0.3);
        GameObject floor = new GameObject(PrimitiveMeshShape.QUAD, ShaderType.DEFAULT, 0.5, 0.3);
        floor.setScale(10);
        floor.setTextureKey("stone_rough_yellow");
        SceneHandler.SCENE_HANDLER.setPlayer(player);
        Camera.CAMERA.setLookAtTarget(player);
        RenderHandler.RENDER_HANDLER.addToRenderQueue(floor);
        RenderHandler.RENDER_HANDLER.addToRenderQueue(player);

        establishServerConnection();

    }

    private static void establishServerConnection() {
        SSLServerConnection sslServerConnection = new SSLServerConnection();
        byte[] key = startAuthenticationProcess(sslServerConnection);

        UpdateListener updateListener = new UpdateListener();
        new Thread(updateListener).start();

        GenericResponse response = registerUdpListener(updateListener, sslServerConnection);
        String serverUdpUpdatePort = ParameterUtil.getParameterIfExists(response.getResponseParameters(), UDP_LISTENING_PORT);
        int connectionId = Integer.parseInt(ParameterUtil.getParameterIfExists(response.getResponseParameters(), CONNECTION_ID));

        InetAddress serverAddress = null;
        try {
            serverAddress = InetAddress.getByName(applicationProperties.getProperty("serverHost"));
        } catch (UnknownHostException e) {
            System.err.println("Failed to get InetAddress from name: " + applicationProperties.getProperty("serverHost"));
            System.exit(1);
        }
        UpdateSender updateSender = new UpdateSender(serverAddress, Integer.parseInt(serverUdpUpdatePort), key);

        CharacterUpdateWorker updateWorker = new CharacterUpdateWorker(SceneHandler.SCENE_HANDLER.getPlayer(), connectionId, updateSender);
        new Thread(updateWorker).start();

        sslServerConnection.close();
    }

    private static GenericResponse registerUdpListener(UpdateListener udpListener, SSLServerConnection serverConnection) {
        String listeningPort = udpListener.getPort();
        String listeningAddress = udpListener.getAddress();
        ReadyForReceivingRequest readyForReceivingRequest = ReadyForReceivingRequest.builder()
                .receivingAddress(listeningAddress)
                .receivingPort(listeningPort)
                .build();
        GenericResponse genericResponse = serverConnection.sendAndAwait(readyForReceivingRequest);
        if (genericResponse.isSuccessful()) {
            String serverUdpUpdatePort = ParameterUtil.getParameterIfExists(genericResponse.getResponseParameters(), UDP_LISTENING_PORT);
            if (serverUdpUpdatePort == null) {
                System.err.println("Error while registering UDP Socket with the server. Response does not contain upd listening port.");
                System.exit(1);
                return null;
            }
            return genericResponse;
        }
        System.err.println("Error while registering UDP Socket with the server. Response does not contain upd listening port.");
        System.exit(1);
        return null;
    }

    private static byte[] startAuthenticationProcess(SSLServerConnection serverConnection) {

        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .username(applicationProperties.getProperty("username"))
                .password(applicationProperties.getProperty("password"))
                .build();

        GenericResponse genericResponse = serverConnection.sendAndAwait(authenticationRequest);

        if (genericResponse.isSuccessful()) {
            byte[] encryptionKey = ParameterUtil.getParameterIfExistsDecoded(genericResponse.getResponseParameters(), ENCRYPTION_KEY);
            if (encryptionKey != null) {
                return encryptionKey;
            } else {
                System.err.println("Error while authenticating with the server. Response does not contain encryption key.");
                serverConnection.close();
                System.exit(1);
            }
        }
        System.err.println("Error while authenticating with the server. " + genericResponse.getResponseText());
        serverConnection.close();
        System.exit(1);
        return null;
    }

}
