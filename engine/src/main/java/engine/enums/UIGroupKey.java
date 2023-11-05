package engine.enums;

public enum UIGroupKey {
    STATS("STATS"),
    HUD("HUD"),
    INVENTORY("INVENTORY"),
    CLOSE_DIALOG("CLOSE_DIALOG"),
    GAME_OVER_DIALOG("GAME_OVER_DIALOG"),
    GENERAL("GENERAL");

    final String key;

    UIGroupKey(String key) {
        this.key = key;
    }
}
