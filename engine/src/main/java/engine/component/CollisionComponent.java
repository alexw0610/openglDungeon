package engine.component;

import engine.component.lambda.OnCollisionFunction;
import engine.object.HitBox;

public class CollisionComponent implements Component {

    private HitBox hitBox;
    private OnCollisionFunction onCollisionFunction;

    public CollisionComponent(HitBox hitBox) {
        this.hitBox = hitBox;
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
}
