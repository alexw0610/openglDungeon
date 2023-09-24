package engine.system;

import engine.EntityKeyConstants;
import engine.component.AIComponent;
import engine.component.BossComponent;
import engine.component.ShieldComponent;
import engine.component.StatComponent;
import engine.component.base.*;
import engine.component.tag.*;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.WorldTileType;
import engine.handler.EntityHandler;
import engine.service.LootSpawner;
import engine.service.UISceneService;
import engine.service.WorldSceneService;
import org.apache.commons.lang3.RandomStringUtils;

public class StatSystem {

    public static void processEntity(Entity entity) {
        StatComponent statComponent = entity.getComponentOfType(StatComponent.class);
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        if (!statComponent.isDead()) {
            if (statComponent.getCurrentShield() > 0.0 && !entity.hasComponentOfType(ShieldComponent.class)) {
                entity.addComponent(new ShieldComponent(transformationComponent));
            } else if (statComponent.getCurrentShield() == 0.0 && entity.hasComponentOfType(ShieldComponent.class)) {
                entity.removeComponent(ShieldComponent.class);
            }
            if (statComponent.getCurrentHealthpoints() <= 0) {
                handleEntityDeath(entity, statComponent, transformationComponent);
            }
            if (entity.hasComponentOfType(PlayerTag.class)
                    && statComponent.getXPPercentage() == 1.0) {
                handleLevelUp(statComponent);
            }
        }
    }

    private static void handleLevelUp(StatComponent statComponent) {
        statComponent.setLevel(statComponent.getLevel() + 1);
        statComponent.setXp(0.0);
        statComponent.healToFull();
        WorldSceneService.loadSafeZone();
        UISceneService.getInstance().showOutOfCombatUI();
    }

    private static void handleEntityDeath(Entity entity, StatComponent statComponent, TransformationComponent transformationComponent) {
        if (entity.hasComponentOfType(TerrainTag.class)) {
            handleTerrainDestruction(entity);
        } else {
            entity.removeComponent(PhysicsComponent.class);
            entity.removeComponent(AIComponent.class);
            entity.removeComponent(AiTargetTag.class);
            entity.getComponentOfType(AnimationComponent.class).setAnimationContinuous(false);
            entity.getComponentOfType(AnimationComponent.class).setMovementDriven(false);
            entity.getComponentOfType(AnimationComponent.class).setAnimationFinished(false);
            entity.getComponentOfType(AnimationComponent.class).setAnimationRow(2.0);
            entity.getComponentOfType(AnimationComponent.class).setAnimationFrame(0.0);
            entity.getComponentOfType(RenderComponent.class).setShadeless(false);
            entity.removeComponent(CollisionComponent.class);
            statComponent.setDead(true);
            if (entity.hasComponentOfType(PlayerTag.class)) {
                handlePlayerDeath(entity);
            }
            if (statComponent.isDropsItems()) {
                handleItemDrops(transformationComponent);
            }
            if (statComponent.isDropsXP()) {
                handleXPDrops(transformationComponent);
            }
            if (entity.hasComponentOfType(BossComponent.class)) {
                LootSpawner.spawnBossLoot();
                LootSpawner.spawnPortal();
                Entity player = EntityHandler.getInstance().getEntityWithComponent(PlayerTag.class);
                UISceneService.getInstance().showOutOfCombatUI();
                player.getComponentOfType(StatComponent.class).healToFull();
                player.getComponentOfType(StatComponent.class).setLevel(player.getComponentOfType(StatComponent.class).getLevel() + 1);
            }
        }
    }

    private static void handlePlayerDeath(Entity entity) {
        AudioComponent audioComponent = new AudioComponent();
        audioComponent.setAudioKey("gameOver");
        audioComponent.setPlayOnce(true);
        entity.addComponent(audioComponent);
        entity.removeComponent(LightSourceComponent.class);
        entity.getComponentOfType(StatComponent.class).setEquippedGun(null);
        EntityHandler.getInstance().removeObject("GUN");
    }

    private static void handleXPDrops(TransformationComponent transformationComponent) {
        int xpAmount = 1;
        xpAmount += (int) (Math.random() * 3);
        for (int i = 0; i < xpAmount; i++) {
            Entity xp = EntityBuilder.builder()
                    .fromTemplate("item")
                    .at(transformationComponent.getPositionX(), transformationComponent.getPositionY())
                    .build();
            xp.getComponentOfType(RenderComponent.class).setTextureKey("xp");
            xp.getComponentOfType(RenderComponent.class).setScale(0.6 + ((Math.random() - 0.5) * 0.2));
            xp.addComponent(new XPTag());
            addRandomDropMomentum(xp);
            EntityHandler.getInstance().addObject(EntityKeyConstants.ITEM_ENTITY_PREFIX + RandomStringUtils.randomAlphanumeric(8), xp);
        }
    }

    private static void addRandomDropMomentum(Entity entity) {
        entity.getComponentOfType(PhysicsComponent.class).setMomentumX((Math.random() - 0.5) * 2.0);
        entity.getComponentOfType(PhysicsComponent.class).setMomentumY((Math.random() - 0.5) * 2.0);
    }

    private static void handleItemDrops(TransformationComponent transformationComponent) {
        if (Math.random() > 0.85) {
            Entity medkit = EntityBuilder.builder()
                    .fromTemplate("item")
                    .at(transformationComponent.getPositionX(), transformationComponent.getPositionY())
                    .build();
            medkit.getComponentOfType(RenderComponent.class).setTextureKey("medkit");
            addRandomDropMomentum(medkit);
            medkit.addComponent(new MedKitTag());
            EntityHandler.getInstance().addObject(EntityKeyConstants.ITEM_ENTITY_PREFIX + RandomStringUtils.randomAlphanumeric(8), medkit);
        }
    }

    private static void handleTerrainDestruction(Entity entity) {
        AudioComponent audioComponent = new AudioComponent();
        audioComponent.setAudioKey("break");
        audioComponent.setPlayOnce(true);
        entity.addComponent(audioComponent);
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        EntityHandler.getInstance().removeObject(entity.getEntityId());

        Entity floor = EntityBuilder.builder()
                .fromTemplate("worldGround")
                .at(transformationComponent.getPositionX(), transformationComponent.getPositionY())
                .build();
        floor.getComponentOfType(RenderComponent.class).setTextureKey("floor");
        EntityHandler.getInstance().addObject(floor);
        EntityHandler.getInstance()
                .getWorld()
                .getTile((int) transformationComponent.getPositionX(), (int) transformationComponent.getPositionY())
                .setWorldTileType(WorldTileType.GROUND);
        EntityHandler.getInstance()
                .getWorld()
                .updateNeighboursTileOrientation((int) transformationComponent.getPositionX(), (int) transformationComponent.getPositionY());
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(StatComponent.class);
    }
}
