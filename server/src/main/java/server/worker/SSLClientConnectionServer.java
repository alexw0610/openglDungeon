package server.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ApplicationProperties;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;

public class SSLClientConnectionServer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(SSLClientConnectionServer.class);
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
                LOG.error("Error while waiting for a new connection. Exiting.");
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
            LOG.error("Error creating SSLServerSocket. Exiting. {}", e.getMessage());
            System.exit(1);
        }
        return sslserversocket;
    }
}
