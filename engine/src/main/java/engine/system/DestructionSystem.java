package engine.system;

import engine.Engine;
import engine.component.DestructionComponent;
import engine.entity.Entity;
import engine.handler.EntityHandler;

public class DestructionSystem {
    public static void processEntity(Entity entity) {
        DestructionComponent destructionComponent = entity.getComponentOfType(DestructionComponent.class);
        if (destructionComponent.getTimer() <= 0) {
            EntityHandler.getInstance().removeObject(entity.getEntityId());
        } else {
            destructionComponent.setTimer(destructionComponent.getTimer() - Engine.stepTimeDelta / 1000);
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(DestructionComponent.class);
    }
}
