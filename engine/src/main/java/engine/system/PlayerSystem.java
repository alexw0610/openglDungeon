package engine.system;

import com.jogamp.newt.event.KeyEvent;
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
import engine.enums.UIGroupKey;
import engine.enums.UpgradeType;
import engine.handler.*;
import engine.object.generation.World;
import engine.service.LootSpawner;
import engine.service.MobSpawner;
import engine.service.WorldGenerator;
import engine.service.util.BulletModifierUtil;
import org.joml.Vector2d;

import java.util.List;

import static engine.EngineConstants.SECONDS_TO_NANOSECONDS_FACTOR;
import static engine.service.util.AudioUtil.createSoundEntity;

public class PlayerSystem {

    public static void processEntity(Entity entity) {
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        StatComponent statComponent = entity.getComponentOfType(StatComponent.class);
        GunComponent gunComponent = statComponent.getEquipedGun();
        Vector2d mousePosWS = MouseHandler.getInstance().getMousePositionWorldSpace();
        Vector2d direction = mousePosWS.sub(transformationComponent.getPosition()).normalize();

        if (!statComponent.isDead()) {
            List<Entity> items = EntityHandler.getInstance().getAllEntitiesWithComponents(ItemTag.class);
            handleItemPickup(entity, items, transformationComponent, statComponent);
            handleMouseInput(entity, statComponent, gunComponent, direction);
        }
        if (KeyHandler.getInstance().isKeyForActionPressed("openInventory")) {
            KeyHandler.getInstance().setKeyReleased(KeyEvent.VK_I);
            //TODO: Please clean this up in the future. PLEASE! :(
            if (UIStateHandler.getInstance().isInventoryOpen()) {
                UIStateHandler.getInstance().closeInventory();
            } else {
                UIStateHandler.getInstance().showInventory();
                LootSpawner.spawnLootOptions();
            }
        }
    }

    private static void handleMouseInput(Entity entity, StatComponent statComponent, GunComponent gunComponent, Vector2d direction) {
        if (gunComponent != null
                && gunComponent.isPrimaryAttack()
                && MouseHandler.getInstance().isKeyForActionPressed("mouseButtonPrimary")
                && System.nanoTime() - statComponent.getLastShotPrimary()
                > (gunComponent.getPrimaryBaseAttackSpeed() * statComponent.getAttackSpeedPrimary() * SECONDS_TO_NANOSECONDS_FACTOR)) {
            handlePrimaryAttack(statComponent, entity, gunComponent, direction);
        }
        if (gunComponent != null
                && gunComponent.isSecondaryAttack()
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
        EntityHandler.getInstance().addObject(projectile);
    }

    private static void handleItemPickup(Entity entity, List<Entity> items, TransformationComponent transformationComponent, StatComponent statComponent) {
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
                            .getUpgradeCategory().equals("statModifier")) {
                        handleStatUpgrade(item, statComponent);
                    }
                }
                if (item.hasComponentOfType(GunComponent.class)) {
                    createSoundEntity("upgrade", transformationComponent);
                    statComponent.addGuns(item.getComponentOfType(GunComponent.class));
                    if (statComponent.getEquipedGun() == null) {
                        statComponent.setEquipedGun(item.getComponentOfType(GunComponent.class));
                        EntityHandler.getInstance()
                                .getEntityWithId("GUN")
                                .getComponentOfType(RenderComponent.class)
                                .setTextureKey(item.getComponentOfType(GunComponent.class)
                                        .getGunSprite());
                    }
                }
                if (item.hasComponentOfType(StartLevelTag.class)) {
                    Entity teleport = EntityBuilder.builder()
                            .fromTemplate("teleport")
                            .buildAndInstantiate();
                    teleport.addComponent(transformationComponent);
                    handleLevelStart(entity, statComponent);
                    createSoundEntity("teleport", transformationComponent);
                }
                EntityHandler.getInstance().removeObject(item.getEntityId());
            }
        }
    }

    private static void handleLevelStart(Entity entity, StatComponent statComponent) {
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

    private static void handleStatUpgrade(Entity item, StatComponent statComponent) {
        UpgradeComponent upgradeComponent = item.getComponentOfType(UpgradeComponent.class);
        if (UpgradeType.MAX_HEALTH.getKey().equals(upgradeComponent.getUpgradeType())) {
            statComponent.setMaxHealthPoints(statComponent.getMaxHealthPoints() + upgradeComponent.getModifierValue());
        } else if (UpgradeType.ATTACK_SPEED.getKey().equals(upgradeComponent.getUpgradeType())) {
            if (upgradeComponent.getUpgradeSlot().equals("primary")) {
                statComponent.setAttackSpeedPrimary(statComponent.getAttackSpeedPrimary() - upgradeComponent.getModifierValue());
            } else {
                statComponent.setAttackSpeedSecondary(statComponent.getAttackSpeedSecondary() - upgradeComponent.getModifierValue());
            }
        } else if (UpgradeType.ATTACK_DAMAGE.getKey().equals(upgradeComponent.getUpgradeType())) {
            if (upgradeComponent.getUpgradeSlot().equals("primary")) {
                statComponent.setBaseBulletDamagePrimary(statComponent.getBaseBulletDamagePrimary() * upgradeComponent.getModifierValue());
            } else {
                statComponent.setBaseBulletDamageSecondary(statComponent.getBaseBulletDamageSecondary() * upgradeComponent.getModifierValue());
            }
        } else if (UpgradeType.BULLET_VELOCITY.getKey().equals(upgradeComponent.getUpgradeType())) {
            if (upgradeComponent.getUpgradeSlot().equals("primary")) {
                statComponent.setBulletSpeedPrimary(statComponent.getBulletSpeedPrimary() * upgradeComponent.getModifierValue());
            } else {
                statComponent.setBulletSpeedSecondary(statComponent.getBulletSpeedSecondary() * upgradeComponent.getModifierValue());
            }
        } else if (UpgradeType.MOVEMENT_SPEED.getKey().equals(upgradeComponent.getUpgradeType())) {
            statComponent.setMovementSpeed(statComponent.getMovementSpeed() + upgradeComponent.getModifierValue());
        } else if (UpgradeType.MAX_ARMOR.getKey().equals(upgradeComponent.getUpgradeType())) {
            statComponent.setMaxArmor(statComponent.getMaxArmor() + upgradeComponent.getModifierValue());
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(PlayerTag.class);
    }
}
