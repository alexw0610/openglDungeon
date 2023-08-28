package engine.system.base;

import engine.component.*;
import engine.component.base.AudioComponent;
import engine.component.base.CollisionComponent;
import engine.component.base.TransformationComponent;
import engine.component.internal.CreatedByComponent;
import engine.component.tag.ItemTag;
import engine.component.tag.MobTag;
import engine.component.tag.RangedMobTag;
import engine.component.tag.SurfaceTag;
import engine.entity.ComponentBuilder;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.handler.EntityHandler;
import engine.service.util.CollisionUtil;
import org.apache.commons.lang3.StringUtils;
import org.joml.Vector2d;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CollisionSystem {
    public static void processEntity(Entity entity) {
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        CollisionComponent collisionComponent = entity.getComponentOfType(CollisionComponent.class);
        CreatedByComponent createdByComponent = entity.getComponentOfType(CreatedByComponent.class);
        ProjectileComponent projectileComponent = entity.getComponentOfType(ProjectileComponent.class);
        DashComponent dashComponent = entity.getComponentOfType(DashComponent.class);
        if (needsCollisionCheck(collisionComponent, projectileComponent, dashComponent)) {
            List<Entity> collisions = getCollisions(entity, transformationComponent, collisionComponent, createdByComponent);
            if (!collisions.isEmpty()) {
                handleDashCollision(entity);
                handleSelfApplyComponent(entity, collisionComponent);
                for (Entity collision : collisions) {
                    handleOtherApplyComponents(collisionComponent, collision);
                }
                if (projectileComponent != null
                        && !projectileComponent.getOnCollisionAttack().isEmpty()) {
                    handleProjectileCollision(entity, transformationComponent, projectileComponent, collisions);
                }
            }
        }
    }

    private static void handleDashCollision(Entity entity) {
        if (entity.hasComponentOfType(DashComponent.class)) {
            entity.getComponentOfType(DashComponent.class).setHasCollided();
        }
    }

    private static void handleProjectileCollision(Entity entity, TransformationComponent transformationComponent, ProjectileComponent projectileComponent, List<Entity> collisions) {
        Optional<Entity> firstCollision;
        Entity createdBy = entity.getComponentOfType(CreatedByComponent.class)
                .getCreatorEntity();
        if (createdBy.hasComponentOfType(MobTag.class)
                || createdBy.hasComponentOfType(RangedMobTag.class)
                || createdBy.hasComponentOfType(BossComponent.class)) {
            firstCollision = collisions.stream()
                    .filter(collision -> !collision.hasComponentOfType(MobTag.class)
                            && !collision.hasComponentOfType(RangedMobTag.class)
                            && !collision.hasComponentOfType(DashComponent.class))
                    .filter(collision -> !collision.hasComponentOfType(ProjectileComponent.class))
                    .findFirst();
        } else {
            firstCollision = collisions.stream()
                    .filter(collision -> !collision.hasComponentOfType(ProjectileComponent.class))
                    .findFirst();
        }
        if (firstCollision.isPresent()) {
            AttackComponent attack = (AttackComponent) ComponentBuilder.fromTemplate(projectileComponent.getOnCollisionAttack());
            if (attack.isSingleTarget()) {
                attack.setTargetEntity(firstCollision.get().getEntityId());
            }
            EntityBuilder.builder()
                    .withComponent(attack)
                    .at(transformationComponent.getPosition().x(), transformationComponent.getPosition().y())
                    .buildAndInstantiate();
            AudioComponent audio = new AudioComponent();
            audio.setPlayOnce(true);
            audio.setAudioKey("impact");
            EntityBuilder.builder()
                    .withComponent(audio)
                    .at(transformationComponent.getPosition().x(), transformationComponent.getPosition().y())
                    .buildAndInstantiate();
            EntityHandler.getInstance().removeObject(entity.getEntityId());
        }
    }

    private static void handleOtherApplyComponents(CollisionComponent collisionComponent, Entity collision) {
        if (StringUtils.isNotBlank(collisionComponent.getOtherApplyComponents())) {
            Component component = ComponentBuilder.fromTemplate(collisionComponent.getOtherApplyComponents());
            collision.addComponent(component);
        }
        Vector2d collisionVector = new Vector2d();
        TransformationComponent collisionTransformation = collision.getComponentOfType(TransformationComponent.class);
        collisionTransformation.getLastPosition().sub(collisionTransformation.getPosition(), collisionVector);
        collisionComponent.collisions.offer(collisionVector.normalize());
    }

    private static void handleSelfApplyComponent(Entity entity, CollisionComponent collisionComponent) {
        if (StringUtils.isNotBlank(collisionComponent.getSelfApplyComponents())) {
            Component component = ComponentBuilder.fromTemplate(collisionComponent.getSelfApplyComponents());
            entity.addComponent(component);
        }
    }

    private static boolean needsCollisionCheck(CollisionComponent collisionComponent, ProjectileComponent projectileComponent, DashComponent dashComponent) {
        return collisionComponent.getSelfApplyComponents() != null
                || collisionComponent.getOtherApplyComponents() != null
                || (projectileComponent != null && !projectileComponent.getOnCollisionAttack().isEmpty())
                || dashComponent != null;
    }

    private static List<Entity> getCollisions(Entity entity, TransformationComponent transformationComponent, CollisionComponent collisionComponent, CreatedByComponent createdByComponent) {
        List<Entity> obstacles = EntityHandler.getInstance()
                .getAllEntitiesWithComponents(TransformationComponent.class, CollisionComponent.class);
        obstacles = obstacles
                .parallelStream()
                .unordered()
                .filter(e -> !e.hasComponentOfType(ItemTag.class) && !e.hasComponentOfType(SurfaceTag.class) && distanceLessThan(e, entity, 4.0))
                .collect(Collectors.toList());
        obstacles.remove(entity);
        if (entity.hasComponentOfType(CreatedByComponent.class)) {
            obstacles.remove(createdByComponent.getCreatorEntity());
        }
        return CollisionUtil.getCollisions(transformationComponent, collisionComponent, obstacles);
    }

    private static boolean distanceLessThan(Entity entityA, Entity entityB, double maximumDistance) {
        return entityA.getComponentOfType(TransformationComponent.class).getPosition()
                .distance(entityB.getComponentOfType(TransformationComponent.class).getPosition()) < maximumDistance;
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(CollisionComponent.class);
    }
}
