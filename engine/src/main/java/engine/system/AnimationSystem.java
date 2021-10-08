package engine.system;

import engine.component.AnimationComponent;
import engine.entity.Entity;

public class AnimationSystem {
    public static void processEntity(Entity entity) {
        AnimationComponent animationComponent = entity.getComponentOfType(AnimationComponent.class);
        if (((java.lang.System.nanoTime() / 1000000.0) - animationComponent.getAnimationUpdatedLast()) > animationComponent.getAnimationSpeed()) {
            if (animationComponent.isAnimationContinuous() || (!animationComponent.isAnimationFinished())) {
                animationComponent.setAnimationUpdatedLast(java.lang.System.nanoTime() / 1000000.0);
                animationComponent.setAnimationFrame(animationComponent.getAnimationFrame() + 1);
                if (animationComponent.getAnimationFrame() >= animationComponent.getAnimationLength()) {
                    animationComponent.setAnimationFinished(true);
                }
            }

        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(AnimationComponent.class);
    }
}
