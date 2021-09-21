package engine.object;

import org.joml.Vector2d;
import org.joml.Vector3d;

import static engine.EngineConstants.*;

public class Camera {

    public static final Camera CAMERA = new Camera();
    private GameObject lookAtTarget;

    private double positionX;
    private double positionY;
    private double positionZ;

    private Camera() {
        this.positionX = 0.0f;
        this.positionY = 0.0f;
        this.positionZ = 0.25f;
    }

    public void setLookAtTarget(GameObject target) {
        this.lookAtTarget = target;
    }

    public void lerpToLookAtTarget(double delta) {
        if (this.lookAtTarget != null) {
            Vector2d temp = new Vector2d(this.positionX, this.positionY);
            temp.lerp(this.lookAtTarget.getPosition(), ((delta) * LERP_SPEED));
            this.positionX = temp.x();
            this.positionY = temp.y();
        }
    }

    public Vector3d getPosition() {
        return new Vector3d(this.positionX, this.positionY, this.positionZ);
    }

    public void moveUp(double delta) {
        this.positionY += delta * CAMERA_MOVE_SPEED;
    }

    public void moveDown(double delta) {
        this.positionY -= delta * CAMERA_MOVE_SPEED;
    }

    public void moveLeft(double delta) {
        this.positionX -= delta * CAMERA_MOVE_SPEED;
    }

    public void moveRight(double delta) {
        this.positionX += delta * CAMERA_MOVE_SPEED;
    }

    public void zoomOut(double delta) {
        this.positionZ = Math.max(this.positionZ - delta * CAMERA_ZOOM_SPEED, MIN_ZOOM_DISTANCE);
    }

    public void zoomIn(double delta) {
        this.positionZ = Math.min(this.positionZ + delta * CAMERA_ZOOM_SPEED, MAX_ZOOM_DISTANCE);
    }
}
