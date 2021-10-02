package engine.object;

import engine.enums.HitBoxType;

public class HitBox {

    private final HitBoxType hitBoxType;
    private final double size;

    public HitBox(HitBoxType hitBoxType, double size) {
        this.hitBoxType = hitBoxType;
        this.size = size;
    }

    public HitBox(HitBoxType hitboxType) {
        this.hitBoxType = hitboxType;
        this.size = 1.0;
    }

    public HitBoxType getHitBoxType() {
        return hitBoxType;
    }

    public double getSize() {
        return size;
    }
}
