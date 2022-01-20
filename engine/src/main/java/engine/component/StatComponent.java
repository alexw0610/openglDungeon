package engine.component;

import static engine.EngineConstants.MOVEMENT_SPEED_VALUE_FACTOR;

public class StatComponent implements Component {

    private double healthPoints;
    private double movementSpeed;
    private boolean isDead;

    public StatComponent() {
        super();
        healthPoints = 100.0;
        movementSpeed = 100.0;
        isDead = false;
    }

    public double getHealthPoints() {
        return healthPoints;
    }

    public void setHealthPoints(Double healthPoints) {
        this.healthPoints = healthPoints;
    }

    public void subtractHealthPoints(double damage) {
        this.healthPoints -= damage;
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
}
