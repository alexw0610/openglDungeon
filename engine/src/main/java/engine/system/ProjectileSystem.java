package engine.system;

import engine.Engine;
import engine.EngineConstants;
import engine.component.BossComponent;
import engine.component.ProjectileComponent;
import engine.component.StatComponent;
import engine.component.base.RenderComponent;
import engine.component.base.TransformationComponent;
import engine.component.tag.MobTag;
import engine.component.tag.RangedMobTag;
import engine.entity.Entity;
import engine.handler.EntityHandler;
import org.joml.Vector2d;

import java.util.Comparator;
import java.util.Optional;

public class ProjectileSystem {
    public static void processEntity(Entity entity) {
        ProjectileComponent projectileComponent = entity.getComponentOfType(ProjectileComponent.class);
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        handleTargetDirection(projectileComponent, transformationComponent);
        setSpriteRotation(entity, projectileComponent);
        updatePosition(projectileComponent, transformationComponent);
    }

    private static void handleTargetDirection(ProjectileComponent projectileComponent, TransformationComponent transformationComponent) {
        if (System.nanoTime() - projectileComponent.getLastTargetCheck() > EngineConstants.SECONDS_TO_NANOSECONDS_FACTOR) {
            updateTargetEntity(projectileComponent, transformationComponent);
            projectileComponent.setLastTargetCheck(System.nanoTime());
        }
        if (projectileComponent.getTargetEntity() != null
                && EntityHandler.getInstance().getEntityWithId(projectileComponent.getTargetEntity().getEntityId()) != null
                && !projectileComponent.getTargetEntity().getComponentOfType(StatComponent.class).isDead()) {
            setDirectionToTarget(projectileComponent, transformationComponent);
        }
    }

    private static void setDirectionToTarget(ProjectileComponent projectileComponent, TransformationComponent transformationComponent) {
        double targetDistance = getDistance(projectileComponent, transformationComponent);
        Vector2d direction = projectileComponent.getTargetEntity().getComponentOfType(TransformationComponent.class)
                .getPosition()
                .sub(transformationComponent.getPosition())
                .normalize();
        Vector2d targetVector = lerpVectors(projectileComponent, direction, 0.3f);
        projectileComponent.setDirection(targetVector.normalize());
        if (targetDistance < 0.1) {
            projectileComponent.setTargetEntity(null);
        }
    }

    private static void updateTargetEntity(ProjectileComponent projectileComponent, TransformationComponent transformationComponent) {
        Vector2d bulletPosition = transformationComponent.getPosition();
        Optional<Entity> targetEntity =
                EntityHandler.getInstance()
                        .getAllEntitiesWithAnyOfComponents(MobTag.class, RangedMobTag.class, BossComponent.class)
                        .stream()
                        .filter(mob -> !mob.getComponentOfType(StatComponent.class).isDead())
                        .filter(mob -> mob.getComponentOfType(TransformationComponent.class)
                                .getPosition()
                                .distance(bulletPosition) < 10.0)
                        .min(Comparator.comparing(mob -> mob.getComponentOfType(TransformationComponent.class)
                                .getPosition()
                                .distance(bulletPosition)));
        targetEntity.ifPresent(projectileComponent::setTargetEntity);
    }

    private static double getDistance(ProjectileComponent projectileComponent, TransformationComponent transformationComponent) {
        return projectileComponent
                .getTargetEntity()
                .getComponentOfType(TransformationComponent.class)
                .getPosition().distance(transformationComponent.getPosition());
    }

    private static void updatePosition(ProjectileComponent projectileComponent, TransformationComponent transformationComponent) {
        transformationComponent.setPositionX(transformationComponent.getPositionX()
                + (Engine.stepTimeDelta * EngineConstants.STEP_TIME_FACTOR)
                * (projectileComponent.getDirection().x() * projectileComponent.getSpeed()));
        transformationComponent.setPositionY(transformationComponent.getPositionY()
                + (Engine.stepTimeDelta * EngineConstants.STEP_TIME_FACTOR)
                * (projectileComponent.getDirection().y() * projectileComponent.getSpeed()));
    }

    private static void setSpriteRotation(Entity entity, ProjectileComponent projectileComponent) {
        entity.getComponentOfType(RenderComponent.class)
                .setTextureRotation(new Vector2d(1.0, 0.0)
                        .angle(projectileComponent.getDirection()) * 180 / 3.14159265359);
    }

    private static Vector2d lerpVectors(ProjectileComponent projectileComponent, Vector2d direction, float lerpValue) {
        return direction.mul(lerpValue).add(projectileComponent.getDirection().mul(1.0 - lerpValue));
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(ProjectileComponent.class);
    }
}
