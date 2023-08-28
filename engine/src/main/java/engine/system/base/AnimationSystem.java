package engine.system.base;

import engine.component.KnockbackComponent;
import engine.component.base.AnimationComponent;
import engine.component.base.PhysicsComponent;
import engine.component.base.RenderComponent;
import engine.component.base.TransformationComponent;
import engine.entity.Entity;
import engine.handler.EntityHandler;
import org.joml.Vector2d;

public class AnimationSystem {
    public static void processEntity(Entity entity) {
        AnimationComponent animationComponent = entity.getComponentOfType(AnimationComponent.class);
        PhysicsComponent physicsComponent = entity.getComponentOfType(PhysicsComponent.class);
        RenderComponent renderComponent = entity.getComponentOfType(RenderComponent.class);
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        if (((java.lang.System.nanoTime() / 1000000.0) - animationComponent.getAnimationUpdatedLast()) > animationComponent.getAnimationSpeed()) {
            if (animationComponent.isMovementDriven() && physicsComponent != null) {
                if (entity.hasComponentOfType(KnockbackComponent.class)) {
                    animationComponent.setAnimationRow(3.0);
                } else {
                    animationComponent.setAnimationRow(0.0);
                }
                if ((Math.abs(physicsComponent.getMomentumX()) > 0.0
                        || Math.abs(physicsComponent.getMomentumY()) > 0.0)) {
                    animationComponent.setAnimationUpdatedLast(java.lang.System.nanoTime() / 1000000.0);
                    animationComponent.setAnimationFrame(animationComponent.getAnimationFrame() + 1);
                }
                renderComponent.setMirrored(physicsComponent.getMomentumX() < 0);

            } else if (animationComponent.isMovementDriven() && physicsComponent == null) {
                Vector2d deltaPosition = transformationComponent.getPosition().negate(transformationComponent.getLastPosition());
                if (deltaPosition.length() > 0.02) {
                    animationComponent.setAnimationRow(1.0);
                    animationComponent.setAnimationUpdatedLast(java.lang.System.nanoTime() / 1000000.0);
                    animationComponent.setAnimationFrame(animationComponent.getAnimationFrame() + 1);
                    renderComponent.setMirrored(deltaPosition.x() < 0);
                } else {
                    animationComponent.setAnimationUpdatedLast(java.lang.System.nanoTime() / 1000000.0);
                    animationComponent.setAnimationRow(0.0);
                    animationComponent.setAnimationFrame(0.0);
                    renderComponent.setMirrored(false);
                }
            } else if (animationComponent.isAnimationContinuous() || (!animationComponent.isAnimationFinished())) {
                animationComponent.setAnimationUpdatedLast(java.lang.System.nanoTime() / 1000000.0);
                animationComponent.setAnimationFrame(animationComponent.getAnimationFrame() + 1);
                if (!animationComponent.isAnimationContinuous()
                        && animationComponent.getAnimationFrame() >= animationComponent.getAnimationLength()) {
                    animationComponent.setAnimationFinished(true);
                    if (animationComponent.isDeleteAfterPlay()) {
                        EntityHandler.getInstance().removeObject(entity.getEntityId());
                    }
                }
            }

        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(AnimationComponent.class) && entity.hasComponentOfType(RenderComponent.class);
    }
}
