package engine.enums;

public enum UpgradeType {

    MAX_HEALTH("maxHealth"),
    ATTACK_SPEED("attackSpeed"),
    ATTACK_DAMAGE("attackDamage"),
    MOVEMENT_SPEED("movementSpeed"),
    MAX_SHIELD("maxShield"),
    BULLET_VELOCITY("bulletVelocity"),
    CRITICAL_STRIKE_CHANCE("criticalStrikeChance"),
    CRITICAL_STRIKE_DAMAGE("criticalStrikeDamage"),
    DASH_COOLDOWN("dashCooldown"),
    DASH_STUN("dashStun");

    private final String key;

    UpgradeType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
