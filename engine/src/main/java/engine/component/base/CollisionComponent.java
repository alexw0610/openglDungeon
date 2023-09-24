package engine.component.base;

import engine.component.Component;
import engine.enums.HitBoxType;
import engine.object.HitBox;
import org.joml.Vector2d;

import java.util.ArrayDeque;
import java.util.Queue;

public class CollisionComponent implements Component {

    private static final long serialVersionUID = -8090166882972027605L;
    private HitBox hitBox;
    private boolean obstructsMovement;
    private String selfApplyComponents;
    private String otherApplyComponents;

    public final Queue<Vector2d> collisions = new ArrayDeque<>();

    public CollisionComponent(String hitBoxType, Double hitBoxSize) {
        this.hitBox = new HitBox(HitBoxType.valueOf(hitBoxType), hitBoxSize);
        this.obstructsMovement = true;
    }

    public CollisionComponent(String hitBoxType, Double hitBoxSize, Boolean obstructsMovement) {
        this.hitBox = new HitBox(HitBoxType.valueOf(hitBoxType), hitBoxSize);
        this.obstructsMovement = obstructsMovement;
    }

    public HitBox getHitBox() {
        return hitBox;
    }

    public void setHitBox(HitBox hitBox) {
        this.hitBox = hitBox;
    }

    public boolean isObstructsMovement() {
        return obstructsMovement;
    }

    public void setObstructsMovement(boolean obstructsMovement) {
        this.obstructsMovement = obstructsMovement;
    }

    public String getSelfApplyComponents() {
        return selfApplyComponents;
    }

    public void setSelfApplyComponents(String selfApplyComponents) {
        this.selfApplyComponents = selfApplyComponents;
    }

    public String getOtherApplyComponents() {
        return otherApplyComponents;
    }

    public void setOtherApplyComponents(String otherApplyComponents) {
        this.otherApplyComponents = otherApplyComponents;
    }

    @Override
    public void onRemove() {

    }
}
