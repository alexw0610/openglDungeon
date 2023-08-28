package engine.system;

import engine.Engine;
import engine.EngineConstants;
import engine.component.ProjectileComponent;
import engine.component.base.TransformationComponent;
import engine.entity.Entity;

public class ProjectileSystem {
    public static void processEntity(Entity entity) {
        ProjectileComponent projectileComponent = entity.getComponentOfType(ProjectileComponent.class);
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        transformationComponent.setPositionX(transformationComponent.getPositionX()
                + (Engine.stepTimeDelta * EngineConstants.STEP_TIME_FACTOR)
                * (projectileComponent.getDirection().x() * projectileComponent.getSpeed()));
        transformationComponent.setPositionY(transformationComponent.getPositionY()
                + (Engine.stepTimeDelta * EngineConstants.STEP_TIME_FACTOR)
                * (projectileComponent.getDirection().y() * projectileComponent.getSpeed()));
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(ProjectileComponent.class);
    }
}
