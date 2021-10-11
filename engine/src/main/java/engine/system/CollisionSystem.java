package engine.system;

import engine.component.CollisionComponent;
import engine.component.SurfaceTag;
import engine.component.TransformationComponent;
import engine.component.internal.CreatedByComponent;
import engine.entity.ComponentBuilder;
import engine.entity.Entity;
import engine.handler.EntityHandler;
import engine.service.util.CollisionUtil;
import org.apache.commons.lang3.StringUtils;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CollisionSystem {
    public static void processEntity(Entity entity) {
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        CollisionComponent collisionComponent = entity.getComponentOfType(CollisionComponent.class);
        CreatedByComponent createdByComponent = entity.getComponentOfType(CreatedByComponent.class);
        if (collisionComponent.getSelfApplyComponents() != null || collisionComponent.getOtherApplyComponents() != null) {
            List<Entity> obstacles = EntityHandler.getInstance().getAllEntitiesWithComponents(TransformationComponent.class, CollisionComponent.class);
            obstacles = obstacles.stream().filter(e -> !e.hasComponentOfType(SurfaceTag.class)).collect(Collectors.toList());
            obstacles.remove(entity);
            if (entity.hasComponentOfType(CreatedByComponent.class)) {
                obstacles.remove(createdByComponent.getCreatorEntity());
            }
            List<Entity> collisions = getCollisions(transformationComponent, collisionComponent, obstacles);
            if (!collisions.isEmpty()) {
                if (StringUtils.isNotBlank(collisionComponent.getSelfApplyComponents())) {
                    entity.addComponent(ComponentBuilder.fromTemplate(collisionComponent.getSelfApplyComponents()));
                }
            }
            for (Entity collision : collisions) {
                if (StringUtils.isNotBlank(collisionComponent.getOtherApplyComponents())) {
                    collision.addComponent(ComponentBuilder.fromTemplate(collisionComponent.getOtherApplyComponents()));
                }
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
