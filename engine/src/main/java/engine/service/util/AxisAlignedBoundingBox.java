package engine.service.util;

import org.joml.Vector2d;

public class AxisAlignedBoundingBox {

    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;
    private final Vector2d center;
    private final double length;

    public AxisAlignedBoundingBox(double minX, double maxX, double minY, double maxY, Vector2d center, double length) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.center = center;
        this.length = length;
    }

    public double getMinX() {
        return minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxY() {
        return maxY;
    }

    public Vector2d getCenter() {
        return center;
    }

    public double getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "AxisAlignedBoundingBox{" +
                "minX=" + minX +
                ", maxX=" + maxX +
                ", minY=" + minY +
                ", maxY=" + maxY +
                ", center=" + center +
                ", length=" + length +
                '}';
    }
}
