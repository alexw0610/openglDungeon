package engine.component;

public class TransformationComponent extends Component {

    private double positionX;
    private double positionY;
    private double scale;

    public TransformationComponent() {
        super();
        this.positionX = 0;
        this.positionY = 0;
        this.scale = 1;
    }

    public double getPositionX() {
        return positionX;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }
}
