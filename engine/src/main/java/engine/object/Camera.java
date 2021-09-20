package engine.object;

import engine.EngineConstants;
import org.joml.Vector2d;
import org.joml.Vector3d;

public class Camera {

    public static final Camera CAMERA = new Camera();

    private static final double MAX_ZOOM_DISTANCE = 0.1f;
    private static final double LERP_SPEED = 250;
    private static final double CAMERA_MOVE_SPEED = 100;

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

    public void lerpToLookAtTarget(double frameDelta) {
        if (this.lookAtTarget != null) {
            Vector2d temp = new Vector2d(this.positionX, this.positionY);
            temp.lerp(this.lookAtTarget.getPosition(), (1.0f * (frameDelta * EngineConstants.FRAME_DELTA_FACTOR)) * LERP_SPEED);
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
        this.positionZ = Math.max(this.positionZ - delta, MAX_ZOOM_DISTANCE);
    }

    public void zoomIn(double delta) {
        this.positionZ += delta;
    }
}
