package engine.component;

public class PlayerComponent implements Component {
    private double movementSpeed = 0.070;
    private double lastDash = 0;

    public double getMovementSpeed() {
        return movementSpeed;
    }

    public void setMovementSpeed(double movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    public double getLastDash() {
        return lastDash;
    }

    public void setLastDash(double lastDash) {
        this.lastDash = lastDash;
    }
}
