package engine.system;

import engine.Engine;
import engine.component.PhysicsComponent;
import engine.component.PlayerComponent;
import engine.entity.Entity;
import engine.handler.KeyHandler;

import java.util.List;

import static engine.EngineConstants.DECAY;
import static engine.EngineConstants.INERTIA;

public class PlayerMovementInputSystem implements System {

    @Override
    public void processEntities(List<Entity> entities) {
        for (Entity entity : entities) {
            PhysicsComponent physicsComponent = entity.getComponentOfType(PhysicsComponent.class);
            PlayerComponent playerComponent = entity.getComponentOfType(PlayerComponent.class);
            decayMomentum(physicsComponent);
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
    }

    private void decayMomentum(PhysicsComponent component) {
        component.setMomentumX(decay(component.getMomentumX()));
        component.setMomentumY(decay(component.getMomentumY()));
    }

    private double decay(double momentum) {
        if (Math.abs(momentum) < 0.001) {
            return 0;
        } else {
            return momentum * (DECAY / Engine.stepTimeDelta);
        }
    }

    @Override
    public boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(PlayerComponent.class)
                && entity.hasComponentOfType(PhysicsComponent.class);
    }
}
