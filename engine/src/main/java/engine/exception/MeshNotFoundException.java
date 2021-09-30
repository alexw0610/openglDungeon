package engine.exception;

public class MeshNotFoundException extends Exception {
    private static final long serialVersionUID = 1978515539928668977L;

    public MeshNotFoundException(String errorMessage, Throwable e, String... params) {
        super(String.format(errorMessage, params), e);
    }
}
