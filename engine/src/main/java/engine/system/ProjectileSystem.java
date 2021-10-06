package engine.system;

import engine.component.PhysicsComponent;
import engine.component.ProjectileComponent;
import engine.entity.Entity;
import org.joml.Vector2d;

public class ProjectileSystem {

    public static void processEntity(Entity entity) {
        ProjectileComponent projectileComponent = entity.getComponentOfType(ProjectileComponent.class);
        PhysicsComponent physicsComponent = entity.getComponentOfType(PhysicsComponent.class);
        Vector2d trajectory = new Vector2d();
        projectileComponent.getDirection().mul(projectileComponent.getVelocity(), trajectory);
        physicsComponent.setMomentumX(trajectory.x());
        physicsComponent.setMomentumY(trajectory.y());
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(ProjectileComponent.class)
                && entity.hasComponentOfType(PhysicsComponent.class);
    }
}
