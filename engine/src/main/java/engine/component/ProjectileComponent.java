package engine.component;

import org.joml.Vector2d;

public class ProjectileComponent implements Component {

    private static final long serialVersionUID = 2474583548251974620L;
    private Vector2d direction;
    private double speed;
    private String onCollisionAttack;

    private double damageOverwrite;

    public Vector2d getDirection() {
        return direction;
    }

    public void setDirection(Vector2d direction) {
        this.direction = direction;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public String getOnCollisionAttack() {
        return onCollisionAttack;
    }

    public void setOnCollisionAttack(String onCollisionAttack) {
        this.onCollisionAttack = onCollisionAttack;
    }

    public double getDamageOverwrite() {
        return damageOverwrite;
    }

    public void setDamageOverwrite(double damageOverwrite) {
        this.damageOverwrite = damageOverwrite;
    }

    @Override
    public void onRemove() {

    }
}
