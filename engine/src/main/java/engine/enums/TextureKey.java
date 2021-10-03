package engine.enums;

public enum TextureKey {
    FLOOR_RED_PLATES_DEBRIE_001("floor_red_plates_debrie_001"),
    FLOOR_RED_PLATES_DEBRIE_002("floor_red_plates_debrie_002"),
    FLOOR_RED_PLATES_DEBRIE_003("floor_red_plates_debrie_003"),
    FLOOR_RED_PLATES_DEBRIE_004("floor_red_plates_debrie_004"),
    FLOOR_RED_PLATES_DEBRIE_005("floor_red_plates_debrie_005"),
    FLOOR_RED_PLATES_DEBRIE_006("floor_red_plates_debrie_006"),
    FLOOR_RED_PLATES_DEBRIE_007("floor_red_plates_debrie_007"),
    WALL_AQUA_BRICK("wall_aqua_brick"),
    WALL_AQUA_BRICK_BOTTOM("wall_aqua_brick_bottom"),
    WALL_AQUA_BRICK_RIGHT("wall_aqua_brick_right"),
    WALL_AQUA_BRICK_LEFT_RIGHT("wall_aqua_brick_left_right"),
    WALL_AQUA_BRICK_LEFT("wall_aqua_brick_left"),

    ASSET_FIRE_PLACE_01("asset_fire_place_01"),

    DEFAULT("default");

    public final String fileName;

    TextureKey(String fileName) {
        this.fileName = fileName;
    }

    public String value() {
        return this.fileName;
    }
}
