package engine.service.util;

import engine.object.GameObject;
import org.joml.Vector2d;

public class CollisionUtil {

    public static final double ARBITRARY_SMALL_RADIUS = 0.000001;

    public static boolean checkCollisionAABBWithAABB(AxisAlignedBoundingBox aabbA, AxisAlignedBoundingBox aabbB) {
        return aabbA.getMinX() < aabbB.getMaxX() &&
                aabbA.getMaxX() > aabbB.getMinX() &&
                aabbA.getMaxY() > aabbB.getMinY() &&
                aabbA.getMinY() < aabbB.getMaxY();
    }

    public static boolean checkCollisionAABBWithCircle(AxisAlignedBoundingBox aabb, CircleHitBox circleHitBox) {
        double outerBoundingCircleRadius = (aabb.getLength() * 2) * Math.sqrt(2);
        double innerBoundingCircleRadius = (aabb.getLength());
        Vector2d circleCenter = new Vector2d(circleHitBox.getX(), circleHitBox.getY());
        Vector2d nearestCirclePoint = circleCenter.add((circleCenter.min(aabb.getCenter()).normalize()).mul(circleHitBox.getSize()));

        if (!checkCollisionCircleWithCircle(getCircleHitboxForPositionAndSize(aabb.getCenter().x(), aabb.getCenter().y(), outerBoundingCircleRadius), circleHitBox)) {
            return false;
        } else if (checkCollisionCircleWithCircle(getCircleHitboxForPositionAndSize(aabb.getCenter().x(), aabb.getCenter().y(), innerBoundingCircleRadius), circleHitBox)) {
            return true;
        }
        return checkCollisionAABBWithAABB(getAABBForPoint(nearestCirclePoint), aabb);

    }

    public static boolean checkCollisionCircleWithCircle(CircleHitBox circleHitBoxA, CircleHitBox circleHitBoxB) {
        return Math.sqrt(
                Math.pow((circleHitBoxA.getX() - circleHitBoxB.getX()), 2)
                        + Math.pow((circleHitBoxA.getY() - circleHitBoxB.getY()), 2))
                < (circleHitBoxA.getSize() + circleHitBoxB.getSize());
    }

    public static boolean checkInsideAABB(Vector2d nextPosition, AxisAlignedBoundingBox aabbForObject) {
        return checkCollisionAABBWithAABB(getAABBForPoint(nextPosition), aabbForObject);
    }

    public static boolean checkInsideCircle(Vector2d nextPosition, CircleHitBox circleHitboxForObject) {
        return checkCollisionCircleWithCircle(getCircleHitboxForPositionAndSize(nextPosition.x(), nextPosition.y(), ARBITRARY_SMALL_RADIUS), circleHitboxForObject);
    }

    public static AxisAlignedBoundingBox getAABBForObject(Vector2d position, GameObject object) {
        return new AxisAlignedBoundingBox(
                position.x() - object.getHitbox().getSize(),
                position.x() + object.getHitbox().getSize(),
                position.y() - object.getHitbox().getSize(),
                position.y() + object.getHitbox().getSize(),
                position,
                object.getHitbox().getSize());
    }

    private static AxisAlignedBoundingBox getAABBForPoint(Vector2d point) {
        return new AxisAlignedBoundingBox(
                point.x(),
                point.x(),
                point.y(),
                point.y(),
                point,
                0);
    }

    public static CircleHitBox getCircleHitboxForObject(Vector2d position, GameObject object) {
        return getCircleHitboxForPositionAndSize(position.x(), position.y(), object.getHitbox().getSize());
    }

    private static CircleHitBox getCircleHitboxForPositionAndSize(double x, double y, double size) {
        return new CircleHitBox(x, y, size);
    }
}
