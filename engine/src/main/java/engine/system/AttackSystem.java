package engine.system;

import engine.component.*;
import engine.entity.Entity;
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
        List<Entity> attackableEntities = EntityHandler.getInstance().getAllEntitiesWithComponents(StatComponent.class, CollisionComponent.class, attackComponent.getTargetComponentConstraint());
        if (attackComponent.isAoE()) {
            for (Entity attackableEntity : attackableEntities.stream().filter(e -> !e.getComponentOfType(StatComponent.class).isDead()).collect(Collectors.toList())) {
                if (CollisionUtil.checkCollision(transformationComponent.getPosition(),
                        new HitBox(HitBoxType.CIRCLE, attackComponent.getRange()),
                        attackableEntity.getComponentOfType(TransformationComponent.class).getPosition(),
                        attackableEntity.getComponentOfType(CollisionComponent.class).getHitBox())) {
                    StatComponent statComponent = attackableEntity.getComponentOfType(StatComponent.class);
                    statComponent.subtractHealthPoints(attackComponent.getDamage());
                    System.out.println("Combat Log: Hit " + attackableEntity.getEntityId() + " with " + attackComponent.getAttackName() + " for " + attackComponent.getDamage() + " health points!");
                    System.out.println("Combat Log: " + attackableEntity.getEntityId() + " now has " + statComponent.getCurrentHealthpoints() + " health points!");
                    if (attackComponent.getKnockback() > 0) {
                        PhysicsComponent physicsComponent = attackableEntity.getComponentOfType(PhysicsComponent.class);
                        Vector2d knockbackDirection = new Vector2d();
                        attackableEntity.getComponentOfType(TransformationComponent.class).getPosition().sub(transformationComponent.getPosition(), knockbackDirection);
                        attackableEntity.addComponent(new ColorShadeComponent(3, 1, 1, 500));
                        knockbackDirection.normalize();
                        physicsComponent.setMomentumX(physicsComponent.getMomentumX() + (knockbackDirection.x() * attackComponent.getKnockback()));
                        physicsComponent.setMomentumY(physicsComponent.getMomentumY() + (knockbackDirection.y() * attackComponent.getKnockback()));
                    }
                }
            }
        } else if (attackComponent.isSingleTarget()) {

        }
        EntityHandler.getInstance().removeObject(entity.getEntityId());
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(AttackComponent.class);
    }
}
