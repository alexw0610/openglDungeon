package engine.enums;

public enum UIGroupKey {
    STATS("STATS"),
    HUD("HUD"),
    INVENTORY("INVENTORY"),
    GENERAL("GENERAL");

    final String key;

    UIGroupKey(String key) {
        this.key = key;
    }
}
