package engine.system;

import engine.EntityKeyConstants;
import engine.component.*;
import engine.component.base.*;
import engine.component.tag.*;
import engine.entity.ComponentBuilder;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.WorldTileType;
import engine.handler.EntityHandler;
import engine.handler.template.ComponentTemplateHandler;
import engine.loader.template.ComponentTemplate;
import engine.service.LootSpawner;
import engine.service.MobSpawner;
import engine.service.UISceneService;
import engine.service.WorldSceneService;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class StatSystem {

    public static void processEntity(Entity entity) {
        StatComponent statComponent = entity.getComponentOfType(StatComponent.class);
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        if (!statComponent.isDead()) {
            statComponent.setMovementSpeedModifier(1.0);
            if (entity.hasComponentOfType(DoTComponent.class)) {
                DoTComponent doTComponent = entity.getComponentOfType(DoTComponent.class);
                if (doTComponent.isSlow()) {
                    statComponent.setMovementSpeedModifier(doTComponent.getSlowModifierValue());
                }
            }
            if (statComponent.getCurrentShield() > 0.0 && !entity.hasComponentOfType(ShieldComponent.class)) {
                entity.addComponent(new ShieldComponent(transformationComponent));
            } else if (statComponent.getCurrentShield() == 0.0 && entity.hasComponentOfType(ShieldComponent.class)) {
                entity.removeComponent(ShieldComponent.class);
            }
            if (statComponent.getCurrentHealthPoints() <= 0) {
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
                handleItemDrops(entity,transformationComponent);
            }
            if (statComponent.isDropsXP()) {
                handleXPDrops(transformationComponent);
            }
            if (entity.hasComponentOfType(BossComponent.class)) {
                handleBossDeath();
            }
        }
    }

    private static void handleBossDeath() {
        LootSpawner.spawnBossLoot();
        LootSpawner.spawnPortal(16.5, 17.5);
        MobSpawner.toggleMobSpawning(false);
        Entity player = EntityHandler.getInstance().getEntityWithComponent(PlayerTag.class);
        UISceneService.getInstance().showOutOfCombatUI();
        player.getComponentOfType(StatComponent.class).healToFull();
        player.getComponentOfType(StatComponent.class).setLevel(player.getComponentOfType(StatComponent.class).getLevel() + 1);
    }

    private static void handlePlayerDeath(Entity entity) {
        AudioComponent audioComponent = new AudioComponent();
        audioComponent.setAudioKey("gameOver");
        audioComponent.setPlayOnce(true);
        entity.addComponent(audioComponent);
        entity.removeComponent(LightSourceComponent.class);
        entity.getComponentOfType(StatComponent.class).setEquippedGun(null);
        EntityHandler.getInstance().removeObject("GUN");
        UISceneService.getInstance().showGameOverDialog();
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

    private static void handleItemDrops(Entity entity, TransformationComponent transformationComponent) {
        String lootDropRarity = (entity.hasComponentOfType(MobTag.class) || entity.hasComponentOfType(RangedMobTag.class)) ?
                "Common":
                "Rare";
        List<ComponentTemplate> availableUpgrades =
                ComponentTemplateHandler.getInstance()
                        .getAllObjects()
                        .stream()
                        .filter(template -> template.getType().equals("UpgradeComponent")
                                && template.getModifiers().get("upgradeRarity").equals(lootDropRarity))
                        .collect(Collectors.toList());
        if (Math.random() > 0.85) {
            Entity item = EntityBuilder.builder()
                    .fromTemplate("item")
                    .at(transformationComponent.getPositionX(), transformationComponent.getPositionY())
                    .build();
            double lootChoice = Math.random();
            if(lootChoice > 0.8){
                item.getComponentOfType(RenderComponent.class).setTextureKey("medkit");
                item.addComponent(new MedKitTag());
            }else{
                int upgradeId = (int) Math.floor(Math.random() * availableUpgrades.size());
                UpgradeComponent upgradeComponent = (UpgradeComponent) ComponentBuilder.fromTemplate(availableUpgrades.get(upgradeId).getTemplateName());
                item.addComponent(upgradeComponent);
                item.getComponentOfType(RenderComponent.class).setTextureKey(upgradeComponent.getUpgradeIcon());
            }
            EntityHandler.getInstance().addObject(EntityKeyConstants.ITEM_ENTITY_PREFIX + RandomStringUtils.randomAlphanumeric(8), item);
            addRandomDropMomentum(item);
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
