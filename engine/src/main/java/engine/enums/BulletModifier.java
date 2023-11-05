package engine.enums;

public enum BulletModifier {

    INCENDIARY("incendiary"),
    CORROSIVE("corrosive"),
    HIGH_VELOCITY("high_velocity"),
    BLAST_WAVE("blast_wave"),
    TRIPLE_SHOT("triple_shot"),
    IMPACT("impact"),
    EXPLOSIVE("explosive"),
    DEEP_FREEZE("deep_freeze"),
    EXECUTION("execution"),
    FREEZING("freezing"),
    LIFESTEAL("lifesteal"),
    SHIELD_DISCHARGE("shield_discharge"),
    CHARGE_SHOT("charge_shot"),
    HEAT_SEEKING("heat_seeking");

    private final String key;

    BulletModifier(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
