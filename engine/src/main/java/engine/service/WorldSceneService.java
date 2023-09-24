package engine.service;

import engine.EntityKeyConstants;
import engine.component.base.TransformationComponent;
import engine.entity.Entity;
import engine.handler.EntityHandler;
import engine.handler.UIHandler;
import engine.object.generation.World;

import static engine.EntityKeyConstants.*;

public class WorldSceneService {

    public static void loadSafeZone() {
        clearWorld();
        World world = WorldGenerator.generateSafeRoom();
        EntityHandler.getInstance().setWorld(world);
        MobSpawner.toggleMobSpawning(false);
        LootSpawner.spawnSafeZoneLoot();
        LootSpawner.spawnPortal();

        Entity player = EntityHandler.getInstance().getEntityWithId(PLAYER_ENTITY_KEY);
        player.getComponentOfType(TransformationComponent.class).setPositionX(4.5);
        player.getComponentOfType(TransformationComponent.class).setPositionY(5);

        System.out.println("Entities: " + EntityHandler.getInstance().getEntityCount());
        System.out.println("UI Elements: " + UIHandler.getInstance().getElementCount());
    }

    public static void loadLevel() {
        clearWorld();
        World world = WorldGenerator.generateLevel();
        EntityHandler.getInstance().setWorld(world);
        MobSpawner.toggleMobSpawning(true);

        Entity player = EntityHandler.getInstance().getEntityWithId(PLAYER_ENTITY_KEY);
        WorldGenerator.setPlayerSpawnPosition(player, world);

        System.out.println("Entities: " + EntityHandler.getInstance().getEntityCount());
        System.out.println("UI Elements: " + UIHandler.getInstance().getElementCount());
    }

    public static void loadBossFight() {
        clearWorld();
        World world = WorldGenerator.generateBossRoom();
        EntityHandler.getInstance().setWorld(world);
        MobSpawner.toggleMobSpawning(false);
        MobSpawner.spawnBoss();

        Entity player = EntityHandler.getInstance().getEntityWithId(PLAYER_ENTITY_KEY);
        player.getComponentOfType(TransformationComponent.class).setPositionX(8);
        player.getComponentOfType(TransformationComponent.class).setPositionY(3);

        System.out.println("Entities: " + EntityHandler.getInstance().getEntityCount());
        System.out.println("UI Elements: " + UIHandler.getInstance().getElementCount());
    }

    private static void clearWorld() {
        EntityHandler.getInstance().removeObjectsWithPrefix(EntityKeyConstants.WORLD_TILE_PREFIX);
        EntityHandler.getInstance().removeObjectsWithPrefix(EntityKeyConstants.ITEM_ENTITY_PREFIX);
        EntityHandler.getInstance().removeObjectsWithPrefix(EntityKeyConstants.ATTACK_PREFIX);
        EntityHandler.getInstance().removeObjectsWithPrefix(EntityKeyConstants.DAMAGE_TEXT_PREFIX);
        EntityHandler.getInstance().removeObjectsWithPrefix(EntityKeyConstants.PORTAL_KEY);
        EntityHandler.getInstance().removeObjectsWithPrefix(EntityKeyConstants.AUDIO_ENTITY_PREFIX);
        EntityHandler.getInstance().removeObjectsWithPrefix(EntityKeyConstants.LOOT_CHOICE_PREFIX);
        UIHandler.getInstance().removeAllObjectsWithPrefix(DAMAGE_TEXT_PREFIX);
        UIHandler.getInstance().removeAllObjectsWithPrefix(HEALTH_BAR_PREFIX);
    }
}
