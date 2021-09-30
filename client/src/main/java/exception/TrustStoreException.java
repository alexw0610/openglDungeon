package exception;

public class TrustStoreException extends Exception {

    private static final long serialVersionUID = 650635134172376001L;

    public TrustStoreException(String message) {
        super("Error generating Truststore. " + message);
    }
}
