package engine.component;

public class CameraComponent implements Component {
    private static final double cameraMoveSpeed = 0.0005;
    private static final double cameraZoomSpeed = 0.0000005;
    private static final long serialVersionUID = -5606642123442386205L;
    private double cameraZoom = 0.25;

    public double getCameraMoveSpeed() {
        return cameraMoveSpeed;
    }

    public double getCameraZoomSpeed() {
        return cameraZoomSpeed;
    }

    public double getCameraZoom() {
        return cameraZoom;
    }

    public void setCameraZoom(double cameraZoom) {
        this.cameraZoom = cameraZoom;
    }
}