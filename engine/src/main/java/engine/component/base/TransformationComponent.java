package engine.component.base;

import engine.component.Component;
import org.joml.Vector2d;

public class TransformationComponent implements Component {

    private static final long serialVersionUID = 7528520654317466342L;
    private double positionX;
    private double positionY;
    private double lastPositionX;
    private double lastPositionY;

    public TransformationComponent() {
        super();
        this.positionX = 0;
        this.positionY = 0;
        this.lastPositionX = 0;
        this.lastPositionY = 0;
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
        this.lastPositionX = this.positionX;
        this.positionX = positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public void setPositionY(double positionY) {
        this.lastPositionY = this.positionY;
        this.positionY = positionY;
    }

    public double getLastPositionX() {
        return lastPositionX;
    }

    public double getLastPositionY() {
        return lastPositionY;
    }

    public Vector2d getLastPosition() {
        return new Vector2d(this.lastPositionX, this.lastPositionY);
    }
}
