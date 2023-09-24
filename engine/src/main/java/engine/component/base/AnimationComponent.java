package engine.component.base;

import engine.component.Component;

public class AnimationComponent implements Component {

    private static final long serialVersionUID = -2733185359091855469L;
    private double animationSpeed;
    private double animationRow;
    private double animationFrame;
    private boolean animationContinuous;
    private int animationLength;
    private boolean isMovementDriven;
    private boolean deleteAfterPlay;
    private boolean animationFinished;
    private double animationUpdatedLast;
    private boolean isKnockbacked;

    public AnimationComponent(Double animationSpeed) {
        this.animationSpeed = animationSpeed;
        this.animationContinuous = true;
        this.animationFinished = false;
        this.animationRow = 1;
        this.animationFrame = 1;
        this.animationLength = 0;
        this.isMovementDriven = false;
        this.deleteAfterPlay = false;
    }

    public AnimationComponent(Double animationSpeed, Boolean animationContinuous, Integer animationLength) {
        this.animationSpeed = animationSpeed;
        this.animationContinuous = animationContinuous;
        this.animationFinished = false;
        this.animationRow = 1;
        this.animationFrame = 1;
        this.animationLength = animationLength;
        this.isMovementDriven = false;
        this.deleteAfterPlay = false;
    }

    public double getAnimationSpeed() {
        return animationSpeed;
    }

    public void setAnimationSpeed(Double animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    public double getAnimationRow() {
        return animationRow;
    }

    public void setAnimationRow(Double animationRow) {
        this.animationRow = animationRow;
    }

    public double getAnimationFrame() {
        return animationFrame;
    }

    public void setAnimationFrame(Double animationFrame) {
        this.animationFrame = animationFrame;
    }

    public boolean isAnimationContinuous() {
        return animationContinuous;
    }

    public void setAnimationContinuous(Boolean animationContinuous) {
        this.animationContinuous = animationContinuous;
    }

    public boolean isAnimationFinished() {
        return animationFinished;
    }

    public void setAnimationFinished(Boolean animationFinished) {
        this.animationFinished = animationFinished;
    }

    public double getAnimationUpdatedLast() {
        return animationUpdatedLast;
    }

    public void setAnimationUpdatedLast(Double animationUpdatedLast) {
        this.animationUpdatedLast = animationUpdatedLast;
    }

    public int getAnimationLength() {
        return animationLength;
    }

    public void setAnimationLength(Integer animationLength) {
        this.animationLength = animationLength;
    }

    public boolean isMovementDriven() {
        return isMovementDriven;
    }

    public void setMovementDriven(Boolean movementDriven) {
        isMovementDriven = movementDriven;
    }

    public boolean isDeleteAfterPlay() {
        return deleteAfterPlay;
    }

    public void setDeleteAfterPlay(Boolean deleteAfterPlay) {
        this.deleteAfterPlay = deleteAfterPlay;
    }

    public boolean isKnockbacked() {
        return isKnockbacked;
    }

    public void setKnockbacked(boolean knockbacked) {
        isKnockbacked = knockbacked;
    }

    @Override
    public void onRemove() {

    }
}
