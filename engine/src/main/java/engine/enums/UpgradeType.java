package engine.enums;

public enum UpgradeType {

    MAX_HEALTH("maxHealth"),
    ATTACK_SPEED("attackSpeed"),
    MOVEMENT_SPEED("movementSpeed"),
    MAX_ARMOR("maxArmor"),
    BULLET_VELOCITY("bulletVelocity"),
    IMPACT_BULLET("impactBullet");

    private final String key;

    UpgradeType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
