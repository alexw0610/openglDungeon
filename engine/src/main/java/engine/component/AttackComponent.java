package engine.component;

import engine.enums.BulletModifier;

import java.util.LinkedList;
import java.util.List;

import static engine.EngineConstants.KNOCKBACK_VALUE_FACTOR;

public class AttackComponent implements Component {
    private static final long serialVersionUID = -4310546471445337516L;
    private final String attackName;
    private boolean isAoE;
    private boolean isSingleTarget;
    private boolean isPlaceable;
    private double damage;
    private double range;
    private double knockback;
    private boolean stunsTarget;
    private boolean affectsTerrain;
    private String targetEntity;
    private String texture;
    private Class<Component> targetComponentConstraint;
    private List<BulletModifier> bulletModifierList;
    private double stunDuration;

    private double criticalHitChance;
    private double criticalBonusModifier;

    public AttackComponent(String name) {
        this.attackName = name;
        this.isAoE = false;
        this.isSingleTarget = false;
        this.isPlaceable = false;
        this.damage = 0;
        this.range = 0;
        this.knockback = 0;
        this.affectsTerrain = false;
        this.stunsTarget = false;
        this.criticalHitChance = 0.0;
        this.stunDuration = 0.0;
        this.bulletModifierList = new LinkedList<>();
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

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public boolean isAffectsTerrain() {
        return affectsTerrain;
    }

    public void setAffectsTerrain(Boolean affectsTerrain) {
        this.affectsTerrain = affectsTerrain;
    }

    public boolean isStunsTarget() {
        return stunsTarget;
    }

    public void setStunsTarget(Boolean stunsTarget) {
        this.stunsTarget = stunsTarget;
    }

    public double getCriticalHitChance() {
        return criticalHitChance;
    }

    public void setCriticalHitChance(Double criticalHitChance) {
        this.criticalHitChance = criticalHitChance;
    }

    public double getCriticalBonusModifier() {
        return criticalBonusModifier;
    }

    public void setCriticalBonusModifier(double criticalBonusModifier) {
        this.criticalBonusModifier = criticalBonusModifier;
    }

    public double getStunDuration() {
        return stunDuration;
    }

    public void setStunDuration(Double stunDuration) {
        this.stunDuration = stunDuration;
    }

    public List<BulletModifier> getBulletModifierList() {
        return bulletModifierList;
    }

    public void addBulletModifierToList(BulletModifier bulletModifier) {
        this.bulletModifierList.add(bulletModifier);
    }

    @Override
    public void onRemove() {

    }
}