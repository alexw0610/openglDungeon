import protocol.dto.AuthenticationRequest;
import protocol.dto.GenericResponse;
import protocol.dto.ReadyForReceivingRequest;
import udp.UDPListener;
import util.ApplicationProperties;
import util.ParameterUtil;

public class Client {
    private static final ApplicationProperties applicationProperties = new ApplicationProperties();
    public static final String ENCRYPTION_KEY = "ENCRYPTION_KEY";

    public static void main(String[] args) {
        SSLServerConnection serverConnection = new SSLServerConnection();
        byte[] key = startAuthenticationProcess(serverConnection);
        UDPListener udpListener = new UDPListener();
        new Thread(udpListener).start();
        registerUdpListener(udpListener, serverConnection);
        //UpdateWorker worker = new UpdateWorker

        serverConnection.close();
        /*Engine engine = new Engine();
        engine.start();
        Player player = new Player("player", PrimitiveMeshShape.QUAD, ShaderType.DEFAULT, 0.5, 0.3);
        GameObject floor = new GameObject(PrimitiveMeshShape.QUAD,ShaderType.DEFAULT,0.5,0.3);
        floor.setScale(10);
        floor.setTextureKey("stone_rough_yellow");
        SceneHandler.SCENE_HANDLER.setPlayer(player);
        Camera.CAMERA.setLookAtTarget(player);
        RenderHandler.RENDER_HANDLER.addToRenderQueue(floor);
        RenderHandler.RENDER_HANDLER.addToRenderQueue(player);*/

    }

    private static void registerUdpListener(UDPListener udpListener, SSLServerConnection serverConnection) {
        String listeningPort = udpListener.getPort();
        String listeningAddress = udpListener.getAddress();
        ReadyForReceivingRequest readyForReceivingRequest = ReadyForReceivingRequest.builder()
                .receivingAddress(listeningAddress)
                .receivingPort(listeningPort)
                .build();
        serverConnection.send(readyForReceivingRequest);
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
