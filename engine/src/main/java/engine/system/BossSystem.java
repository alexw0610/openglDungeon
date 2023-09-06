package engine.system;

import engine.component.*;
import engine.component.base.RenderComponent;
import engine.component.base.TransformationComponent;
import engine.component.internal.CreatedByComponent;
import engine.component.tag.PlayerTag;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.handler.EntityHandler;
import engine.service.MobSpawner;
import org.joml.Math;
import org.joml.Vector2d;

public class BossSystem {
    public static void processEntity(Entity entity) {
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        StatComponent statComponent = entity.getComponentOfType(StatComponent.class);
        BossComponent bossComponent = entity.getComponentOfType(BossComponent.class);
        Entity player = EntityHandler.getInstance().getEntityWithComponent(PlayerTag.class);
        Vector2d direction = player.getComponentOfType(TransformationComponent.class).getPosition().sub(transformationComponent.getPosition()).normalize();
        if (!statComponent.isDead() && !player.getComponentOfType(StatComponent.class).isDead()) {
            if (System.currentTimeMillis() - bossComponent.getLastPrimaryAttack() > (statComponent.getAttackSpeedPrimary() * 1000)) {
                bossComponent.setLastPrimaryAttack(System.currentTimeMillis());
                attackPrimary(entity, direction, transformationComponent);
            }
            if (System.currentTimeMillis() - bossComponent.getLastSecondaryAttack() > (statComponent.getAttackSpeedSecondary() * 1000)) {
                bossComponent.setLastSecondaryAttack(System.currentTimeMillis());
                attackSecondary(entity, transformationComponent);
            }
            if (System.currentTimeMillis() - bossComponent.getLastAddSpawn() > (bossComponent.getAddSpawnIntervalSeconds())) {
                bossComponent.setLastAddSpawn(System.currentTimeMillis());
                MobSpawner.spawnBossAdd(EntityHandler.getInstance().getWorld(), 4, player.getComponentOfType(StatComponent.class).getLevel());
            }
        }
    }

    private static void attackPrimary(Entity entity, Vector2d direction, TransformationComponent transformationComponent) {
        Entity projectile = EntityBuilder.builder()
                .fromTemplate("projectile_alien")
                .at(transformationComponent.getPosition().x(), transformationComponent.getPosition().y())
                .buildAndInstantiate();
        projectile.addComponent(new CreatedByComponent(entity));
        projectile.getComponentOfType(ProjectileComponent.class)
                .setDirection(direction);
        projectile.getComponentOfType(RenderComponent.class).setTextureRotation(new Vector2d(1.0, 0.0).angle(direction) * 180 / 3.14159265359);
    }

    private static void attackSecondary(Entity entity, TransformationComponent transformationComponent) {

        for (int angle = 0; angle < 8; angle++) {
            Vector2d direction = new Vector2d(
                    Math.sin((angle / 8.0) * (Math.PI * 2)),
                    Math.cos((angle / 8.0) * (Math.PI * 2))
            );
            Entity projectile = EntityBuilder.builder()
                    .fromTemplate("projectile_alien_boss")
                    .at(transformationComponent.getPosition().x(), transformationComponent.getPosition().y())
                    .buildAndInstantiate();
            projectile.addComponent(new CreatedByComponent(entity));
            projectile.getComponentOfType(ProjectileComponent.class)
                    .setDirection(direction);
            projectile.getComponentOfType(RenderComponent.class)
                    .setTextureRotation(new Vector2d(1.0, 0.0).angle(direction) * 180 / 3.14159265359);
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(BossComponent.class);
    }
}
