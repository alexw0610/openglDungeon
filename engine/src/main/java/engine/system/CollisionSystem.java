package engine.system;

import engine.Engine;
import engine.EngineConstants;
import engine.component.CollisionComponent;
import engine.component.Component;
import engine.component.SurfaceTag;
import engine.component.TransformationComponent;
import engine.component.internal.CreatedByComponent;
import engine.entity.ComponentBuilder;
import engine.entity.Entity;
import engine.handler.EntityHandler;
import engine.service.util.CollisionUtil;
import org.apache.commons.lang3.StringUtils;
import org.joml.Vector2d;

import java.util.List;
import java.util.stream.Collectors;

public class CollisionSystem {
    public static void processEntity(Entity entity) {
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        CollisionComponent collisionComponent = entity.getComponentOfType(CollisionComponent.class);
        CreatedByComponent createdByComponent = entity.getComponentOfType(CreatedByComponent.class);
        if (collisionComponent.getSelfApplyComponents() != null || collisionComponent.getOtherApplyComponents() != null) {
            List<Entity> obstacles = EntityHandler.getInstance().getAllEntitiesWithComponents(TransformationComponent.class, CollisionComponent.class);
            obstacles = obstacles.parallelStream().unordered().filter(e -> !e.hasComponentOfType(SurfaceTag.class)).collect(Collectors.toList());
            obstacles.remove(entity);
            if (entity.hasComponentOfType(CreatedByComponent.class)) {
                obstacles.remove(createdByComponent.getCreatorEntity());
            }
            List<Entity> collisions = CollisionUtil.getCollisions(transformationComponent, collisionComponent, obstacles);
            if (!collisions.isEmpty()) {
                if (StringUtils.isNotBlank(collisionComponent.getSelfApplyComponents())) {
                    Component component = ComponentBuilder.fromTemplate(collisionComponent.getSelfApplyComponents());
                    if (EngineConstants.INSTANCE.isServerMode() || !component.isServerSide()) {
                        entity.addComponent(component);
                    }
                }
            }
            for (Entity collision : collisions) {
                if (StringUtils.isNotBlank(collisionComponent.getOtherApplyComponents())) {
                    Component component = ComponentBuilder.fromTemplate(collisionComponent.getOtherApplyComponents());
                    if (EngineConstants.INSTANCE.isServerMode() || !component.isServerSide()) {
                        collision.addComponent(component);
                    }
                }
                Vector2d collisionVector = new Vector2d();
                TransformationComponent collisionTransformation = collision.getComponentOfType(TransformationComponent.class);
                collisionTransformation.getLastPosition().sub(collisionTransformation.getPosition(), collisionVector);
                collisionComponent.collisions.offer(collisionVector.normalize());
            }
            Engine.collisionHandled += 1;
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(CollisionComponent.class);
    }
}
