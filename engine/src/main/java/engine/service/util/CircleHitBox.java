package engine.service.util;

public class CircleHitBox {

    private final double x;
    private final double y;
    private final double size;

    public CircleHitBox(double x, double y, double size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getSize() {
        return size;
    }
}
