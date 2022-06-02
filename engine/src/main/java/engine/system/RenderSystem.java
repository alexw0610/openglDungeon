package engine.system;

import engine.component.AnimationComponent;
import engine.component.RenderComponent;
import engine.component.TransformationComponent;
import engine.entity.Entity;
import engine.service.RenderService;

public class RenderSystem {

    private static final RenderService renderService = RenderService.getInstance();

    public static void processEntity(Entity entity) {
        RenderComponent renderComponent = entity.getComponentOfType(RenderComponent.class);
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        if (entity.hasComponentOfType(AnimationComponent.class)) {
            AnimationComponent animationComponent = entity.getComponentOfType(AnimationComponent.class);
            renderComponent.setTextureOffSetX(animationComponent.getAnimationFrame());
            renderComponent.setTextureOffSetY(animationComponent.getAnimationRow());
        }
        renderService.renderComponent(renderComponent, transformationComponent.getPosition());
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(RenderComponent.class)
                && entity.hasComponentOfType(TransformationComponent.class);
    }
}
