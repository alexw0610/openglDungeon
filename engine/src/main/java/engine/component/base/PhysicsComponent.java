package engine.component.base;

import engine.component.Component;
import org.joml.Vector2d;

public class PhysicsComponent implements Component {

    private static final long serialVersionUID = 6962944663306622171L;
    private double momentumX;
    private double momentumY;
    private boolean gravity;
    private Vector2d moveToTarget;

    private double lastDash;

    public double getMomentumX() {
        return momentumX;
    }

    public void setMomentumX(double momentumX) {
        this.momentumX = momentumX;
    }

    public double getMomentumY() {
        return momentumY;
    }

    public void setMomentumY(double momentumY) {
        this.momentumY = momentumY;
    }

    public boolean isGravity() {
        return gravity;
    }

    public void setGravity(boolean gravity) {
        this.gravity = gravity;
    }

    public Vector2d getMoveToTarget() {
        return moveToTarget;
    }

    public void setMoveToTarget(Vector2d moveToTarget) {
        this.moveToTarget = moveToTarget;
    }

    public double getLastDash() {
        return lastDash;
    }

    public void setLastDash(double lastDash) {
        this.lastDash = lastDash;
    }
}
