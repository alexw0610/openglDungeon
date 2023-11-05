package engine.component;

import engine.entity.Entity;
import org.joml.Vector2d;

public class
ProjectileComponent implements Component {

    private static final long serialVersionUID = 2474583548251974620L;
    private Vector2d direction;
    private double speed;
    private String onCollisionAttack;
    private AttackComponent attackComponent;
    private Entity targetEntity;

    private double lastTargetCheck;

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

    public AttackComponent getAttackComponent() {
        return attackComponent;
    }

    public void setAttackComponent(AttackComponent attackComponent) {
        this.attackComponent = attackComponent;
    }

    public Entity getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(Entity targetEntity) {
        this.targetEntity = targetEntity;
    }

    public double getLastTargetCheck() {
        return lastTargetCheck;
    }

    public void setLastTargetCheck(double lastTargetCheck) {
        this.lastTargetCheck = lastTargetCheck;
    }

    @Override
    public void onRemove() {

    }
}
