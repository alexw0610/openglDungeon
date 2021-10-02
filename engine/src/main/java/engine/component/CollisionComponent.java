package engine.component;

import engine.object.HitBox;

public class CollisionComponent implements Component {

    private HitBox hitBox;

    public CollisionComponent(HitBox hitBox) {
        this.hitBox = hitBox;
    }

    public HitBox getHitBox() {
        return hitBox;
    }

    public void setHitBox(HitBox hitBox) {
        this.hitBox = hitBox;
    }
}
