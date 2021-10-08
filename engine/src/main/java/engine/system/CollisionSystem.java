package engine.system;

import engine.component.CollisionComponent;
import engine.component.CreatedByComponent;
import engine.component.TransformationComponent;
import engine.entity.Entity;
import engine.handler.EntityHandler;
import engine.service.util.CollisionUtil;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class CollisionSystem {
    public static void processEntity(Entity entity) {
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        CollisionComponent collisionComponent = entity.getComponentOfType(CollisionComponent.class);
        CreatedByComponent createdByComponent = entity.getComponentOfType(CreatedByComponent.class);
        if (collisionComponent.getOnCollisionFunction() != null) {
            List<Entity> obstacles = EntityHandler.getInstance().getAllEntitiesWithComponents(TransformationComponent.class, CollisionComponent.class);
            obstacles.remove(entity);
            if (entity.hasComponentOfType(CreatedByComponent.class)) {
                obstacles.remove(createdByComponent.getCreatorEntity());
            }
            List<Entity> collisions = getCollisions(transformationComponent, collisionComponent, obstacles);
            for (Entity collision : collisions) {
                collisionComponent.getOnCollisionFunction().run(entity, collision);
            }
        }
    }

    private static List<Entity> getCollisions(TransformationComponent transformationComponent, CollisionComponent collisionComponent, List<Entity> obstacles) {
        double x = transformationComponent.getPositionX();
        double y = transformationComponent.getPositionY();
        List<Entity> collisions = new ArrayList<>();
        for (Entity entity : obstacles) {
            TransformationComponent transformationComponentTarget = entity.getComponentOfType(TransformationComponent.class);
            CollisionComponent collisionComponentTarget = entity.getComponentOfType(CollisionComponent.class);
            double xTarget = transformationComponentTarget.getPositionX();
            double yTarget = transformationComponentTarget.getPositionY();
            if (CollisionUtil.checkCollision(new Vector2d(x, y), collisionComponent.getHitBox(), new Vector2d(xTarget, yTarget), collisionComponentTarget.getHitBox())) {
                collisions.add(entity);
            }
        }
        return collisions;
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(CollisionComponent.class);
    }
}
