package engine.component;

public class StatComponent implements Component {

    private static final long serialVersionUID = 4912727537252374835L;

    private String entityName;
    private double currentHealthPoints;
    private double maxHealthPoints;
    private double movementSpeed;
    private double currentArmor;
    private double maxArmor;
    private double attackSpeed;
    private double secondaryAttackSpeed;
    private double lastDashed;
    private boolean isDead;
    private double xp;
    private boolean dropsItems;
    private boolean dropsXP;
    private int level;

    public StatComponent() {
        super();
        this.maxHealthPoints = 10.0;
        this.currentHealthPoints = maxHealthPoints;
        this.movementSpeed = 100.0;
        this.attackSpeed = 1.0;
        this.secondaryAttackSpeed = 2.0;
        this.isDead = false;
        this.xp = 0;
        this.level = 1;
        this.dropsItems = true;
        this.dropsXP = true;
        this.lastDashed = 0;
        this.currentArmor = 0;
        this.maxArmor = 0;
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

    public double getAttackSpeed() {
        return attackSpeed;
    }

    public void setAttackSpeed(Double attackSpeed) {
        this.attackSpeed = Math.max(attackSpeed, 0.1);
    }

    public double getSecondaryAttackSpeed() {
        return secondaryAttackSpeed;
    }

    public void setSecondaryAttackSpeed(Double secondaryAttackSpeed) {
        this.secondaryAttackSpeed = secondaryAttackSpeed;
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

    @Override
    public String toString() {
        return "StatComponent{" +
                "maxHealthPoints=" + maxHealthPoints +
                ", movementSpeed=" + movementSpeed +
                ", attackSpeed=" + attackSpeed +
                ", level=" + level +
                '}';
    }
}
