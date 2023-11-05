package engine.system;

import engine.EntityKeyConstants;
import engine.component.AttackComponent;
import engine.component.BossComponent;
import engine.component.ProjectileComponent;
import engine.component.StatComponent;
import engine.component.base.RenderComponent;
import engine.component.base.TransformationComponent;
import engine.component.internal.CreatedByComponent;
import engine.component.tag.PlayerTag;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.Slot;
import engine.handler.EntityHandler;
import engine.system.util.AttackUtil;
import engine.system.util.ProjectileUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.joml.Math;
import org.joml.Vector2d;

public class BossSystem {
    public static void processEntity(Entity entity) {
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        StatComponent statComponent = entity.getComponentOfType(StatComponent.class);
        BossComponent bossComponent = entity.getComponentOfType(BossComponent.class);
        Entity player = EntityHandler.getInstance().getEntityWithComponent(PlayerTag.class);
        Vector2d direction = player.getComponentOfType(TransformationComponent.class)
                .getPosition()
                .sub(transformationComponent.getPosition())
                .normalize();
        double playerDistance = player.getComponentOfType(TransformationComponent.class).getPosition()
                .distance(transformationComponent.getPosition());
        if (!statComponent.isDead() && !player.getComponentOfType(StatComponent.class).isDead()) {
            if (System.currentTimeMillis() - bossComponent.getLastPrimaryAttack() > (statComponent.getAttackSpeedPrimary() * 1000)) {
                bossComponent.setLastPrimaryAttack(System.currentTimeMillis());
                attackPrimary(entity, direction, transformationComponent, statComponent);
            }
            if (System.currentTimeMillis() - bossComponent.getLastSecondaryAttack() > (statComponent.getAttackSpeedSecondary() * 1000)) {
                bossComponent.setLastSecondaryAttack(System.currentTimeMillis());
                attackSecondary(entity, transformationComponent, statComponent);
            }
            if (playerDistance < 3.5 && System.currentTimeMillis() - bossComponent.getLastProximityAttack() > 1000) {
                bossComponent.setLastProximityAttack(System.currentTimeMillis());
                attackProximity(transformationComponent, statComponent);
            }
        }
    }

    private static void attackPrimary(Entity entity, Vector2d direction, TransformationComponent transformationComponent, StatComponent statComponent) {
        Entity projectile = EntityBuilder.builder()
                .fromTemplate("projectileAlien")
                .at(transformationComponent.getPosition().x(), transformationComponent.getPosition().y())
                .buildAndInstantiate();
        ProjectileComponent projectileComponent = projectile.getComponentOfType(ProjectileComponent.class);
        AttackComponent attackComponent =
                AttackUtil.getAdjustedAttackComponent(Slot.PRIMARY, statComponent, projectileComponent.getOnCollisionAttack());
        projectileComponent.setAttackComponent(attackComponent);
        ProjectileUtil.adjustProjectileWithStats(Slot.PRIMARY, projectileComponent, statComponent);
        projectile.addComponent(new CreatedByComponent(entity));
        projectile.getComponentOfType(ProjectileComponent.class)
                .setDirection(direction);
        projectile.getComponentOfType(RenderComponent.class).setTextureRotation(new Vector2d(1.0, 0.0).angle(direction) * 180 / 3.14159265359);
    }

    private static void attackSecondary(Entity entity, TransformationComponent transformationComponent, StatComponent statComponent) {

        for (int angle = 0; angle < 8; angle++) {
            Vector2d direction = new Vector2d(
                    Math.sin((angle / 8.0) * (Math.PI * 2)),
                    Math.cos((angle / 8.0) * (Math.PI * 2))
            );
            Entity projectile = EntityBuilder.builder()
                    .fromTemplate("projectileAlien")
                    .at(transformationComponent.getPosition().x(), transformationComponent.getPosition().y())
                    .buildAndInstantiate();
            ProjectileComponent projectileComponent = projectile.getComponentOfType(ProjectileComponent.class);
            AttackComponent attackComponent =
                    AttackUtil.getAdjustedAttackComponent(Slot.PRIMARY, statComponent, projectileComponent.getOnCollisionAttack());
            projectileComponent.setAttackComponent(attackComponent);
            ProjectileUtil.adjustProjectileWithStats(Slot.PRIMARY, projectileComponent, statComponent);
            projectile.addComponent(new CreatedByComponent(entity));
            projectile.getComponentOfType(ProjectileComponent.class)
                    .setDirection(direction);
            projectile.getComponentOfType(RenderComponent.class)
                    .setTextureRotation(new Vector2d(1.0, 0.0).angle(direction) * 180 / 3.14159265359);
        }
    }

    private static void attackProximity(TransformationComponent transformationComponent, StatComponent statComponent) {
        AttackComponent attackComponent = new AttackComponent("bossMelee");
        attackComponent.setDamage(statComponent.getBaseDamagePrimary());
        attackComponent.setKnockback(800.0);
        attackComponent.setAoE(true);
        attackComponent.setTexture("slash_alien");
        attackComponent.setRange(3.5);
        EntityBuilder.builder()
                .withComponent(attackComponent)
                .at(transformationComponent.getPosition().x(), transformationComponent.getPosition().y())
                .buildAndInstantiate(EntityKeyConstants.ATTACK_PREFIX + RandomStringUtils.randomAlphanumeric(6));
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(BossComponent.class);
    }
}
