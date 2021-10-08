package engine.component;

public class AnimationComponent implements Component {

    private double animationSpeed;
    private double animationRow;
    private double animationFrame;
    private boolean animationContinuous;
    private double animationLength;

    private boolean animationFinished;
    private double animationUpdatedLast;

    public AnimationComponent(double animationSpeed) {
        this.animationSpeed = animationSpeed;
        this.animationContinuous = true;
        this.animationFinished = false;
        this.animationRow = 1;
        this.animationFrame = 1;
        this.animationLength = 0;
    }

    public AnimationComponent(double animationSpeed, boolean animationContinuous, double animationLength) {
        this.animationSpeed = animationSpeed;
        this.animationContinuous = animationContinuous;
        this.animationFinished = false;
        this.animationRow = 1;
        this.animationFrame = 1;
        this.animationLength = animationLength;
    }

    public double getAnimationSpeed() {
        return animationSpeed;
    }

    public void setAnimationSpeed(double animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    public double getAnimationRow() {
        return animationRow;
    }

    public void setAnimationRow(double animationRow) {
        this.animationRow = animationRow;
    }

    public double getAnimationFrame() {
        return animationFrame;
    }

    public void setAnimationFrame(double animationFrame) {
        this.animationFrame = animationFrame;
    }

    public boolean isAnimationContinuous() {
        return animationContinuous;
    }

    public void setAnimationContinuous(boolean animationContinuous) {
        this.animationContinuous = animationContinuous;
    }

    public boolean isAnimationFinished() {
        return animationFinished;
    }

    public void setAnimationFinished(boolean animationFinished) {
        this.animationFinished = animationFinished;
    }

    public double getAnimationUpdatedLast() {
        return animationUpdatedLast;
    }

    public void setAnimationUpdatedLast(double animationUpdatedLast) {
        this.animationUpdatedLast = animationUpdatedLast;
    }

    public double getAnimationLength() {
        return animationLength;
    }

    public void setAnimationLength(double animationLength) {
        this.animationLength = animationLength;
    }
}
