package engine.component;

public class PlayerComponent implements Component {
    private double movementSpeed = 0.1;

    public double getMovementSpeed() {
        return movementSpeed;
    }

    public void setMovementSpeed(double movementSpeed) {
        this.movementSpeed = movementSpeed;
    }
}
