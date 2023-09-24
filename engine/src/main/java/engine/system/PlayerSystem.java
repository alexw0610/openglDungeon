package engine.system;

import engine.Engine;
import engine.component.GunComponent;
import engine.component.ProjectileComponent;
import engine.component.StatComponent;
import engine.component.UpgradeComponent;
import engine.component.base.RenderComponent;
import engine.component.base.TransformationComponent;
import engine.component.internal.CreatedByComponent;
import engine.component.tag.*;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.Slot;
import engine.handler.EntityHandler;
import engine.handler.KeyHandler;
import engine.handler.MouseHandler;
import engine.service.LootSpawner;
import engine.service.MobSpawner;
import engine.service.UISceneService;
import engine.service.WorldSceneService;
import engine.service.util.BulletModifierUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.joml.Vector2d;

import java.util.List;

import static engine.EngineConstants.SECONDS_TO_NANOSECONDS_FACTOR;
import static engine.EntityKeyConstants.*;
import static engine.service.util.AudioUtil.createSoundEntity;
import static engine.system.util.StatUpgradeUtil.handleStatUpgrade;

public class PlayerSystem {

    public static void processEntity(Entity entity) {
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        StatComponent statComponent = entity.getComponentOfType(StatComponent.class);
        GunComponent gunComponent = statComponent.getEquippedGun();
        Vector2d mousePosWS = MouseHandler.getInstance().getMousePositionWorldSpace();
        Vector2d direction = mousePosWS.sub(transformationComponent.getPosition()).normalize();
        if (!statComponent.isDead()) {
            handleItemPickup(entity, transformationComponent, statComponent);
            handleMouseInput(entity, statComponent, gunComponent, direction);
        }
        handleKeyInput();
    }

    private static void handleKeyInput() {
        if (KeyHandler.getInstance().isKeyForActionPressed("openInventory", true)) {
            if (UISceneService.getInstance().isInventoryVisible()) {
                UISceneService.getInstance()
                        .hideInventory();
            } else {
                UISceneService.getInstance().showInventory();
                LootSpawner.spawnSafeZoneLoot();
            }
        }
        if (KeyHandler.getInstance().isKeyForActionPressed("closeGame", true)) {
            if (UISceneService.getInstance().isCloseDialogVisible()) {
                Engine.requestShutdown();
            } else {
                UISceneService.getInstance().showCloseDialog();
            }
        }
        if (UISceneService.getInstance().isCloseDialogVisible()
                && KeyHandler.getInstance().isKeyForActionPressed("resumeGame", true)) {
            UISceneService.getInstance().hideCloseDialog();
        }
    }

    private static void handleMouseInput(Entity entity, StatComponent statComponent, GunComponent gunComponent, Vector2d direction) {
        if (gunComponent != null
                && gunComponent.isPrimaryAttack()
                && !UISceneService.getInstance().isInventoryVisible()
                && MouseHandler.getInstance().isKeyForActionPressed("mouseButtonPrimary")
                && System.nanoTime() - statComponent.getLastShotPrimary()
                > (gunComponent.getPrimaryBaseAttackSpeed() * statComponent.getAttackSpeedPrimary() * SECONDS_TO_NANOSECONDS_FACTOR)) {
            handlePrimaryAttack(statComponent, entity, gunComponent, direction);
        }
        if (gunComponent != null
                && gunComponent.isSecondaryAttack()
                && !UISceneService.getInstance().isInventoryVisible()
                && MouseHandler.getInstance().isKeyForActionPressed("mouseButtonSecondary")
                && System.nanoTime() - statComponent.getLastShotSecondary()
                > (gunComponent.getSecondaryBaseAttackSpeed() * statComponent.getAttackSpeedSecondary() * SECONDS_TO_NANOSECONDS_FACTOR)) {
            //handleSecondaryAttack(statComponent, entity, gunComponent, direction);
        }
    }

    private static void handlePrimaryAttack(StatComponent statComponent, Entity player, GunComponent gunComponent, Vector2d direction) {
        TransformationComponent transformationComponent = player.getComponentOfType(TransformationComponent.class);
        statComponent.setLastShotPrimary(System.nanoTime());
        statComponent.setBulletCountPrimary(statComponent.getBulletCountPrimary() + 1);
        Entity projectile = EntityBuilder.builder()
                .fromTemplate("projectile")
                .at(transformationComponent.getPosition().x(), transformationComponent.getPosition().y()).build();
        BulletModifierUtil.adjustProjectileComponentWithStats(Slot.PRIMARY, projectile.getComponentOfType(ProjectileComponent.class), statComponent, gunComponent);
        projectile.getComponentOfType(ProjectileComponent.class)
                .setDirection(direction);
        projectile.getComponentOfType(RenderComponent.class)
                .setTextureRotation(new Vector2d(1.0, 0.0)
                        .angle(direction) * 180 / 3.14159265359);
        projectile.addComponent(new CreatedByComponent(player));
        createSoundEntity("gunshot", transformationComponent);
        EntityHandler.getInstance().addObject(PROJECTILE_PREFIX + RandomStringUtils.randomAlphanumeric(6), projectile);
    }

    private static void handleItemPickup(Entity entity, TransformationComponent transformationComponent, StatComponent statComponent) {
        List<Entity> items = EntityHandler.getInstance().getAllEntitiesWithComponents(ItemTag.class);
        for (Entity item : items) {
            if (transformationComponent.getPosition()
                    .distance(item.getComponentOfType(TransformationComponent.class).getPosition()) < 0.8) {
                if (item.hasComponentOfType(XPTag.class)) {
                    statComponent.setXp(statComponent.getXp() + 10);
                    createSoundEntity("pickup", transformationComponent);
                }
                if (item.hasComponentOfType(MedKitTag.class)) {
                    statComponent.setCurrentHealthPoints(Math.min(statComponent.getCurrentHealthpoints() + 30, statComponent.getMaxHealthPoints()));
                    createSoundEntity("medkit", transformationComponent);
                }
                if (item.hasComponentOfType(UpgradeComponent.class)) {
                    statComponent.addUpgrade(item.getComponentOfType(UpgradeComponent.class));
                    createSoundEntity("upgrade", transformationComponent);
                    if (item.getComponentOfType(UpgradeComponent.class)
                            .getUpgradeCategory().equals("playerStatModifier")
                            || item.getComponentOfType(UpgradeComponent.class)
                            .getUpgradeCategory().equals("gunStatModifier")) {
                        handleStatUpgrade(item.getComponentOfType(UpgradeComponent.class), statComponent);
                    }
                }
                if (item.hasComponentOfType(GunComponent.class)) {
                    equipGun(transformationComponent, statComponent, item);
                }
                if (item.hasComponentOfType(StartLevelTag.class)) {
                    startLevel(entity, transformationComponent, statComponent);
                }
                if (item.hasComponentOfType(LootChoiceTag.class)) {
                    EntityHandler.getInstance().removeObjectsWithPrefix(LOOT_CHOICE_PREFIX);
                    break;
                }
                EntityHandler.getInstance().removeObject(item.getEntityId());
            }
        }
    }

    private static void startLevel(Entity entity, TransformationComponent transformationComponent, StatComponent statComponent) {
        Entity teleport = EntityBuilder.builder()
                .fromTemplate("teleport")
                .buildAndInstantiate(ITEM_ENTITY_PREFIX + RandomStringUtils.randomAlphanumeric(6));
        teleport.addComponent(transformationComponent);
        handleLevelStart(statComponent.getLevel());
        createSoundEntity("teleport", transformationComponent);
    }

    private static void equipGun(TransformationComponent transformationComponent, StatComponent statComponent, Entity item) {
        createSoundEntity("upgrade", transformationComponent);
        statComponent.addGuns(item.getComponentOfType(GunComponent.class));
        if (statComponent.getEquippedGun() != null) {
            statComponent.uneqiupGunUpgrades();
        }
        statComponent.setEquippedGun(item.getComponentOfType(GunComponent.class));
        EntityHandler.getInstance()
                .getEntityWithId("GUN")
                .getComponentOfType(RenderComponent.class)
                .setTextureKey(item.getComponentOfType(GunComponent.class)
                        .getGunSprite());
    }

    private static void handleLevelStart(int level) {
        UISceneService.getInstance().showCombatUI();
        MobSpawner.setDifficultyLevel(level);
        if (level % 5 == 0) {
            WorldSceneService.loadBossFight();
        } else {
            WorldSceneService.loadLevel();
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(PlayerTag.class);
    }
}
