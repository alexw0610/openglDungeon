package engine.system;

import com.jogamp.newt.event.MouseEvent;
import engine.Engine;
import engine.component.PhysicsComponent;
import engine.component.PlayerComponent;
import engine.component.TransformationComponent;
import engine.entity.Entity;
import engine.handler.KeyHandler;
import engine.handler.MouseHandler;
import org.joml.Vector2d;

import java.text.NumberFormat;

import static engine.EngineConstants.INERTIA;

public class PlayerMovementInputSystem {

    public static void processEntity(Entity entity) {
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
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
        while (!MouseHandler.getInstance().getMouseClickedEventsQueue().isEmpty()) {
            MouseEvent event = MouseHandler.getInstance().getMouseClickedEventsQueue().poll();
            if (event != null && event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() == 1) {
                Vector2d mousePositionWorld = MouseHandler.getInstance().getMousePositionWorldSpace();
                System.out.println(mousePositionWorld.toString(NumberFormat.getIntegerInstance()));
            }
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(PlayerComponent.class)
                && entity.hasComponentOfType(PhysicsComponent.class);
    }
}
