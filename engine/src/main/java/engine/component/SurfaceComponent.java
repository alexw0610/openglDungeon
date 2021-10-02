package engine.component;

import engine.object.HitBox;

public class SurfaceComponent implements Component {
    private HitBox hitBox;

    public SurfaceComponent(HitBox hitBox) {
        this.hitBox = hitBox;
    }

    public HitBox getHitBox() {
        return hitBox;
    }

    public void setHitBox(HitBox hitBox) {
        this.hitBox = hitBox;
    }
}
