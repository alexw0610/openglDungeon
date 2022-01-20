package engine.component;

import static engine.EngineConstants.KNOCKBACK_VALUE_FACTOR;

public class AttackComponent implements Component {
    private final String attackName;
    private boolean isAoE;
    private boolean isSingleTarget;
    private boolean isPlaceable;
    private double damage;
    private double range;
    private double knockback;
    private String targetEntity;
    private Class<Component> targetComponentConstraint;

    public AttackComponent(String name) {
        attackName = name;
        isAoE = false;
        isSingleTarget = false;
        isPlaceable = false;
        damage = 0;
        range = 0;
        knockback = 0;
    }

    public String getAttackName() {
        return attackName;
    }

    public boolean isAoE() {
        return isAoE;
    }

    public void setAoE(Boolean aoE) {
        isAoE = aoE;
    }

    public boolean isSingleTarget() {
        return isSingleTarget;
    }

    public void setSingleTarget(Boolean singleTarget) {
        isSingleTarget = singleTarget;
    }

    public boolean isPlaceable() {
        return isPlaceable;
    }

    public void setPlaceable(Boolean placeable) {
        isPlaceable = placeable;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(Double damage) {
        this.damage = damage;
    }

    public double getRange() {
        return range;
    }

    public void setRange(Double range) {
        this.range = range;
    }

    public String getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(String targetEntity) {
        this.targetEntity = targetEntity;
    }

    public double getKnockback() {
        return knockback / KNOCKBACK_VALUE_FACTOR;
    }

    public void setKnockback(Double knockback) {
        this.knockback = knockback;
    }

    public Class<Component> getTargetComponentConstraint() {
        return targetComponentConstraint;
    }

    public void setTargetComponentConstraint(Class targetComponentConstraint) {
        this.targetComponentConstraint = targetComponentConstraint;
    }
}