package engine.component;

public class PhysicsComponent implements Component {

    private double momentumX;
    private double momentumY;

    public double getMomentumX() {
        return momentumX;
    }

    public void setMomentumX(double momentumX) {
        this.momentumX = momentumX;
    }

    public double getMomentumY() {
        return momentumY;
    }

    public void setMomentumY(double momentumY) {
        this.momentumY = momentumY;
    }
}
