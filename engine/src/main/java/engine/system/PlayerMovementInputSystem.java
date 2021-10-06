package engine.system;

import com.jogamp.newt.event.MouseEvent;
import engine.Engine;
import engine.component.*;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.PrimitiveMeshShape;
import engine.enums.ShaderType;
import engine.enums.TextureKey;
import engine.handler.EntityHandler;
import engine.handler.KeyHandler;
import engine.handler.MouseHandler;
import org.joml.Vector2d;
import org.joml.Vector3d;

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
                Vector2d projectileDirection = new Vector2d(mousePositionWorld).sub(transformationComponent.getPosition());
                Entity orb = EntityBuilder.builder()
                        .withComponent(new TransformationComponent(transformationComponent.getPositionX(), transformationComponent.getPositionY()))
                        .withComponent(new RenderComponent(PrimitiveMeshShape.QUAD, TextureKey.ORB_AQUA, ShaderType.DEFAULT, 1, 5))
                        .withComponent(new AnimationComponent(0.01))
                        .withComponent(new PhysicsComponent())
                        .withComponent(new ProjectileComponent(projectileDirection.normalize(), 0.02))
                        .withComponent(new LightSourceComponent(new Vector3d(Math.random(), Math.random(), Math.random()), 1, 0.01))
                        .build();
                orb.getComponentOfType(RenderComponent.class).setShadeless(true);
                orb.getComponentOfType(RenderComponent.class).setAlwaysVisible(true);
                EntityHandler.getInstance().addObject(orb);
            }

        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(PlayerComponent.class)
                && entity.hasComponentOfType(PhysicsComponent.class);
    }
}
