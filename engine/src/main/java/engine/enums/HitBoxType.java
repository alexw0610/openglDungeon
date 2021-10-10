package engine.enums;

public enum HitBoxType {
    AABB("AABB"),
    CIRCLE("CIRCLE");

    private final String value;

    HitBoxType(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
