package engine.component;

public class PhysicsComponent extends Component {

    private double momentumX;
    private double momentumY;

    public PhysicsComponent() {
    }

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
