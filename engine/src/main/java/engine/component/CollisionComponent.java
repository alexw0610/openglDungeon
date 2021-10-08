package engine.component;

import engine.component.lambda.OnCollisionFunction;
import engine.object.HitBox;

public class CollisionComponent implements Component {

    private HitBox hitBox;
    private OnCollisionFunction onCollisionFunction;
    private boolean obstructsMovement;

    public CollisionComponent(HitBox hitBox) {
        this.hitBox = hitBox;
        this.obstructsMovement = true;
    }

    public CollisionComponent(HitBox hitBox, boolean obstructsMovement) {
        this.hitBox = hitBox;
        this.obstructsMovement = obstructsMovement;
    }

    public HitBox getHitBox() {
        return hitBox;
    }

    public void setHitBox(HitBox hitBox) {
        this.hitBox = hitBox;
    }

    public OnCollisionFunction getOnCollisionFunction() {
        return onCollisionFunction;
    }

    public void setOnCollisionFunction(OnCollisionFunction onCollisionFunction) {
        this.onCollisionFunction = onCollisionFunction;
    }

    public boolean isObstructsMovement() {
        return obstructsMovement;
    }

    public void setObstructsMovement(boolean obstructsMovement) {
        this.obstructsMovement = obstructsMovement;
    }
}
