package engine.enums;

public enum Slot {

    PRIMARY("primary"),
    SECONDARY("secondary");
    private final String key;
    Slot(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
