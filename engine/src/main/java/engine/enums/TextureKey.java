package engine.enums;

public enum TextureKey {
    DEFAULT("default"),
    FONT("font");

    public final String fileName;

    TextureKey(String fileName) {
        this.fileName = fileName;
    }

    public String value() {
        return this.fileName;
    }
}
