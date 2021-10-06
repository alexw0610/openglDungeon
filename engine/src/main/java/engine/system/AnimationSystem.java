package engine.system;

import engine.component.AnimationComponent;
import engine.entity.Entity;
import engine.service.RenderService;

public class AnimationSystem {
    public static void processEntity(Entity entity) {
        AnimationComponent animationComponent = entity.getComponentOfType(AnimationComponent.class);
        animationComponent.setAnimationFrame(RenderService.renderTick / 1000 * animationComponent.getAnimationSpeed());
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(AnimationComponent.class);
    }
}
