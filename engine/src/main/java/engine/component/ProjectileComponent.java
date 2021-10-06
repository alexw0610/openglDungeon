package engine.component;

import org.joml.Vector2d;

public class ProjectileComponent implements Component {
    private Vector2d direction;
    private double velocity;

    public ProjectileComponent(Vector2d direction, double velocity) {
        this.direction = direction;
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
