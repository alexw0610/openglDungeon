package engine.system.util;

import engine.component.CollisionComponent;
import engine.component.TransformationComponent;
import engine.entity.Entity;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class CollisionUtil {
    public static List<Entity> getCollisions(TransformationComponent transformationComponent, CollisionComponent collisionComponent, List<Entity> obstacles) {
        double x = transformationComponent.getPositionX();
        double y = transformationComponent.getPositionY();
        List<Entity> collisions = new ArrayList<>();
        for (Entity entity : obstacles) {
            TransformationComponent transformationComponentTarget = entity.getComponentOfType(TransformationComponent.class);
            CollisionComponent collisionComponentTarget = entity.getComponentOfType(CollisionComponent.class);
            double xTarget = transformationComponentTarget.getPositionX();
            double yTarget = transformationComponentTarget.getPositionY();
            if (engine.service.util.CollisionUtil.checkCollision(new Vector2d(x, y), collisionComponent.getHitBox(), new Vector2d(xTarget, yTarget), collisionComponentTarget.getHitBox())) {
                collisions.add(entity);
            }
        }
        return collisions;
    }

    public static List<Entity> getInside(TransformationComponent transformationComponent, List<Entity> obstacles) {
        double x = transformationComponent.getPositionX();
        double y = transformationComponent.getPositionY();
        List<Entity> collisions = new ArrayList<>();
        for (Entity entity : obstacles) {
            TransformationComponent transformationComponentTarget = entity.getComponentOfType(TransformationComponent.class);
            CollisionComponent collisionComponentTarget = entity.getComponentOfType(CollisionComponent.class);
            double xTarget = transformationComponentTarget.getPositionX();
            double yTarget = transformationComponentTarget.getPositionY();
            if (engine.service.util.CollisionUtil.checkInside(new Vector2d(x, y), collisionComponentTarget.getHitBox(), new Vector2d(xTarget, yTarget))) {
                collisions.add(entity);
            }
        }
        return collisions;
    }
}
