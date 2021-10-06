package engine.component;

public class AnimationComponent implements Component {

    private double animationSpeed;
    private double animationRow;
    private double animationFrame;

    public AnimationComponent(double animationSpeed) {
        this.animationSpeed = animationSpeed;
        animationRow = 1;
        animationFrame = 1;
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
}
