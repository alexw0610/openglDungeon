package server.worker;

import util.ApplicationProperties;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;

public class SSLClientConnectionServer implements Runnable {

    private static final ApplicationProperties applicationProperties = new ApplicationProperties();
    private final SSLServerSocket sslServerSocket;
    private final String udpRecPort;

    public SSLClientConnectionServer(String udpRecPort) {
        this.sslServerSocket = getSslServerSocket();
        this.udpRecPort = udpRecPort;
    }

    @Override
    public void run() {
        while (true) {
            SSLSocket sslsocket = null;
            try {
                sslsocket = (SSLSocket) sslServerSocket.accept();
            } catch (IOException e) {
                System.err.println("Error while waiting for a new connection.");
                System.exit(1);
            }
            SSLClientConnectionWorker sslClientConnectionWorker = new SSLClientConnectionWorker(sslsocket, udpRecPort);
            new Thread(sslClientConnectionWorker).start();
        }
    }

    private static SSLServerSocket getSslServerSocket() {
        SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        SSLServerSocket sslserversocket = null;
        try {
            sslserversocket = (SSLServerSocket) factory.createServerSocket(Integer.parseInt(applicationProperties.getProperty("port")));
        } catch (IOException e) {
            System.err.println("Error creating SSLServerSocket. " + e.getMessage());
            System.exit(1);
        }
        return sslserversocket;
    }
}
