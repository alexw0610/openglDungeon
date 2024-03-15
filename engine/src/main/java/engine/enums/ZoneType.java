package engine.enums;

public enum ZoneType {
    RUBBLE_ZONE(0),
    HIVE_ZONE(3),
    STRANGLE_ZONE(8),
    FIRE_ZONE(13);

    private final int minSpawnLevel;

    ZoneType(int value) {
        this.minSpawnLevel = value;
    }

    public int value() {
        return this.minSpawnLevel;
    }
}
