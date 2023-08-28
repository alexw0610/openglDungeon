package engine.system;

import engine.Engine;
import engine.component.KnockbackComponent;
import engine.component.base.PhysicsComponent;
import engine.entity.Entity;

import static engine.EngineConstants.STEP_TIME_FACTOR;

public class KnockbackSystem {
    public static void processEntity(Entity entity) {
        KnockbackComponent knockbackComponent = entity.getComponentOfType(KnockbackComponent.class);
        PhysicsComponent physicsComponent = entity.getComponentOfType(PhysicsComponent.class);
        applyKnockbackToMomentum(knockbackComponent, physicsComponent);
        //if ((System.nanoTime() - knockbackComponent.getKnockbackStartTime()) > (0.5 * 1000000000)) {
        //    entity.removeComponent(KnockbackComponent.class);
        //}
        knockbackComponent.setKnockbackVelocity(knockbackComponent.getKnockbackVelocity() - (knockbackComponent.getKnockbackVelocity() * 0.15));
        if (knockbackComponent.getKnockbackVelocity() < 0.2) {
            entity.removeComponent(KnockbackComponent.class);
        }
    }

    private static void applyKnockbackToMomentum(KnockbackComponent knockbackComponent, PhysicsComponent physicsComponent) {
        double x = physicsComponent.getMomentumX();
        x += knockbackComponent.getKnockbackDirection().x() * knockbackComponent.getKnockbackVelocity() * (Engine.stepTimeDelta * STEP_TIME_FACTOR);
        physicsComponent.setMomentumX(x);
        double y = physicsComponent.getMomentumY();
        y += knockbackComponent.getKnockbackDirection().y() * knockbackComponent.getKnockbackVelocity() * (Engine.stepTimeDelta * STEP_TIME_FACTOR);
        physicsComponent.setMomentumY(y);
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(KnockbackComponent.class)
                && entity.hasComponentOfType(PhysicsComponent.class);
    }
}
