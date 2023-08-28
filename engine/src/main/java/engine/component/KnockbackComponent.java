package engine.component;

import org.joml.Vector2d;

public class KnockbackComponent implements Component {
    private static final long serialVersionUID = -4481923690637880033L;

    private Vector2d knockbackDirection;
    private double knockbackVelocity;
    private final double knockbackStartTime;
    private boolean hasCollided;

    public KnockbackComponent(Vector2d dashDirection, double dashVelocity) {
        this.knockbackDirection = dashDirection;
        this.knockbackVelocity = dashVelocity;
        this.knockbackStartTime = System.nanoTime();
        this.hasCollided = false;
    }

    public Vector2d getKnockbackDirection() {
        return knockbackDirection;
    }

    public void setKnockbackDirection(Vector2d knockbackDirection) {
        this.knockbackDirection = knockbackDirection;
    }

    public double getKnockbackVelocity() {
        return knockbackVelocity;
    }

    public void setKnockbackVelocity(double knockbackVelocity) {
        this.knockbackVelocity = knockbackVelocity;
    }

    public double getKnockbackStartTime() {
        return knockbackStartTime;
    }

    public boolean hasCollided() {
        return hasCollided;
    }

    public void setHasCollided() {
        this.hasCollided = true;
    }
}
