package engine.component;

import engine.enums.HitBoxType;
import engine.object.HitBox;

public class CollisionComponent implements Component {

    private HitBox hitBox;
    private boolean obstructsMovement;
    private String selfApplyComponents;
    private String otherApplyComponents;

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
}
