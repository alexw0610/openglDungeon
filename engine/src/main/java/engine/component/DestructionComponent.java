package engine.component;

public class DestructionComponent implements Component {

    private static final long serialVersionUID = 7106383031829151046L;
    private double timer;

    public DestructionComponent(Double timer) {
        this.timer = timer;
    }

    public double getTimer() {
        return timer;
    }

    public void setTimer(double timer) {
        this.timer = timer;
    }
}
