package engine.system;

import engine.component.ProjectileComponent;
import engine.component.TransformationComponent;
import engine.entity.Entity;
import org.joml.Vector2d;

public class ProjectileSystem {
    public static void processEntity(Entity entity) {
        ProjectileComponent projectileComponent = entity.getComponentOfType(ProjectileComponent.class);
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        Vector2d trajectory = new Vector2d();
        projectileComponent.getDirection().normalize().mul(projectileComponent.getVelocity(), trajectory);
        transformationComponent.setPositionX(transformationComponent.getPositionX() + trajectory.x());
        transformationComponent.setPositionY(transformationComponent.getPositionY() + trajectory.y());
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(ProjectileComponent.class);
    }
}
