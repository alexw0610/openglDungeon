package client;

import engine.Engine;
import engine.component.TooltipComponent;
import engine.component.base.CameraComponent;
import engine.component.base.RenderComponent;
import engine.component.base.TransformationComponent;
import engine.component.tag.StartLevelTag;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.handler.EntityHandler;
import engine.handler.UIHandler;
import engine.object.generation.World;
import engine.service.MobSpawner;
import engine.service.WorldGenerator;

public class Main {

    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.start();

        EntityHandler.setInstance(engine.getEntityHandler());
        EntityBuilder.builder()
                .withComponent(new TransformationComponent(0.0, 0.0))
                .withComponent(new CameraComponent())
                .at(0, 0)
                .buildAndInstantiate("CAMERA");

        Entity player = EntityBuilder
                .builder()
                .fromTemplate("player")
                .at(0, 0)
                .build();
        EntityHandler.getInstance().addObject("PLAYER", player);

        Entity gunItem = EntityBuilder.builder()
                .fromTemplate("item")
                .at(4.5, 10)
                .buildAndInstantiate();
        gunItem.addComponent(new StartLevelTag());
        gunItem.getComponentOfType(RenderComponent.class).setTextureKey("gun");
        gunItem.addComponent(new TooltipComponent("Pick me up!"));

        World world = WorldGenerator.generateSafeRoom();
        MobSpawner.toggleMobSpawning(false);
        EntityHandler.getInstance().setWorld(world);
        player.getComponentOfType(TransformationComponent.class).setPositionX(4.5);
        player.getComponentOfType(TransformationComponent.class).setPositionY(5);
        UIHandler.setInstance(engine.getUIHandler());
        engine.setPaused(false);
    }
}