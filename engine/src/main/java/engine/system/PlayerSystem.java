package engine.system;

import engine.component.GunComponent;
import engine.component.StatComponent;
import engine.component.UpgradeComponent;
import engine.component.base.TransformationComponent;
import engine.component.tag.*;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.UIGroupKey;
import engine.enums.UpgradeType;
import engine.handler.EntityHandler;
import engine.handler.UIHandler;
import engine.object.generation.World;
import engine.service.LootSpawner;
import engine.service.MobSpawner;
import engine.service.WorldGenerator;

import java.util.List;

public class PlayerSystem {

    public static void processEntity(Entity entity) {
        List<Entity> items = EntityHandler.getInstance().getAllEntitiesWithComponents(ItemTag.class);
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        StatComponent statComponent = entity.getComponentOfType(StatComponent.class);
        Entity gun = EntityHandler.getInstance().getEntityWithComponent(GunComponent.class);
        GunComponent gunComponent = null;
        if (gun != null) {
            gunComponent = gun.getComponentOfType(GunComponent.class);
        }
        if (!statComponent.isDead()) {
            handleItemPickup(entity, items, transformationComponent, statComponent, gunComponent);
        }
    }

    private static void handleItemPickup(Entity entity, List<Entity> items, TransformationComponent transformationComponent, StatComponent statComponent, GunComponent gunComponent) {
        for (Entity item : items) {
            if (transformationComponent.getPosition()
                    .distance(item.getComponentOfType(TransformationComponent.class).getPosition()) < 0.8) {
                if (item.hasComponentOfType(XPTag.class)) {
                    statComponent.setXp(statComponent.getXp() + 10);
                }
                if (item.hasComponentOfType(MedKitTag.class)) {
                    statComponent.setCurrentHealthPoints(Math.min(statComponent.getCurrentHealthpoints() + 30, statComponent.getMaxHealthPoints()));
                }
                if (item.hasComponentOfType(UpgradeComponent.class)) {
                    handleStatUpgrade(item, statComponent, gunComponent);
                }
                if (item.hasComponentOfType(StartLevelTag.class)) {
                    Entity teleport = EntityBuilder.builder()
                            .fromTemplate("teleport")
                            .buildAndInstantiate();
                    teleport.addComponent(transformationComponent);
                    handleLevelStart(entity, statComponent);
                }
                EntityHandler.getInstance().removeObject(item.getEntityId());
            }
        }
    }

    private static void handleLevelStart(Entity entity, StatComponent statComponent) {
        if (EntityHandler.getInstance().getEntityWithComponent(GunComponent.class) == null) {
            equipGun(entity);
        }
        UIHandler.getInstance().toggleUIGroupVisible(UIGroupKey.STATS, false);
        LootSpawner.clearLoot();
        WorldGenerator.clearWorld();
        UIHandler.getInstance().removeTextObjectsWithPrefix("DT_");
        MobSpawner.setDifficultyLevel(statComponent.getLevel());
        if (statComponent.getLevel() % 6 == 0) {
            World world = WorldGenerator.generateBossRoom();
            entity.getComponentOfType(TransformationComponent.class).setPositionX(8);
            entity.getComponentOfType(TransformationComponent.class).setPositionY(3);
            MobSpawner.toggleMobSpawning(false);
            MobSpawner.spawnBoss();
            EntityHandler.getInstance().setWorld(world);
        } else {
            World world = WorldGenerator.generateLevel();
            WorldGenerator.setPlayerSpawnPosition(entity, world);
            MobSpawner.toggleMobSpawning(true);
        }
    }

    private static void equipGun(Entity entity) {
        Entity gun = EntityBuilder
                .builder()
                .fromTemplate("gun")
                .at(0, 0)
                .build();
        gun.addComponent(entity.getComponentOfType(TransformationComponent.class));
        EntityHandler.getInstance().addObject("GUN", gun);
    }

    private static void handleStatUpgrade(Entity item, StatComponent statComponent, GunComponent gunComponent) {
        UpgradeComponent upgradeComponent = item.getComponentOfType(UpgradeComponent.class);
        if (UpgradeType.MAX_HEALTH.getKey().equals(upgradeComponent.getUpgradeType())) {
            statComponent.setMaxHealthPoints(statComponent.getMaxHealthPoints() + upgradeComponent.getModifierValue());
        } else if (UpgradeType.ATTACK_SPEED.getKey().equals(upgradeComponent.getUpgradeType())) {
            statComponent.setAttackSpeed(statComponent.getAttackSpeed() - upgradeComponent.getModifierValue());
        } else if (UpgradeType.MOVEMENT_SPEED.getKey().equals(upgradeComponent.getUpgradeType())) {
            statComponent.setMovementSpeed(statComponent.getMovementSpeed() + upgradeComponent.getModifierValue());
        } else if (UpgradeType.MAX_ARMOR.getKey().equals(upgradeComponent.getUpgradeType())) {
            statComponent.setMaxArmor(statComponent.getMaxArmor() + upgradeComponent.getModifierValue());
        } else if (UpgradeType.BULLET_VELOCITY.getKey().equals(upgradeComponent.getUpgradeType())) {
            gunComponent.setBulletSpeed(gunComponent.getBulletSpeed() * 1.1);
        } else if (UpgradeType.IMPACT_BULLET.getKey().equals(upgradeComponent.getUpgradeType())) {
            gunComponent.addModifier(UpgradeType.IMPACT_BULLET);
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(PlayerTag.class);
    }
}
