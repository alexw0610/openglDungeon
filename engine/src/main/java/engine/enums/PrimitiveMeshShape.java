package engine.enums;

public enum PrimitiveMeshShape {
    TRIANGLE("triangle"),
    QUAD("quad");

    private final String key;

    PrimitiveMeshShape(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
