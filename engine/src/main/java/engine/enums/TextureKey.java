package engine.enums;

public enum TextureKey {
    FLOOR_RED_PLATES_DEBRIS("floor_red_plates_debris"),
    FLOOR_PURPLE_GREY_PLATES("floor_purple_grey_plates"),
    WALL_AQUA_BRICK("wall_aqua_brick"),
    LANTERN_HANGING("lantern_hanging"),
    ORB_AQUA("orb_aqua"),
    SKELETON_PILE("skeleton_pile"),
    ENEMY_MONK("enemy_monk"),
    DEFAULT("default");

    public final String fileName;

    TextureKey(String fileName) {
        this.fileName = fileName;
    }

    public String value() {
        return this.fileName;
    }
}
