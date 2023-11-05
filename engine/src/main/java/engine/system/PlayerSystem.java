package engine.system;

import engine.Engine;
import engine.component.*;
import engine.component.base.RenderComponent;
import engine.component.base.TransformationComponent;
import engine.component.internal.CreatedByComponent;
import engine.component.tag.*;
import engine.entity.ComponentBuilder;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.BulletModifier;
import engine.enums.Slot;
import engine.handler.EntityHandler;
import engine.handler.KeyHandler;
import engine.handler.MouseHandler;
import engine.service.GameStateService;
import engine.service.MobSpawner;
import engine.service.UISceneService;
import engine.service.WorldSceneService;
import engine.service.util.AudioUtil;
import engine.system.util.AttackUtil;
import engine.system.util.ProjectileUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.joml.Vector2d;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static engine.EngineConstants.SECONDS_TO_NANOSECONDS_FACTOR;
import static engine.EntityKeyConstants.*;
import static engine.service.util.AudioUtil.createSoundEntity;
import static engine.service.util.VectorUtil.rotateVector;
import static engine.system.util.StatUpgradeUtil.handleStatUpgrade;

public class PlayerSystem {

    public static void processEntity(Entity entity) {
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        StatComponent statComponent = entity.getComponentOfType(StatComponent.class);
        GunComponent gunComponent = statComponent.getEquippedGun();
        Vector2d mousePosWS = MouseHandler.getInstance().getMousePositionWorldSpace();
        Vector2d direction = mousePosWS.sub(transformationComponent.getPosition()).normalize();
        if (!statComponent.isDead()) {
            handleItemPickup(transformationComponent, statComponent);
            handleMouseInput(entity, statComponent, gunComponent, direction);
        }
        handleKeyInput();
    }

    private static void handleKeyInput() {
        if (KeyHandler.getInstance().isKeyForActionPressed("openInventory", true)) {
            if (UISceneService.getInstance().isInventoryVisible()) {
                UISceneService.getInstance().hideInventory();
            } else if (!UISceneService.getInstance().isCloseDialogVisible()
                    && !UISceneService.getInstance().isGameOverDialogVisible()) {
                UISceneService.getInstance().showInventory();
            }
        }
        if (KeyHandler.getInstance().isKeyForActionPressed("closeGame", true)) {
            if (UISceneService.getInstance().isInventoryVisible()) {
                UISceneService.getInstance().hideInventory();
            } else if (UISceneService.getInstance().isCloseDialogVisible()
                    || UISceneService.getInstance().isGameOverDialogVisible()) {
                Engine.requestShutdown();
            } else {
                UISceneService.getInstance().showCloseDialog();
            }
        }
        if (UISceneService.getInstance().isCloseDialogVisible()
                && KeyHandler.getInstance().isKeyForActionPressed("resumeGame", true)) {
            UISceneService.getInstance().hideCloseDialog();
        }
        if (UISceneService.getInstance().isGameOverDialogVisible()
                && KeyHandler.getInstance().isKeyForActionPressed("resumeGame", true)) {
            GameStateService.initPlayer();
        }
    }

    private static void handleMouseInput(Entity entity, StatComponent statComponent, GunComponent gunComponent, Vector2d direction) {
        if (gunComponent != null
                && gunComponent.isPrimaryAttack()
                && !UISceneService.getInstance().isInventoryVisible()
                && MouseHandler.getInstance().isKeyForActionPressed("mouseButtonPrimary")
                && System.nanoTime() - statComponent.getLastAttackPrimary()
                > ((gunComponent.getPrimaryBaseAttackSpeed() * statComponent.getAttackSpeedPrimary()) * SECONDS_TO_NANOSECONDS_FACTOR)) {
            statComponent.setLastAttackPrimary(System.nanoTime());
            statComponent.setAttackCountPrimary(statComponent.getAttackCountPrimary() + 1);
            handleAttack(Slot.PRIMARY, statComponent, entity, gunComponent, direction);
        }
        if (gunComponent != null
                && gunComponent.isSecondaryAttack()
                && !UISceneService.getInstance().isInventoryVisible()
                && MouseHandler.getInstance().isKeyForActionPressed("mouseButtonSecondary")
                && System.nanoTime() - statComponent.getLastAttackSecondary()
                > ((gunComponent.getSecondaryBaseAttackSpeed() * statComponent.getAttackSpeedSecondary()) * SECONDS_TO_NANOSECONDS_FACTOR)) {
            statComponent.setLastAttackSecondary(System.nanoTime());
            statComponent.setAttackCountSecondary(statComponent.getAttackCountSecondary() + 1);
            handleAttack(Slot.SECONDARY, statComponent, entity, gunComponent, direction);
        }
    }

    private static void handleAttack(Slot slot, StatComponent statComponent, Entity player, GunComponent gunComponent, Vector2d direction) {
        TransformationComponent transformationComponent = player.getComponentOfType(TransformationComponent.class);
        Entity projectilePrimary = getModifiedProjectileEntity(slot, statComponent, player, gunComponent, transformationComponent);
        if (projectilePrimary.getComponentOfType(ProjectileComponent.class)
                .getAttackComponent().getBulletModifierList()
                .contains(BulletModifier.TRIPLE_SHOT)) {
            Entity projectileLeft = getModifiedProjectileEntity(slot, statComponent, player, gunComponent, transformationComponent);
            projectileLeft.addComponent(new CreatedByComponent(player));
            projectileLeft.getComponentOfType(ProjectileComponent.class)
                    .setDirection(rotateVector(new Vector2d(direction), 0.2));
            Entity projectileRight = getModifiedProjectileEntity(slot, statComponent, player, gunComponent, transformationComponent);
            projectileRight.addComponent(new CreatedByComponent(player));
            projectileRight.getComponentOfType(ProjectileComponent.class)
                    .setDirection(rotateVector(new Vector2d(direction), -0.2));
            EntityHandler.getInstance().addObject(PROJECTILE_PREFIX + RandomStringUtils.randomAlphanumeric(6), projectileLeft);
            EntityHandler.getInstance().addObject(PROJECTILE_PREFIX + RandomStringUtils.randomAlphanumeric(6), projectileRight);
        }
        List<BulletModifier> bulletModifierList = projectilePrimary
                .getComponentOfType(ProjectileComponent.class)
                .getAttackComponent()
                .getBulletModifierList();
        if (bulletModifierList.contains(BulletModifier.BLAST_WAVE)) {
            AttackComponent attack = (AttackComponent) ComponentBuilder.fromTemplate("dashAttack");
            attack.setStunDuration(0.0);
            EntityBuilder.builder()
                    .withComponent(attack)
                    .at(transformationComponent.getPosition().x(), transformationComponent.getPosition().y())
                    .withComponent(new CreatedByComponent(player))
                    .buildAndInstantiate();
            AudioUtil.createSoundEntity("impact", transformationComponent);
        }
        if (bulletModifierList.contains(BulletModifier.CHARGE_SHOT)) {
            statComponent.setCurrentShield(statComponent.getCurrentShield() + 0.5);
        }
        if (bulletModifierList.contains(BulletModifier.SHIELD_DISCHARGE)) {
            statComponent.setCurrentShield(0.0);
        }
        projectilePrimary.getComponentOfType(ProjectileComponent.class).setDirection(direction);
        projectilePrimary.addComponent(new CreatedByComponent(player));
        createSoundEntity("gunshot", transformationComponent);
        EntityHandler.getInstance().addObject(PROJECTILE_PREFIX + RandomStringUtils.randomAlphanumeric(6), projectilePrimary);
    }

    private static Entity getModifiedProjectileEntity(Slot slot, StatComponent statComponent, Entity player, GunComponent gunComponent, TransformationComponent transformationComponent) {
        Entity projectile = EntityBuilder.builder()
                .fromTemplate("projectile")
                .at(transformationComponent.getPosition().x(), transformationComponent.getPosition().y())
                .build();
        ProjectileComponent projectileComponent = projectile.getComponentOfType(ProjectileComponent.class);
        AttackComponent attackComponent = AttackUtil.getAdjustedAttackComponent(slot,
                statComponent,
                gunComponent,
                projectileComponent.getOnCollisionAttack());
        ProjectileUtil.adjustProjectileWithStats(slot,
                projectileComponent,
                statComponent,
                gunComponent);
        projectileComponent.setAttackComponent(attackComponent);
        ProjectileUtil.addActiveBulletModifiers(slot, gunComponent, projectileComponent.getAttackComponent());
        handelAppliedBulletModifiers(player, transformationComponent, projectileComponent, attackComponent);
        return projectile;
    }

    private static void handelAppliedBulletModifiers(Entity player, TransformationComponent transformationComponent, ProjectileComponent projectileComponent, AttackComponent attackComponent) {
        if (attackComponent.getBulletModifierList().contains(BulletModifier.HIGH_VELOCITY)) {
            attackComponent.setDamage(attackComponent.getDamage() + projectileComponent.getSpeed() * 2);
        }
        if (attackComponent.getBulletModifierList().contains(BulletModifier.SHIELD_DISCHARGE)) {
            StatComponent statComponent = player.getComponentOfType(StatComponent.class);
            attackComponent.setDamage(attackComponent.getDamage() + statComponent.getCurrentShield() * 0.5);
        }
        if (attackComponent.getBulletModifierList().contains(BulletModifier.HEAT_SEEKING)) {
            Vector2d playerPosition = transformationComponent.getPosition();
            Optional<Entity> targetEntity =
                    EntityHandler.getInstance()
                            .getAllEntitiesWithAnyOfComponents(MobTag.class, RangedMobTag.class, BossComponent.class)
                            .stream()
                            .filter(mob -> !mob.getComponentOfType(StatComponent.class).isDead())
                            .filter(mob -> mob.getComponentOfType(TransformationComponent.class)
                                    .getPosition()
                                    .distance(playerPosition) < 10.0)
                            .min(Comparator.comparing(mob -> mob.getComponentOfType(TransformationComponent.class)
                                    .getPosition()
                                    .distance(playerPosition)));
            targetEntity.ifPresent(projectileComponent::setTargetEntity);
        }
    }

    private static void handleItemPickup(TransformationComponent transformationComponent, StatComponent statComponent) {
        List<Entity> items = EntityHandler.getInstance().getAllEntitiesWithComponents(ItemTag.class);
        for (Entity item : items) {
            if (transformationComponent.getPosition()
                    .distance(item.getComponentOfType(TransformationComponent.class).getPosition()) < 0.8) {
                if (item.hasComponentOfType(XPTag.class)) {
                    statComponent.setXp(statComponent.getXp() + 10);
                    createSoundEntity("pickup", transformationComponent);
                }
                if (item.hasComponentOfType(MedKitTag.class)) {
                    statComponent.setCurrentHealthPoints(Math.min(statComponent.getCurrentHealthPoints() + 30, statComponent.getMaxHealthPoints()));
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
                    startLevel(transformationComponent, statComponent);
                    break;
                }
                if (item.hasComponentOfType(LootChoiceTag.class)) {
                    EntityHandler.getInstance().removeObjectsWithPrefix(LOOT_CHOICE_PREFIX);
                    break;
                }
                EntityHandler.getInstance().removeObject(item.getEntityId());
            }
        }
    }

    private static void startLevel(TransformationComponent transformationComponent, StatComponent statComponent) {
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
            statComponent.unequipGunUpgrades();
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
