package engine.component;

import org.joml.Vector2d;

public class DashComponent implements Component {
    private static final long serialVersionUID = -2698391729424249087L;

    private Vector2d dashDirection;
    private double dashVelocity;
    private final double dashStartedGameTimeNano;

    private boolean hasCollided;

    public DashComponent(Vector2d dashDirection, double dashVelocity) {
        this.dashDirection = dashDirection;
        this.dashVelocity = dashVelocity;
        this.dashStartedGameTimeNano = System.nanoTime();
        this.hasCollided = false;
    }

    public Vector2d getDashDirection() {
        return dashDirection;
    }

    public void setDashDirection(Vector2d dashDirection) {
        this.dashDirection = dashDirection;
    }

    public double getDashVelocity() {
        return dashVelocity;
    }

    public void setDashVelocity(double dashVelocity) {
        this.dashVelocity = dashVelocity;
    }

    public double getDashStartedGameTimeNano() {
        return dashStartedGameTimeNano;
    }

    public boolean hasCollided() {
        return hasCollided;
    }

    public void setHasCollided() {
        this.hasCollided = true;
    }
}
