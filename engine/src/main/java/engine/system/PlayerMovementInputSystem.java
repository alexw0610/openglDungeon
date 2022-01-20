package engine.system;

import com.jogamp.newt.event.MouseEvent;
import engine.Engine;
import engine.component.*;
import engine.entity.ComponentBuilder;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.handler.KeyHandler;
import engine.handler.MouseHandler;
import org.joml.Vector2d;

import static engine.EngineConstants.INERTIA;

public class PlayerMovementInputSystem {

    public static void processEntity(Entity entity) {
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
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
        while (!MouseHandler.getInstance().getMouseClickedEventsQueue().isEmpty()) {
            MouseEvent event = MouseHandler.getInstance().getMouseClickedEventsQueue().poll();
            if (event != null && event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() == 1) {
                Vector2d mousePositionWorld = MouseHandler.getInstance().getMousePositionWorldSpace();
                AttackComponent attack = (AttackComponent) ComponentBuilder.fromTemplate("slashAttack");
                attack.setTargetComponentConstraint(MobTag.class);
                EntityBuilder.builder()
                        .withComponent(attack)
                        .at(transformationComponent.getPosition().x(), transformationComponent.getPosition().y())
                        .buildAndInstantiate();
            }
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(PlayerTag.class)
                && entity.hasComponentOfType(PhysicsComponent.class)
                && entity.hasComponentOfType(StatComponent.class);
    }
}
