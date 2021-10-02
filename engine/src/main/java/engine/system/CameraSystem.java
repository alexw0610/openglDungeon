package engine.system;

import engine.Engine;
import engine.component.CameraComponent;
import engine.component.CameraTargetComponent;
import engine.component.TransformationComponent;
import engine.entity.Entity;
import engine.handler.EntityHandler;
import engine.handler.KeyHandler;
import engine.service.RenderService;
import org.joml.Vector2d;

import static engine.EngineConstants.LERP_SPEED;

public class CameraSystem implements System {
    @Override
    public void processEntity(Entity entity) {
        Entity player = EntityHandler.getInstance().getEntityWithComponent(CameraTargetComponent.class);
        CameraComponent cameraComponent = entity.getComponentOfType(CameraComponent.class);
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        if (KeyHandler.getInstance().isKeyForActionPressed("moveCameraUp")) {
            double y = transformationComponent.getPositionY() + (Engine.stepTimeDelta * cameraComponent.getCameraMoveSpeed());
            transformationComponent.setPositionY(y);
        }
        if (KeyHandler.getInstance().isKeyForActionPressed("moveCameraDown")) {
            double y = transformationComponent.getPositionY() - (Engine.stepTimeDelta * cameraComponent.getCameraMoveSpeed());
            transformationComponent.setPositionY(y);
        }
        if (KeyHandler.getInstance().isKeyForActionPressed("moveCameraRight")) {
            double x = transformationComponent.getPositionX() + (Engine.stepTimeDelta * cameraComponent.getCameraMoveSpeed());
            transformationComponent.setPositionX(x);
        }
        if (KeyHandler.getInstance().isKeyForActionPressed("moveCameraLeft")) {
            double x = transformationComponent.getPositionX() - (Engine.stepTimeDelta * cameraComponent.getCameraMoveSpeed());
            transformationComponent.setPositionX(x);
        }
        if (player != null) {
            TransformationComponent playerTransform = player.getComponentOfType(TransformationComponent.class);
            Vector2d temp = new Vector2d(transformationComponent.getPositionX(), transformationComponent.getPositionY());
            temp.lerp(new Vector2d(playerTransform.getPositionX(), playerTransform.getPositionY()), ((Engine.stepTimeDelta) * LERP_SPEED));
            transformationComponent.setPositionX(temp.x());
            transformationComponent.setPositionY(temp.y());
        }
        RenderService.cameraPosX = transformationComponent.getPositionX();
        RenderService.cameraPosY = transformationComponent.getPositionY();
    }

    @Override
    public boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(CameraComponent.class)
                && entity.hasComponentOfType(TransformationComponent.class);
    }
}
