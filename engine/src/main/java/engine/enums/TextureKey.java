package engine.enums;

public enum TextureKey {
    FLOOR_RED_PLATES_DEBRIS("floor_red_plates_debris"),
    WALL_AQUA_BRICK("wall_aqua_brick"),
    LANTERN_HANGING("lantern_hanging"),
    DEFAULT("default");

    public final String fileName;

    TextureKey(String fileName) {
        this.fileName = fileName;
    }

    public String value() {
        return this.fileName;
    }
}
