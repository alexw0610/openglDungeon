package engine.component;

import org.joml.Vector2d;

public class TransformationComponent implements Component {

    private double positionX;
    private double positionY;

    public TransformationComponent() {
        super();
        this.positionX = 0;
        this.positionY = 0;
    }

    public TransformationComponent(Double x, Double y) {
        super();
        this.positionX = x;
        this.positionY = y;
    }

    public Vector2d getPosition() {
        return new Vector2d(positionX, positionY);
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
