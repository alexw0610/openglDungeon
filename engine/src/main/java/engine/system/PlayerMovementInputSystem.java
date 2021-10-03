package engine.system;

import engine.Engine;
import engine.component.PhysicsComponent;
import engine.component.PlayerComponent;
import engine.entity.Entity;
import engine.handler.KeyHandler;

import static engine.EngineConstants.INERTIA;

public class PlayerMovementInputSystem {

    public static void processEntity(Entity entity) {
        PhysicsComponent physicsComponent = entity.getComponentOfType(PhysicsComponent.class);
        PlayerComponent playerComponent = entity.getComponentOfType(PlayerComponent.class);
        if (KeyHandler.getInstance().isKeyForActionPressed("movePlayerUp")) {
            double y = physicsComponent.getMomentumY() + (Engine.stepTimeDelta * INERTIA * playerComponent.getMovementSpeed());
            physicsComponent.setMomentumY(y);
        }
        if (KeyHandler.getInstance().isKeyForActionPressed("movePlayerDown")) {
            double y = physicsComponent.getMomentumY() - (Engine.stepTimeDelta * INERTIA * playerComponent.getMovementSpeed());
            physicsComponent.setMomentumY(y);
        }
        if (KeyHandler.getInstance().isKeyForActionPressed("movePlayerRight")) {
            double x = physicsComponent.getMomentumX() + (Engine.stepTimeDelta * INERTIA * playerComponent.getMovementSpeed());
            physicsComponent.setMomentumX(x);
        }
        if (KeyHandler.getInstance().isKeyForActionPressed("movePlayerLeft")) {
            double x = physicsComponent.getMomentumX() - (Engine.stepTimeDelta * INERTIA * playerComponent.getMovementSpeed());
            physicsComponent.setMomentumX(x);
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(PlayerComponent.class)
                && entity.hasComponentOfType(PhysicsComponent.class);
    }
}
