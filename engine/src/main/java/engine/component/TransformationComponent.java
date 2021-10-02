package engine.component;

public class TransformationComponent implements Component {

    private double positionX;
    private double positionY;

    public TransformationComponent() {
        super();
        this.positionX = 0;
        this.positionY = 0;
    }

    public TransformationComponent(double x, double y) {
        super();
        this.positionX = x;
        this.positionY = y;
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

}
