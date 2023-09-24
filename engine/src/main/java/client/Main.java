package client;

import engine.Engine;
import engine.EntityKeyConstants;
import engine.component.GunComponent;
import engine.component.base.CameraComponent;
import engine.component.base.RenderComponent;
import engine.component.base.TransformationComponent;
import engine.entity.ComponentBuilder;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.handler.EntityHandler;
import engine.handler.UIHandler;
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

        Entity player = EntityBuilder
                .builder()
                .fromTemplate("player")
                .at(0, 0)
                .build();
        EntityHandler.getInstance().addObject(EntityKeyConstants.PLAYER_ENTITY_KEY, player);

        Entity gun = EntityBuilder
                .builder()
                .fromTemplate("gun")
                .at(0, 0)
                .build();
        gun.addComponent(player.getComponentOfType(TransformationComponent.class));
        EntityHandler.getInstance().addObject(EntityKeyConstants.GUN_ENTITY_KEY, gun);

        Entity gunItem = EntityBuilder.builder()
                .fromTemplate("item")
                .at(4.5, 7)
                .buildAndInstantiate();
        gunItem.addComponent(ComponentBuilder.fromTemplate("gunStandardIssueBlaster"));
        gunItem.getComponentOfType(RenderComponent.class)
                .setTextureKey(gunItem
                        .getComponentOfType(GunComponent.class)
                        .getGunSprite());

        Entity gunItem2 = EntityBuilder.builder()
                .fromTemplate("item")
                .at(2.5, 3)
                .buildAndInstantiate();
        gunItem2.addComponent(ComponentBuilder.fromTemplate("gunAdvancedScoutBlaster"));
        gunItem2.getComponentOfType(RenderComponent.class)
                .setTextureKey(gunItem2
                        .getComponentOfType(GunComponent.class)
                        .getGunSprite());

        Entity gunItem3 = EntityBuilder.builder()
                .fromTemplate("item")
                .at(4.5, 3)
                .buildAndInstantiate();
        gunItem3.addComponent(ComponentBuilder.fromTemplate("gunHeavyMarineBlaster"));
        gunItem3.getComponentOfType(RenderComponent.class)
                .setTextureKey(gunItem3
                        .getComponentOfType(GunComponent.class)
                        .getGunSprite());

        Entity gunItem4 = EntityBuilder.builder()
                .fromTemplate("item")
                .at(6.5, 3)
                .buildAndInstantiate();
        gunItem4.addComponent(ComponentBuilder.fromTemplate("gunPreciseReconBlaster"));
        gunItem4.getComponentOfType(RenderComponent.class)
                .setTextureKey(gunItem4
                        .getComponentOfType(GunComponent.class)
                        .getGunSprite());

        WorldSceneService.loadSafeZone();
        UIHandler.setInstance(engine.getUIHandler());
        engine.setPaused(false);
    }
}
