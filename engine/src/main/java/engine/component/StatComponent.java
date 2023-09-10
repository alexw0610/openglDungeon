package engine.component;

import java.util.ArrayList;
import java.util.List;

public class StatComponent implements Component {

    private static final long serialVersionUID = 4912727537252374835L;

    private String entityName;
    private double currentHealthPoints;
    private double maxHealthPoints;
    private double movementSpeed;
    private double currentArmor;
    private double maxArmor;
    private double attackSpeedPrimary;
    private double attackSpeedSecondary;
    private double bulletSpeedPrimary;
    private double bulletSpeedSecondary;
    private double lastShotPrimary;
    private double lastShotSecondary;
    private double bulletCountPrimary;
    private double bulletCountSecondary;
    private double baseBulletDamagePrimary;
    private double baseBulletDamageSecondary;
    private double lastDashed;
    private boolean isDead;
    private double xp;
    private boolean dropsItems;
    private boolean dropsXP;
    private int level;
    private final List<UpgradeComponent> upgrades;
    private final List<GunComponent> guns;

    private GunComponent equipedGun;

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
        this.currentArmor = 0;
        this.maxArmor = 0;
        this.bulletSpeedPrimary = 1.0;
        this.bulletSpeedSecondary = 1.0;
        this.baseBulletDamagePrimary = 1.0;
        this.baseBulletDamageSecondary = 1.0;
        this.upgrades = new ArrayList<>();
        this.guns = new ArrayList<>();
        this.equipedGun = null;
    }

    public double getCurrentHealthpoints() {
        return this.currentHealthPoints;
    }

    public void setCurrentHealthPoints(Double healthPoints) {
        this.currentHealthPoints = healthPoints;
    }

    public void healToFull() {
        this.currentHealthPoints = this.maxHealthPoints;
        this.currentArmor = this.maxArmor;
    }

    public void setMaxHealthPoints(Double healthPoints) {
        this.maxHealthPoints = healthPoints;
        this.currentHealthPoints = this.maxHealthPoints;
    }

    public void subtractHealthPoints(double damage) {
        this.currentHealthPoints = Math.max(this.currentHealthPoints - damage, 0.0);
    }

    public void subtractArmorPoints(double damage) {
        this.currentArmor = Math.max(this.currentArmor - damage, 0.0);
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

    public double getArmorPercentage() {
        return Math.max(this.currentArmor / this.maxArmor, 0);
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

    public void setXp(double xp) {
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

    public double getCurrentArmor() {
        return currentArmor;
    }

    public void setCurrentArmor(double currentArmor) {
        this.currentArmor = currentArmor;
    }

    public double getMaxArmor() {
        return maxArmor;
    }

    public void setMaxArmor(double maxArmor) {
        this.maxArmor = maxArmor;
        this.currentArmor = maxArmor;
    }

    public double getBulletSpeedPrimary() {
        return bulletSpeedPrimary;
    }

    public void setBulletSpeedPrimary(double bulletSpeedPrimary) {
        this.bulletSpeedPrimary = bulletSpeedPrimary;
    }

    public double getBulletSpeedSecondary() {
        return bulletSpeedSecondary;
    }

    public void setBulletSpeedSecondary(double bulletSpeedSecondary) {
        this.bulletSpeedSecondary = bulletSpeedSecondary;
    }

    public double getLastShotPrimary() {
        return lastShotPrimary;
    }

    public void setLastShotPrimary(double lastShotPrimary) {
        this.lastShotPrimary = lastShotPrimary;
    }

    public double getLastShotSecondary() {
        return lastShotSecondary;
    }

    public void setLastShotSecondary(double lastShotSecondary) {
        this.lastShotSecondary = lastShotSecondary;
    }

    public double getBulletCountPrimary() {
        return bulletCountPrimary;
    }

    public void setBulletCountPrimary(double bulletCountPrimary) {
        this.bulletCountPrimary = bulletCountPrimary;
    }

    public double getBulletCountSecondary() {
        return bulletCountSecondary;
    }

    public void setBulletCountSecondary(double bulletCountSecondary) {
        this.bulletCountSecondary = bulletCountSecondary;
    }

    public double getBaseBulletDamagePrimary() {
        return baseBulletDamagePrimary;
    }

    public void setBaseBulletDamagePrimary(double baseBulletDamagePrimary) {
        this.baseBulletDamagePrimary = baseBulletDamagePrimary;
    }

    public double getBaseBulletDamageSecondary() {
        return baseBulletDamageSecondary;
    }

    public void setBaseBulletDamageSecondary(double baseBulletDamageSecondary) {
        this.baseBulletDamageSecondary = baseBulletDamageSecondary;
    }

    public List<UpgradeComponent> getUpgrades() {
        return upgrades;
    }

    public void addUpgrade(UpgradeComponent upgradeComponent) {
        if(upgradeComponent != null){
            this.upgrades.add(upgradeComponent);
        }
    }

    public void removeUpgrade(UpgradeComponent upgradeComponent) {
        if(upgradeComponent != null){
            this.upgrades.remove(upgradeComponent);
        }
    }

    public List<GunComponent> getGuns() {
        return guns;
    }

    public void addGuns(GunComponent gunComponent) {
        this.guns.add(gunComponent);
    }

    public void setEquipedGun(GunComponent gunComponent) {
        this.equipedGun = gunComponent;
    }

    public GunComponent getEquipedGun() {
        return equipedGun;
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
}
