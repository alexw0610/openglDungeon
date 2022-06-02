package engine.component;

import static engine.EngineConstants.MOVEMENT_SPEED_VALUE_FACTOR;

public class StatComponent implements Component {

    private static final long serialVersionUID = 4912727537252374835L;

    private String entityName;
    private double currentHealthPoints;
    private double maxHealthPoints;
    private double movementSpeed;
    private boolean isDead;

    public StatComponent() {
        super();
        maxHealthPoints = 100.0;
        currentHealthPoints = maxHealthPoints;
        movementSpeed = 100.0;
        isDead = false;
    }

    public double getCurrentHealthpoints() {
        return this.currentHealthPoints;
    }

    public void setCurrentHealthPoints(Double healthPoints) {
        this.currentHealthPoints = healthPoints;
    }

    public void setMaxHealthPoints(Double healthPoints) {
        this.maxHealthPoints = healthPoints;
    }

    public void subtractHealthPoints(double damage) {
        this.currentHealthPoints -= damage;
    }

    public double getMovementSpeed() {
        return movementSpeed / MOVEMENT_SPEED_VALUE_FACTOR;
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
        return this.currentHealthPoints / this.maxHealthPoints;
    }
}
