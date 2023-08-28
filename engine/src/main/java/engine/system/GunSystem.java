package engine.system;

import engine.component.GunComponent;
import engine.component.ProjectileComponent;
import engine.component.StatComponent;
import engine.component.base.AudioComponent;
import engine.component.base.PhysicsComponent;
import engine.component.base.RenderComponent;
import engine.component.base.TransformationComponent;
import engine.component.internal.CreatedByComponent;
import engine.component.tag.PlayerTag;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.UpgradeType;
import engine.handler.EntityHandler;
import engine.handler.KeyHandler;
import engine.handler.MouseHandler;
import org.joml.Vector2d;

import static engine.EngineConstants.SECONDS_TO_NANOSECONDS_FACTOR;

public class GunSystem {


    public static void processEntity(Entity entity) {
        Entity player = EntityHandler.getInstance().getEntityWithComponent(PlayerTag.class);
        GunComponent gunComponent = entity.getComponentOfType(GunComponent.class);
        StatComponent statComponent = player.getComponentOfType(StatComponent.class);
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        Vector2d mousePosWS = MouseHandler.getInstance().getMousePositionWorldSpace();
        Vector2d direction = mousePosWS.sub(transformationComponent.getPosition()).normalize();
        if (MouseHandler.getInstance().isKeyForActionPressed("mouseButtonPrimary")
                && System.nanoTime() - gunComponent.getLastShotTime() > (statComponent.getAttackSpeed() * SECONDS_TO_NANOSECONDS_FACTOR)) {
            handlePrimaryAttack(entity, player, gunComponent, transformationComponent, direction);
        }
        if (KeyHandler.getInstance().isKeyForActionPressed("placeBomb")
                && System.nanoTime() - gunComponent.getLastBombTime() > (statComponent.getSecondaryAttackSpeed() * SECONDS_TO_NANOSECONDS_FACTOR)) {
            //handleSecondaryAttack(gunComponent, transformationComponent, direction);
        }
        rotateGunSprite(entity);

    }

    private static void handlePrimaryAttack(Entity entity, Entity player, GunComponent gunComponent, TransformationComponent transformationComponent, Vector2d direction) {
        gunComponent.setLastShotTime(System.nanoTime());
        gunComponent.setBulletCount(gunComponent.getBulletCount() + 1);
        Entity projectile = EntityBuilder.builder()
                .fromTemplate("projectile")
                .at(transformationComponent.getPosition().x(), transformationComponent.getPosition().y()).build();
        if (gunComponent.getModifiers().contains(UpgradeType.IMPACT_BULLET) && gunComponent.getBulletCount() % 4 == 0) {
            projectile.getComponentOfType(ProjectileComponent.class).setOnCollisionAttack("hitAttackBulletExplosive");
        }

        projectile.getComponentOfType(ProjectileComponent.class)
                .setDirection(direction);
        projectile.getComponentOfType(ProjectileComponent.class)
                .setSpeed(gunComponent.getBulletSpeed());
        projectile.getComponentOfType(RenderComponent.class).setTextureRotation(new Vector2d(1.0, 0.0).angle(direction) * 180 / 3.14159265359);
        projectile.addComponent(new CreatedByComponent(player));

        AudioComponent audio = new AudioComponent();
        audio.setPlayOnce(true);
        audio.setAudioKey("gunshot");
        entity.addComponent(audio);
        EntityHandler.getInstance().addObject(projectile);
    }

    private static void handleSecondaryAttack(GunComponent gunComponent, TransformationComponent transformationComponent, Vector2d direction) {
        gunComponent.setLastBombTime(System.nanoTime());
        Entity bomb = EntityBuilder.builder().fromTemplate("bomb")
                .at(transformationComponent.getPosition().x(), transformationComponent.getPosition().y())
                .build();
        bomb.getComponentOfType(PhysicsComponent.class).setMomentumX(direction.x * 0.85);
        bomb.getComponentOfType(PhysicsComponent.class).setMomentumY(direction.y * 0.85);
        EntityHandler.getInstance().addObject(bomb);
    }

    private static void rotateGunSprite(Entity entity) {
        Vector2d mousePosWS = MouseHandler.getInstance().getMousePositionWorldSpace();
        Vector2d direction = mousePosWS.sub(entity.getComponentOfType(TransformationComponent.class).getPosition()).normalize();
        double angle = new Vector2d(1.0, 0.0).angle(direction) * (180 / 3.14159265359);
        if (angle > 90 || angle < -90) {
            entity.getComponentOfType(RenderComponent.class)
                    .setMirrored(true);
            entity.getComponentOfType(RenderComponent.class)
                    .setTextureRotation(angle + 180);
        } else {
            entity.getComponentOfType(RenderComponent.class)
                    .setMirrored(false);
            entity.getComponentOfType(RenderComponent.class)
                    .setTextureRotation(angle);
        }
    }


    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(GunComponent.class);
    }
}
