package engine.component;

public class CameraComponent implements Component {
    static final double cameraMoveSpeed = 0.0005;

    public double getCameraMoveSpeed() {
        return cameraMoveSpeed;
    }
}