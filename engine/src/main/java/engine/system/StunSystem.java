package engine.system;

import engine.component.StunComponent;
import engine.entity.Entity;

public class StunSystem {
    public static void processEntity(Entity entity) {
        StunComponent stunComponent = entity.getComponentOfType(StunComponent.class);
        if ((System.nanoTime() - stunComponent.getStunStartTime()) > (stunComponent.getStunDurationSeconds() * 1000000000)) {
            entity.removeComponent(StunComponent.class);
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(StunComponent.class);
    }
}
