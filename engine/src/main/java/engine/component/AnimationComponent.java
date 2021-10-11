package engine.component;

public class AnimationComponent implements Component {

    private double animationSpeed;
    private double animationRow;
    private double animationFrame;
    private boolean animationContinuous;
    private double animationLength;
    private boolean isMovementDriven;

    private boolean animationFinished;
    private double animationUpdatedLast;

    public AnimationComponent(Double animationSpeed) {
        this.animationSpeed = animationSpeed;
        this.animationContinuous = true;
        this.animationFinished = false;
        this.animationRow = 1;
        this.animationFrame = 1;
        this.animationLength = 0;
        this.isMovementDriven = false;
    }

    public AnimationComponent(Double animationSpeed, Boolean animationContinuous, Double animationLength) {
        this.animationSpeed = animationSpeed;
        this.animationContinuous = animationContinuous;
        this.animationFinished = false;
        this.animationRow = 1;
        this.animationFrame = 1;
        this.animationLength = animationLength;
        this.isMovementDriven = false;
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

    public double getAnimationLength() {
        return animationLength;
    }

    public void setAnimationLength(Double animationLength) {
        this.animationLength = animationLength;
    }

    public boolean isMovementDriven() {
        return isMovementDriven;
    }

    public void setMovementDriven(Boolean movementDriven) {
        isMovementDriven = movementDriven;
    }
}
