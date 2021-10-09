package engine.system;

import engine.component.AnimationComponent;
import engine.component.PhysicsComponent;
import engine.entity.Entity;

public class AnimationSystem {
    public static void processEntity(Entity entity) {
        AnimationComponent animationComponent = entity.getComponentOfType(AnimationComponent.class);
        PhysicsComponent physicsComponent = entity.getComponentOfType(PhysicsComponent.class);
        if (((java.lang.System.nanoTime() / 1000000.0) - animationComponent.getAnimationUpdatedLast()) > animationComponent.getAnimationSpeed()) {
            if (animationComponent.isMovementDriven() && physicsComponent != null) {
                if (physicsComponent.getMomentumX() != 0 || physicsComponent.getMomentumY() != 0) {
                    animationComponent.setAnimationUpdatedLast(java.lang.System.nanoTime() / 1000000.0);
                    animationComponent.setAnimationFrame(animationComponent.getAnimationFrame() + 1);
                }
            } else if (animationComponent.isAnimationContinuous() || (!animationComponent.isAnimationFinished())) {
                animationComponent.setAnimationUpdatedLast(java.lang.System.nanoTime() / 1000000.0);
                animationComponent.setAnimationFrame(animationComponent.getAnimationFrame() + 1);
                if (!animationComponent.isAnimationContinuous() && animationComponent.getAnimationFrame() >= animationComponent.getAnimationLength()) {
                    animationComponent.setAnimationFinished(true);
                }
            }

        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(AnimationComponent.class);
    }
}
