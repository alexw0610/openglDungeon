package exception;

public class TrustStoreException extends Exception {

    public TrustStoreException(String message){
        super("Error generating Truststore. "+message);
    }
}
