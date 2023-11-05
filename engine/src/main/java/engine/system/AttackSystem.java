package engine.system;

import engine.component.*;
import engine.component.base.CollisionComponent;
import engine.component.base.RenderComponent;
import engine.component.base.TransformationComponent;
import engine.component.internal.CreatedByComponent;
import engine.component.tag.PlayerTag;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.BulletModifier;
import engine.enums.HitBoxType;
import engine.handler.EntityHandler;
import engine.object.HitBox;
import engine.object.generation.World;
import engine.service.util.CollisionUtil;
import engine.system.util.DamageUtil;
import org.joml.Vector2d;

import java.util.List;
import java.util.stream.Collectors;

public class AttackSystem {

    public static void processEntity(Entity entity) {
        AttackComponent attackComponent = entity.getComponentOfType(AttackComponent.class);
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        List<Entity> attackableEntities;
        if (attackComponent.getTargetComponentConstraint() != null) {
            attackableEntities = EntityHandler.getInstance()
                    .getAllEntitiesWithComponents(StatComponent.class, CollisionComponent.class, attackComponent.getTargetComponentConstraint());
        } else {
            attackableEntities = EntityHandler.getInstance()
                    .getAllEntitiesWithComponents(StatComponent.class, CollisionComponent.class);
        }
        if (entity.hasComponentOfType(CreatedByComponent.class)) {
            attackableEntities = attackableEntities.stream()
                    .filter(attackableEntity -> !attackableEntity.getEntityId()
                            .equals(entity.getComponentOfType(CreatedByComponent.class).getCreatorEntity().getEntityId()))
                    .collect(Collectors.toList());
        }
        handleModifiers(attackComponent, transformationComponent);
        if (attackComponent.isAoE()) {
            handleAoeAttack(attackComponent, transformationComponent, attackableEntities);
        } else if (attackComponent.isSingleTarget()) {
            handleSingleTargetAttack(attackComponent, transformationComponent);
        }
        EntityHandler.getInstance().removeObject(entity.getEntityId());
    }

    private static void handleModifiers(AttackComponent attackComponent, TransformationComponent transformationComponent) {
        if (!attackComponent.getBulletModifierList().isEmpty()) {
            if (attackComponent.getBulletModifierList().contains(BulletModifier.INCENDIARY)) {
                DoTComponent dotFireComponent = new DoTComponent(3.0, 5.0, true, false, 0.0, "fire");
                dotFireComponent.setOriginModifier(BulletModifier.INCENDIARY);
                addComponentToWalkableTilesInArea(transformationComponent, dotFireComponent);
            }
            if (attackComponent.getBulletModifierList().contains(BulletModifier.CORROSIVE)) {
                DoTComponent dotCorrosiveComponent = new DoTComponent(3.0, 2.0, true, true, 0.25, "fire");
                dotCorrosiveComponent.setOriginModifier(BulletModifier.CORROSIVE);
                addComponentToWalkableTilesInArea(transformationComponent, dotCorrosiveComponent);
            }
            if (attackComponent.getBulletModifierList().contains(BulletModifier.EXPLOSIVE)) {
                attackComponent.setAoE(true);
                attackComponent.setRange(2.0);
            }
            if (attackComponent.getBulletModifierList().contains(BulletModifier.IMPACT)) {
                attackComponent.setAoE(true);
                attackComponent.setRange(2.0);
                attackComponent.setKnockback(100.0);
            }
        }
    }

    private static void addComponentToWalkableTilesInArea(TransformationComponent transformationComponent, DoTComponent component) {
        World world = EntityHandler.getInstance().getWorld();
        Vector2d center = transformationComponent.getPosition();
        int radius = 2;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                if (new Vector2d((int) center.x() + x, (int) center.y() + y)
                        .distance(center) < radius) {
                    if (world.isWalkable((int) center.x() + x, (int) center.y() + y)) {
                        world.getTile((int) center.x() + x, (int) center.y() + y)
                                .getEntity()
                                .addComponent(component);
                    }
                }
            }
        }
    }

    private static void handleSingleTargetAttack(AttackComponent attackComponent, TransformationComponent transformationComponent) {
        Entity attackableEntity = EntityHandler.getInstance().getObject(attackComponent.getTargetEntity());
        if (attackableEntity != null
                && !attackableEntity.hasComponentOfType(DashComponent.class)) {
            handleAttack(attackComponent, transformationComponent, attackableEntity);
        }
    }

    private static void handleAoeAttack(AttackComponent attackComponent, TransformationComponent transformationComponent, List<Entity> attackableEntities) {
        for (Entity attackableEntity : attackableEntities.stream().filter(e -> !e.getComponentOfType(StatComponent.class).isDead()).collect(Collectors.toList())) {
            if (!attackableEntity.hasComponentOfType(DashComponent.class)
                    && CollisionUtil.checkCollision(transformationComponent.getPosition(),
                    new HitBox(HitBoxType.CIRCLE, attackComponent.getRange()),
                    attackableEntity.getComponentOfType(TransformationComponent.class).getPosition(),
                    attackableEntity.getComponentOfType(CollisionComponent.class).getHitBox())) {
                handleAttack(attackComponent, transformationComponent, attackableEntity);
            }
        }
    }

    private static void handleAttack(AttackComponent attackComponent, TransformationComponent transformationComponent, Entity attackableEntity) {
        StatComponent statComponent = attackableEntity.getComponentOfType(StatComponent.class);
        TransformationComponent targetTransform = attackableEntity.getComponentOfType(TransformationComponent.class);
        createAttackSpriteAtTarget(attackComponent, targetTransform);
        if (statComponent.getCurrentShield() == 0.0) {
            applyKnockback(attackComponent, transformationComponent, attackableEntity);
            applyStun(attackComponent, attackableEntity);
        }
        if (attackComponent.getBulletModifierList().contains(BulletModifier.FREEZING)) {
            DoTComponent freezing = new DoTComponent(3.0, 0.0, false, true, 0.5, "freezing");
            freezing.setOriginModifier(BulletModifier.FREEZING);
            attackableEntity.addComponent(freezing);
        }
        if (attackComponent.getBulletModifierList().contains(BulletModifier.DEEP_FREEZE)
                && attackableEntity.hasComponentOfType(DoTComponent.class)
                && attackableEntity.getComponentOfType(DoTComponent.class)
                .getOriginModifier()
                .equals(BulletModifier.FREEZING)) {
            attackableEntity.addComponent(new StunComponent(3, "stun"));
        }
        double damage = DamageUtil.applyDamage(attackableEntity,
                attackComponent.getDamage(),
                attackComponent.getCriticalHitChance(),
                attackComponent.getCriticalBonusModifier(),
                attackComponent.getBulletModifierList().contains(BulletModifier.EXECUTION));
        if (attackComponent.getBulletModifierList().contains(BulletModifier.LIFESTEAL)) {
            StatComponent playerStatComponent = EntityHandler.getInstance().getEntityWithComponent(PlayerTag.class)
                    .getComponentOfType(StatComponent.class);
            playerStatComponent.setCurrentHealthPoints(playerStatComponent.getCurrentHealthPoints() + (damage * 0.02));
        }
    }

    private static void applyStun(AttackComponent attackComponent, Entity attackableEntity) {
        if (attackComponent.isStunsTarget() && attackComponent.getStunDuration() > 0.0) {
            attackableEntity.addComponent(new StunComponent(attackComponent.getStunDuration(), "stun"));
        }
    }

    private static void createAttackSpriteAtTarget(AttackComponent attackComponent, TransformationComponent targetTransform) {
        if (!attackComponent.getTexture().isEmpty()) {
            Entity attackSprite = EntityBuilder.builder()
                    .fromTemplate("attack")
                    .at(targetTransform.getPosition().x(), targetTransform.getPosition().y())
                    .build();
            attackSprite.getComponentOfType(RenderComponent.class).setTextureKey(attackComponent.getTexture());
            EntityHandler.getInstance().addObject(attackSprite);
        }
    }

    private static void applyKnockback(AttackComponent attackComponent, TransformationComponent transformationComponent, Entity attackableEntity) {
        if (attackComponent.getKnockback() > 0) {
            Vector2d knockbackDirection = new Vector2d();
            attackableEntity.getComponentOfType(TransformationComponent.class).getPosition().sub(transformationComponent.getPosition(), knockbackDirection);
            knockbackDirection.normalize();
            attackableEntity.addComponent(new KnockbackComponent(knockbackDirection, attackComponent.getKnockback()));
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(AttackComponent.class);
    }
}
