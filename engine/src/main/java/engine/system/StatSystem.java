package engine.system;

import engine.component.*;
import engine.entity.Entity;

public class StatSystem {
    public static void processEntity(Entity entity) {
        StatComponent statComponent = entity.getComponentOfType(StatComponent.class);
        if (!statComponent.isDead()) {
            if (statComponent.getCurrentHealthpoints() <= 0) {
                System.out.println("Combat Log: " + entity.getEntityId() + " died!");
                entity.removeComponent(PhysicsComponent.class);
                entity.removeComponent(AIComponent.class);
                entity.removeComponent(AnimationComponent.class);
                if (entity.hasComponentOfType(CollisionComponent.class)) {
                    entity.getComponentOfType(CollisionComponent.class).setObstructsMovement(false);
                }
                statComponent.setDead(true);
            }
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(StatComponent.class);
    }
}
