package engine.system;

import engine.Engine;
import engine.component.DashComponent;
import engine.component.StatComponent;
import engine.component.base.PhysicsComponent;
import engine.component.base.TransformationComponent;
import engine.component.tag.PlayerTag;
import engine.entity.Entity;
import engine.handler.KeyHandler;
import engine.handler.MouseHandler;
import org.joml.Vector2d;

import static engine.EngineConstants.STEP_TIME_FACTOR;

public class PlayerMovementInputSystem {

    public static void processEntity(Entity entity) {
        PhysicsComponent physicsComponent = entity.getComponentOfType(PhysicsComponent.class);
        StatComponent statComponent = entity.getComponentOfType(StatComponent.class);
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        Vector2d mousePosWS = MouseHandler.getInstance().getMousePositionWorldSpace();
        Vector2d direction = mousePosWS.sub(transformationComponent.getPosition()).normalize();
        if (!entity.hasComponentOfType(DashComponent.class)) {
            if (KeyHandler.getInstance().isKeyForActionPressed("movePlayerUp")) {
                double y = physicsComponent.getMomentumY();
                y += (statComponent.getMovementSpeed()) * (Engine.stepTimeDelta * STEP_TIME_FACTOR);
                physicsComponent.setMomentumY(y);
            }
            if (KeyHandler.getInstance().isKeyForActionPressed("movePlayerDown")) {
                double y = physicsComponent.getMomentumY();
                y -= (statComponent.getMovementSpeed()) * (Engine.stepTimeDelta * STEP_TIME_FACTOR);
                physicsComponent.setMomentumY(y);
            }
            if (KeyHandler.getInstance().isKeyForActionPressed("movePlayerRight")) {
                double x = physicsComponent.getMomentumX();
                x += (statComponent.getMovementSpeed()) * (Engine.stepTimeDelta * STEP_TIME_FACTOR);
                physicsComponent.setMomentumX(x);
            }
            if (KeyHandler.getInstance().isKeyForActionPressed("movePlayerLeft")) {
                double x = physicsComponent.getMomentumX();
                x -= (statComponent.getMovementSpeed()) * (Engine.stepTimeDelta * STEP_TIME_FACTOR);
                physicsComponent.setMomentumX(x);
            }
        }
        if (KeyHandler.getInstance().isKeyForActionPressed("dash")) {
            if (!entity.hasComponentOfType(DashComponent.class)
                    && ((System.nanoTime() - statComponent.getLastDashed()) > (statComponent.getDashCooldownSpeed() * 1000000000))) {
                statComponent.setLastDashed(System.nanoTime());
                DashComponent dashComponent = new DashComponent(direction, 10.0);
                dashComponent.setDashStunDuration(statComponent.getDashStunDuration());
                entity.addComponent(dashComponent);
            }
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(PlayerTag.class) && entity.hasComponentOfType(PhysicsComponent.class);
    }
}
