import dto.Request;
import dto.ssl.GenericResponse;
import exception.TrustStoreException;
import util.ApplicationProperties;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

public class SSLServerConnection {
    private final ApplicationProperties applicationProperties;
    private SSLSocket sslSocket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public SSLServerConnection() {
        this.applicationProperties = new ApplicationProperties();
        SSLContext context = null;
        try {
            context = generateTrustStore();
        } catch (TrustStoreException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        try {
            createSSLSocket(context);
            createObjectIOStreams();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void createSSLSocket(SSLContext context) throws IOException {
        SSLSocketFactory factory = context.getSocketFactory();
        this.sslSocket = (SSLSocket) factory.createSocket(applicationProperties.getProperty("serverHost"),
                Integer.parseInt(applicationProperties.getProperty("serverPort")));
    }

    private void createObjectIOStreams() throws IOException {
        this.objectOutputStream = new ObjectOutputStream(sslSocket.getOutputStream());
        this.objectInputStream = new ObjectInputStream(sslSocket.getInputStream());
    }

    public GenericResponse sendAndAwait(Request request) {
        try {
            this.objectOutputStream.writeObject(request);
        } catch (IOException e) {
            System.err.println("Failed to send Object to server. " + e.getMessage());
            e.printStackTrace();
        }
        return getResponse();
    }

    public void send(Request request) {
        try {
            this.objectOutputStream.writeObject(request);
        } catch (IOException e) {
            System.err.println("Failed to send Object to server. " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            this.sslSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private GenericResponse getResponse() {
        GenericResponse genericResponse;
        while (true) {
            try {
                if ((genericResponse = (GenericResponse) objectInputStream.readObject()) != null) {
                    return genericResponse;
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Failed to read response from server. " + e.getMessage());
                e.printStackTrace();
                return GenericResponse.builder().responseStatus(false).responseText("Connection with Server failed. No valid Response!").build();
            }
        }
    }

    private SSLContext generateTrustStore() throws TrustStoreException {
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, applicationProperties.getProperty("trustStorePassword").toCharArray());
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            String certificateFileName = applicationProperties.getProperty("serverCertificateName");
            Certificate certificate = certificateFactory.generateCertificate(new FileInputStream(applicationProperties.getFileFromConfig(certificateFileName)));
            keyStore.setCertificateEntry("openglDungeonCertificate", certificate);
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            throw new TrustStoreException(e.getMessage());
        }
        return sslContext;
    }
}
