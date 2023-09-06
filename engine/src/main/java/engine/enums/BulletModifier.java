package engine.enums;

public enum BulletModifier {

    IMPACT("impact"),
    HIGH_VELOCITY("highVelocity"),
    CORROSIVE("corrosive"),
    INCENDIARY("incendiary");
    private final String key;

    BulletModifier(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
