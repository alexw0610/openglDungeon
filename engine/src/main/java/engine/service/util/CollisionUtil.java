package engine.service.util;

public class CollisionUtil {

    public static boolean checkCollisionAABBWithAABB(AxisAlignedBoundingBox aabbA, AxisAlignedBoundingBox aabbB) {
        return aabbA.getMinX() < aabbB.getMaxX() &&
                aabbA.getMaxX() > aabbB.getMinX() &&
                aabbA.getMaxY() > aabbB.getMinY() &&
                aabbA.getMinY() < aabbB.getMaxY();
    }

    public static boolean checkCollisionAABBWithCircle() {
        return true;
    }

    public static boolean checkCollisionCircleWithCircle() {
        return true;
    }

}
