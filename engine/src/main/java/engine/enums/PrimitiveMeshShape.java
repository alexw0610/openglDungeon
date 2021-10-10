package engine.enums;

public enum PrimitiveMeshShape {
    TRIANGLE("TRIANGLE"),
    QUAD("QUAD");

    private final String key;

    PrimitiveMeshShape(String key) {
        this.key = key;
    }

    public String value() {
        return this.key;
    }
}
