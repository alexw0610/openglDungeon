package engine.object;

import engine.object.enums.HitboxType;

public class Hitbox {

    private final HitboxType hitboxType;
    private double hitboxPositonX;
    private double hitboxPositonY;

    /**
     * either the radius of the hitbox or the side length
     * depending on the hitboxType
     */
    private double hitboxSizeParameter;

    public Hitbox(HitboxType hitboxType) {
        this.hitboxType = hitboxType;
    }

    public HitboxType getHitboxType() {
        return hitboxType;
    }

    public double getHitboxPositonX() {
        return hitboxPositonX;
    }

    public void setHitboxPositonX(double hitboxPositonX) {
        this.hitboxPositonX = hitboxPositonX;
    }

    public double getHitboxPositonY() {
        return hitboxPositonY;
    }

    public void setHitboxPositonY(double hitboxPositonY) {
        this.hitboxPositonY = hitboxPositonY;
    }

    public double getHitboxSizeParameter() {
        return hitboxSizeParameter;
    }

    public void setHitboxSizeParameter(double hitboxSizeParameter) {
        this.hitboxSizeParameter = hitboxSizeParameter;
    }
}
