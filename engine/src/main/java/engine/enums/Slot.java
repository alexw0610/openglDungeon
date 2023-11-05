package engine.enums;

public enum Slot {

    PRIMARY("Primary"),
    SECONDARY("Secondary");
    private final String key;

    Slot(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
