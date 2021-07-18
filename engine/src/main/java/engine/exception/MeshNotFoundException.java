package engine.exception;

public class MeshNotFoundException extends Exception {
    public MeshNotFoundException(String errorMessage, Throwable e, String... params) {
        super(String.format(errorMessage,params), e);
    }
}
