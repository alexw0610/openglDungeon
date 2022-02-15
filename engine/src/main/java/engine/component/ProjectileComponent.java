package engine.component;

import org.joml.Vector2d;

public class ProjectileComponent implements Component {
    private static final long serialVersionUID = -1268060665062914044L;
    private Vector2d direction;
    private double velocity;

    public ProjectileComponent(Double directionX, Double directionY, Double velocity) {
        this.direction = new Vector2d(directionX, directionY);
        this.velocity = velocity;
    }

    public Vector2d getDirection() {
        return direction;
    }

    public void setDirection(Vector2d direction) {
        this.direction = direction;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }
}
