package engine.service.util;

import engine.object.HitBox;
import org.joml.Intersectiond;
import org.joml.Vector2d;

public class CollisionUtil {

    public static boolean checkInside(Vector2d point, HitBox hitbox, Vector2d hitboxPosition) {
        switch (hitbox.getHitBoxType()) {
            case AABB:
                return Intersectiond.testPointAar(point.x(), point.y(),
                        hitboxPosition.x() - (hitbox.getSize() / 2),
                        hitboxPosition.y() - (hitbox.getSize() / 2),
                        hitboxPosition.x() + (hitbox.getSize() / 2),
                        hitboxPosition.y() + (hitbox.getSize() / 2)
                );
            case CIRCLE:
                return Intersectiond.testPointCircle(point.x(), point.y(), hitboxPosition.x(), hitboxPosition.y(), Math.pow(hitbox.getSize(), 2));
            default:
                return false;
        }
    }

    public static boolean checkCollision(Vector2d positionA, HitBox hitBoxA, Vector2d positionB, HitBox hitBoxB) {
        switch (hitBoxA.getHitBoxType()) {
            case AABB:
                Vector2d minA = new Vector2d(positionA.x() - hitBoxA.getSize(), positionA.y() - hitBoxA.getSize());
                Vector2d maxA = new Vector2d(positionA.x() + hitBoxA.getSize(), positionA.y() + hitBoxA.getSize());
                switch (hitBoxB.getHitBoxType()) {
                    case AABB:
                        Vector2d minB = new Vector2d(positionB.x() - hitBoxB.getSize(), positionB.y() - hitBoxB.getSize());
                        Vector2d maxB = new Vector2d(positionB.x() + hitBoxB.getSize(), positionB.y() + hitBoxB.getSize());
                        return Intersectiond.testAarAar(minA, maxA, minB, maxB);
                    case CIRCLE:
                        return Intersectiond.testAarCircle(minA, maxA, positionB, Math.pow(hitBoxB.getSize(), 2));
                    default:
                        return false;
                }
            case CIRCLE:
                switch (hitBoxB.getHitBoxType()) {
                    case AABB:
                        Vector2d minB = new Vector2d(positionB.x() - hitBoxB.getSize(), positionB.y() - hitBoxB.getSize());
                        Vector2d maxB = new Vector2d(positionB.x() + hitBoxB.getSize(), positionB.y() + hitBoxB.getSize());
                        return Intersectiond.testAarCircle(minB, maxB, positionA, Math.pow(hitBoxA.getSize(), 2));
                    case CIRCLE:
                        return Intersectiond.testCircleCircle(positionA.x(), positionA.y(), hitBoxA.getSize(),
                                positionB.x(), positionB.y(), hitBoxB.getSize());
                    default:
                        return false;
                }
            default:
                return false;
        }
    }
}
