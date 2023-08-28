package engine.system.base;

import engine.Engine;
import engine.EngineConstants;
import engine.component.base.CameraComponent;
import engine.component.tag.CameraTargetTag;
import engine.component.base.TransformationComponent;
import engine.entity.Entity;
import engine.handler.EntityHandler;
import engine.handler.KeyHandler;
import engine.service.RenderService;
import org.joml.Vector2d;

public class CameraSystem {

    public static void processEntity(Entity entity) {
        Entity cameraTarget = EntityHandler.getInstance().getEntityWithComponent(CameraTargetTag.class);
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
        if (KeyHandler.getInstance().isKeyForActionPressed("zoomCameraIn")) {
            double z = cameraComponent.getCameraZoom() - (Engine.stepTimeDelta * EngineConstants.STEP_TIME_FACTOR * cameraComponent.getCameraZoomSpeed());
            cameraComponent.setCameraZoom(Math.max(z, 0.05));
        }
        if (KeyHandler.getInstance().isKeyForActionPressed("zoomCameraOut")) {
            double z = cameraComponent.getCameraZoom() + (Engine.stepTimeDelta * EngineConstants.STEP_TIME_FACTOR * cameraComponent.getCameraZoomSpeed());
            cameraComponent.setCameraZoom(Math.min(z, 0.90));
        }
        if (cameraTarget != null) {
            TransformationComponent targetTransform = cameraTarget.getComponentOfType(TransformationComponent.class);
            Vector2d source = new Vector2d(transformationComponent.getPositionX(), transformationComponent.getPositionY());
            Vector2d target = new Vector2d(targetTransform.getPositionX(), targetTransform.getPositionY());
            if (source.distance(target) > 10) {
                transformationComponent.setPositionX(target.x());
                transformationComponent.setPositionY(target.y());
            } else {
                transformationComponent.setPositionX(target.x());
                transformationComponent.setPositionY(target.y());
                //source.lerp(target, Math.max(Math.pow(source.distance(target), 1), 0.1) * (Engine.stepTimeDelta) * LERP_SPEED);
                //transformationComponent.setPositionX(source.x());
                //transformationComponent.setPositionY(source.y());
            }
        }
        RenderService.cameraPosX = transformationComponent.getPositionX();
        RenderService.cameraPosY = transformationComponent.getPositionY();
        RenderService.cameraPosZ = cameraComponent.getCameraZoom();
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(CameraComponent.class)
                && entity.hasComponentOfType(TransformationComponent.class);
    }
}
