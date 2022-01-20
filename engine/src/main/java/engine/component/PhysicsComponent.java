package engine.component;

import org.joml.Vector2d;

public class PhysicsComponent implements Component {

    private double momentumX;
    private double momentumY;
    private boolean gravity;
    private Vector2d moveToTarget;

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
}
