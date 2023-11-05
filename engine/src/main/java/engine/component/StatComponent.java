package engine.component;

import java.util.ArrayList;
import java.util.List;

public class StatComponent implements Component {

    private static final long serialVersionUID = 4912727537252374835L;

    private String entityName;
    private double currentHealthPoints;
    private double maxHealthPoints;
    private double movementSpeed;
    private double movementSpeedModifier;
    private double currentShield;
    private double maxShield;
    private double attackSpeedPrimary;
    private double attackSpeedSecondary;
    private double bulletSpeedPrimary;
    private double bulletSpeedSecondary;
    private double lastAttackPrimary;
    private double lastAttackSecondary;
    private double attackCountPrimary;
    private double attackCountSecondary;
    private double baseDamagePrimary;
    private double baseDamageSecondary;
    private double lastDashed;
    private double dashCooldownSpeed;
    private double dashStunDuration;
    private boolean isDead;
    private double xp;
    private boolean dropsItems;
    private boolean dropsXP;
    private int level;
    private final List<UpgradeComponent> upgrades;
    private final List<GunComponent> guns;
    private GunComponent equippedGun;
    private double critChance;
    private double critBonusDamage;

    public StatComponent() {
        super();
        this.maxHealthPoints = 10.0;
        this.currentHealthPoints = maxHealthPoints;
        this.movementSpeed = 100.0;
        this.attackSpeedPrimary = 1.0;
        this.attackSpeedSecondary = 1.0;
        this.isDead = false;
        this.xp = 0;
        this.level = 1;
        this.dropsItems = true;
        this.dropsXP = true;
        this.lastDashed = 0;
        this.currentShield = 0;
        this.maxShield = 0;
        this.bulletSpeedPrimary = 1.0;
        this.bulletSpeedSecondary = 1.0;
        this.baseDamagePrimary = 1.0;
        this.baseDamageSecondary = 1.0;
        this.upgrades = new ArrayList<>();
        this.guns = new ArrayList<>();
        this.equippedGun = null;
        this.dashCooldownSpeed = 4.0;
        this.dashStunDuration = 0.0;
        this.critChance = 0.0;
        this.critBonusDamage = 1.0;
        this.movementSpeedModifier = 1.0;
    }

    public void unequipGunUpgrades() {
        if (this.equippedGun != null) {
            UpgradeComponent upgradePrimA = this.getEquippedGun().getPrimaryModifierSlotA();
            this.addUpgrade(upgradePrimA);
            this.equippedGun.setPrimaryModifierSlotA(null);
            UpgradeComponent upgradePrimB = this.getEquippedGun().getPrimaryModifierSlotB();
            this.addUpgrade(upgradePrimB);
            this.equippedGun.setPrimaryModifierSlotB(null);
            UpgradeComponent upgradeSecA = this.getEquippedGun().getSecondaryModifierSlotA();
            this.addUpgrade(upgradeSecA);
            this.equippedGun.setSecondaryModifierSlotA(null);
            UpgradeComponent upgradeSecB = this.getEquippedGun().getSecondaryModifierSlotB();
            this.addUpgrade(upgradeSecB);
            this.equippedGun.setSecondaryModifierSlotB(null);
        }
    }

    public double getCurrentHealthPoints() {
        return this.currentHealthPoints;
    }

    public void setCurrentHealthPoints(Double healthPoints) {
        this.currentHealthPoints = Math.min(healthPoints, this.maxHealthPoints);
    }

    public void healToFull() {
        this.currentHealthPoints = this.maxHealthPoints;
        this.currentShield = this.maxShield;
    }

    public void setMaxHealthPoints(Double healthPoints) {
        this.maxHealthPoints = healthPoints;
        this.currentHealthPoints = this.maxHealthPoints;
    }

    public void subtractHealthPoints(Double damage) {
        this.currentHealthPoints = Math.max(this.currentHealthPoints - damage, 0.0);
    }

    public void subtractShieldPoints(Double damage) {
        this.currentShield = Math.max(this.currentShield - damage, 0.0);
    }

    public double getMovementSpeed() {
        return movementSpeed;
    }

    public void setMovementSpeed(Double movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public double getHealthPercentage() {
        return Math.max(this.currentHealthPoints / this.maxHealthPoints, 0);
    }

    public double getXPPercentage() {
        return Math.min(this.xp / (100 * (Math.log(level + 1))), 1.0);
    }

    public double getShieldPercentage() {
        return Math.max(this.currentShield / this.maxShield, 0);
    }

    public double getAttackSpeedPrimary() {
        return attackSpeedPrimary;
    }

    public void setAttackSpeedPrimary(Double attackSpeedPrimary) {
        this.attackSpeedPrimary = Math.max(attackSpeedPrimary, 0.1);
    }

    public double getAttackSpeedSecondary() {
        return attackSpeedSecondary;
    }

    public void setAttackSpeedSecondary(Double attackSpeedSecondary) {
        this.attackSpeedSecondary = attackSpeedSecondary;
    }

    public double getXp() {
        return xp;
    }

    public void setXp(Double xp) {
        this.xp = xp;
    }

    public double getMaxHealthPoints() {
        return maxHealthPoints;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isDropsItems() {
        return dropsItems;
    }

    public void setDropsItems(Boolean dropsItems) {
        this.dropsItems = dropsItems;
    }

    public boolean isDropsXP() {
        return dropsXP;
    }

    public void setDropsXP(Boolean dropsXP) {
        this.dropsXP = dropsXP;
    }

    public double getLastDashed() {
        return lastDashed;
    }

    public void setLastDashed(double lastDashed) {
        this.lastDashed = lastDashed;
    }

    public double getCurrentShield() {
        return currentShield;
    }

    public void setCurrentShield(Double currentShield) {
        this.currentShield = Math.min(currentShield, this.maxShield);
    }

    public double getMaxShield() {
        return maxShield;
    }

    public void setMaxShield(Double maxShield) {
        this.maxShield = maxShield;
        this.currentShield = maxShield;
    }

    public double getBulletSpeedPrimary() {
        return bulletSpeedPrimary;
    }

    public void setBulletSpeedPrimary(Double bulletSpeedPrimary) {
        this.bulletSpeedPrimary = bulletSpeedPrimary;
    }

    public double getBulletSpeedSecondary() {
        return bulletSpeedSecondary;
    }

    public void setBulletSpeedSecondary(Double bulletSpeedSecondary) {
        this.bulletSpeedSecondary = bulletSpeedSecondary;
    }

    public double getLastAttackPrimary() {
        return lastAttackPrimary;
    }

    public void setLastAttackPrimary(double lastAttackPrimary) {
        this.lastAttackPrimary = lastAttackPrimary;
    }

    public double getLastAttackSecondary() {
        return lastAttackSecondary;
    }

    public void setLastAttackSecondary(double lastAttackSecondary) {
        this.lastAttackSecondary = lastAttackSecondary;
    }

    public double getAttackCountPrimary() {
        return attackCountPrimary;
    }

    public void setAttackCountPrimary(Double attackCountPrimary) {
        this.attackCountPrimary = attackCountPrimary;
    }

    public double getAttackCountSecondary() {
        return attackCountSecondary;
    }

    public void setAttackCountSecondary(Double attackCountSecondary) {
        this.attackCountSecondary = attackCountSecondary;
    }

    public double getBaseDamagePrimary() {
        return baseDamagePrimary;
    }

    public void setBaseDamagePrimary(Double baseDamagePrimary) {
        this.baseDamagePrimary = baseDamagePrimary;
    }

    public double getBaseDamageSecondary() {
        return baseDamageSecondary;
    }

    public void setBaseDamageSecondary(Double baseDamageSecondary) {
        this.baseDamageSecondary = baseDamageSecondary;
    }

    public List<UpgradeComponent> getUpgrades() {
        return upgrades;
    }

    public void addUpgrade(UpgradeComponent upgradeComponent) {
        if (upgradeComponent != null) {
            this.upgrades.add(upgradeComponent);
        }
    }

    public void removeUpgrade(UpgradeComponent upgradeComponent) {
        if (upgradeComponent != null) {
            this.upgrades.remove(upgradeComponent);
        }
    }

    public List<GunComponent> getGuns() {
        return guns;
    }

    public void addGuns(GunComponent gunComponent) {
        this.guns.add(gunComponent);
    }

    public void setEquippedGun(GunComponent gunComponent) {
        this.equippedGun = gunComponent;
    }

    public GunComponent getEquippedGun() {
        return equippedGun;
    }

    public double getDashCooldownSpeed() {
        return dashCooldownSpeed;
    }

    public void setDashCooldownSpeed(Double dashCooldownSpeed) {
        this.dashCooldownSpeed = dashCooldownSpeed;
    }

    public double getDashStunDuration() {
        return dashStunDuration;
    }

    public void setDashStunDuration(double dashStunDuration) {
        this.dashStunDuration = dashStunDuration;
    }

    public double getCritChance() {
        return critChance;
    }

    public void setCritChance(Double critChance) {
        this.critChance = Math.min(critChance, 1.0);
    }

    public double getCritBonusDamage() {
        return critBonusDamage;
    }

    public void setCritBonusDamage(Double critBonusDamage) {
        this.critBonusDamage = critBonusDamage;
    }

    public double getMovementSpeedModifier() {
        return movementSpeedModifier;
    }

    public void setMovementSpeedModifier(double movementSpeedModifier) {
        this.movementSpeedModifier = movementSpeedModifier;
    }

    @Override
    public String toString() {
        return "StatComponent{" +
                "maxHealthPoints=" + maxHealthPoints +
                ", movementSpeed=" + movementSpeed +
                ", attackSpeed=" + attackSpeedPrimary +
                ", level=" + level +
                '}';
    }

    @Override
    public void onRemove() {

    }
}
