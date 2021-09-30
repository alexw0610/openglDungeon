package engine.enums;

public enum TextureKey {
    STONE_CLEAN_SUNSET_WALL("stone_clean_sunset_wall"),
    STONE_CLEAN_SUNSET_WALL_RIGHT("stone_clean_sunset_wall_right"),
    STONE_FLOOR_PLAIN_PURPLE("stone_floor_plain_purple"),
    STONE_FLOOR_PLAIN_PURPLE_DEBRIE("stone_floor_plain_purple_debrie"),
    STONE_FLOOR_PLAIN_PURPLE_DEBRIE_SMALL("stone_floor_plain_purple_debrie_small"),
    STONE_ROUGH_PURPLE_DARK_CRACKS("stone_rough_purple_dark_cracks"),
    STONE_ROUGH_PURPLE_DARK_DIMPLES("stone_rough_purple_dark_dimples"),
    STONE_ROUGH_PURPLE_DARK_NO_HIGHLIGHTS("stone_rough_purple_dark_no_highlights"),
    DEFAULT("default");

    public final String fileName;

    TextureKey(String fileName) {
        this.fileName = fileName;
    }

    public String value() {
        return this.fileName;
    }
}
