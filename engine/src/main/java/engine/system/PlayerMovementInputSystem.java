package engine.system;

import engine.Engine;
import engine.component.PhysicsComponent;
import engine.component.PlayerTag;
import engine.component.StatComponent;
import engine.entity.Entity;
import engine.handler.KeyHandler;

import static engine.EngineConstants.INERTIA;

public class PlayerMovementInputSystem {

    public static void processEntity(Entity entity) {
        PhysicsComponent physicsComponent = entity.getComponentOfType(PhysicsComponent.class);
        StatComponent statComponent = entity.getComponentOfType(StatComponent.class);
        if (KeyHandler.getInstance().isKeyForActionPressed("movePlayerUp")) {
            double y = physicsComponent.getMomentumY() + (Engine.stepTimeDelta * INERTIA * statComponent.getMovementSpeed());
            physicsComponent.setMomentumY(y);
        }
        if (KeyHandler.getInstance().isKeyForActionPressed("movePlayerDown")) {
            double y = physicsComponent.getMomentumY() - (Engine.stepTimeDelta * INERTIA * statComponent.getMovementSpeed());
            physicsComponent.setMomentumY(y);
        }
        if (KeyHandler.getInstance().isKeyForActionPressed("movePlayerRight")) {
            double x = physicsComponent.getMomentumX() + (Engine.stepTimeDelta * INERTIA * statComponent.getMovementSpeed());
            physicsComponent.setMomentumX(x);
        }
        if (KeyHandler.getInstance().isKeyForActionPressed("movePlayerLeft")) {
            double x = physicsComponent.getMomentumX() - (Engine.stepTimeDelta * INERTIA * statComponent.getMovementSpeed());
            physicsComponent.setMomentumX(x);
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(PlayerTag.class)
                && entity.hasComponentOfType(PhysicsComponent.class)
                && entity.hasComponentOfType(StatComponent.class);
    }
}
