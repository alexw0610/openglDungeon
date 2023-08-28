package engine.system;

import engine.component.*;
import engine.component.base.AudioComponent;
import engine.component.base.CollisionComponent;
import engine.component.base.RenderComponent;
import engine.component.base.TransformationComponent;
import engine.component.internal.CreatedByComponent;
import engine.component.tag.PlayerTag;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.HitBoxType;
import engine.handler.EntityHandler;
import engine.object.HitBox;
import engine.service.util.CollisionUtil;
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
                    .filter(attackableEntity -> !
                            attackableEntity.getEntityId()
                                    .equals(entity.getComponentOfType(CreatedByComponent.class).getCreatorEntity().getEntityId()))
                    .collect(Collectors.toList());
        }
        if (attackComponent.isAoE()) {
            for (Entity attackableEntity : attackableEntities.stream().filter(e -> !e.getComponentOfType(StatComponent.class).isDead()).collect(Collectors.toList())) {
                if (!attackableEntity.hasComponentOfType(DashComponent.class)
                        && CollisionUtil.checkCollision(transformationComponent.getPosition(),
                        new HitBox(HitBoxType.CIRCLE, attackComponent.getRange()),
                        attackableEntity.getComponentOfType(TransformationComponent.class).getPosition(),
                        attackableEntity.getComponentOfType(CollisionComponent.class).getHitBox())) {
                    handleAttack(attackComponent, transformationComponent, attackableEntity);
                }
            }
        } else if (attackComponent.isSingleTarget()) {
            Entity attackableEntity = EntityHandler.getInstance().getObject(attackComponent.getTargetEntity());
            if (attackableEntity != null
                    && !attackableEntity.hasComponentOfType(DashComponent.class)) {
                handleAttack(attackComponent, transformationComponent, attackableEntity);
            }
        }
        EntityHandler.getInstance().removeObject(entity.getEntityId());
    }

    private static void handleAttack(AttackComponent attackComponent, TransformationComponent transformationComponent, Entity attackableEntity) {
        StatComponent statComponent = attackableEntity.getComponentOfType(StatComponent.class);
        handleDamage(attackComponent, attackableEntity, statComponent);
        TransformationComponent targetTransform = attackableEntity.getComponentOfType(TransformationComponent.class);
        createAttackSpriteAtTarget(attackComponent, targetTransform);
        applyKnockback(attackComponent, transformationComponent, attackableEntity);
        applyStun(attackComponent, attackableEntity);
    }

    private static void applyStun(AttackComponent attackComponent, Entity attackableEntity) {
        if (attackComponent.isStunsTarget()) {
            attackableEntity.addComponent(new StunComponent(2));
        }
    }

    private static void handleDamage(AttackComponent attackComponent, Entity attackableEntity, StatComponent statComponent) {
        if (statComponent != null) {
            DamageTextComponent damageTextComponent = new DamageTextComponent(attackComponent.getDamage());
            double damage = attackComponent.getDamage();
            if (Math.random() < attackComponent.getCriticalHitChance()) {
                damage = damage * (1.0 + Math.random());
                damageTextComponent.setCriticalHit(true);
            }
            if (statComponent.getMaxArmor() > 0) {
                double leftOverDamage = Math.max(damage - statComponent.getCurrentArmor(), 0.0);
                statComponent.subtractArmorPoints(damage);
                damage = leftOverDamage;
            }
            statComponent.subtractHealthPoints(damage);
            AudioComponent audioComponent = new AudioComponent();
            audioComponent.setAudioKey("hurt");
            audioComponent.setPlayOnce(true);
            attackableEntity.addComponent(audioComponent);
            if (attackableEntity.hasComponentOfType(PlayerTag.class)) {
                damageTextComponent.setPlayer(true);
            }
            EntityBuilder.builder()
                    .withComponent(damageTextComponent)
                    .withComponent(attackableEntity.getComponentOfType(TransformationComponent.class))
                    .buildAndInstantiate();
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
