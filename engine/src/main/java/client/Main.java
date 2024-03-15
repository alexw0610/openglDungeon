package client;

import engine.Engine;
import engine.EntityKeyConstants;
import engine.component.GunComponent;
import engine.component.StatComponent;
import engine.component.UpgradeComponent;
import engine.component.base.CameraComponent;
import engine.component.base.RenderComponent;
import engine.component.base.TransformationComponent;
import engine.entity.ComponentBuilder;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.handler.EntityHandler;
import engine.handler.UIHandler;
import engine.service.GameStateService;
import engine.service.WorldSceneService;

public class Main {

    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.start();

        EntityHandler.setInstance(engine.getEntityHandler());
        EntityBuilder.builder()
                .withComponent(new TransformationComponent(0.0, 0.0))
                .withComponent(new CameraComponent())
                .at(0, 0)
                .buildAndInstantiate(EntityKeyConstants.CAMERA_ENTITY_KEY);

        GameStateService.initPlayer();
        //GameStateService.initTestMode();
        UIHandler.setInstance(engine.getUIHandler());
        engine.setPaused(false);
    }
}
