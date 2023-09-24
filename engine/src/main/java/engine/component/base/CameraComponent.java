package engine.component.base;

import engine.component.Component;

public class CameraComponent implements Component {
    private static final double cameraMoveSpeed = 0.025;
    private static final double cameraZoomSpeed = 0.025;
    private static final long serialVersionUID = -5606642123442386205L;
    private double cameraZoom = 0.10;

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

    @Override
    public void onRemove() {

    }
}