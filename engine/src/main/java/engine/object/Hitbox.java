package engine.object;

import engine.enums.HitboxType;

public class Hitbox {

    private final HitboxType hitboxType;
    private final double size;

    public Hitbox(HitboxType hitboxType, double size) {
        this.hitboxType = hitboxType;
        this.size = size;
    }

    public Hitbox(HitboxType hitboxType) {
        this.hitboxType = hitboxType;
        this.size = 1.0;
    }

    public HitboxType getHitboxType() {
        return hitboxType;
    }

    public double getSize() {
        return size;
    }
}
