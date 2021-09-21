package engine.service.util;

public class AxisAlignedBoundingBox {

    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;

    public AxisAlignedBoundingBox(double minX, double maxX, double minY, double maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
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

    @Override
    public String toString() {
        return "AxisAlignedBoundingBox{" +
                "minX=" + minX +
                ", maxX=" + maxX +
                ", minY=" + minY +
                ", maxY=" + maxY +
                '}';
    }
}
