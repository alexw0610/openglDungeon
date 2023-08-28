package engine.system.base;

import engine.EngineConstants;
import engine.component.base.AnimationComponent;
import engine.component.tag.ItemTag;
import engine.component.base.RenderComponent;
import engine.component.base.TransformationComponent;
import engine.component.internal.CreatedAtComponent;
import engine.entity.Entity;
import engine.service.RenderService;
import org.joml.Math;
import org.joml.Vector2d;

public class RenderSystem {

    public static void processEntity(Entity entity) {
        RenderComponent renderComponent = entity.getComponentOfType(RenderComponent.class);
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        if (entity.hasComponentOfType(AnimationComponent.class)) {
            AnimationComponent animationComponent = entity.getComponentOfType(AnimationComponent.class);
            renderComponent.setTextureOffSetX(animationComponent.getAnimationFrame());
            renderComponent.setTextureOffSetY(animationComponent.getAnimationRow());
        }
        Vector2d position = new Vector2d(transformationComponent.getPosition());
        if (entity.hasComponentOfType(ItemTag.class)) {
            position = new Vector2d(position.x(),
                    Math.lerp(position.y(),
                            position.y() + 0.1,
                            lerpFactor(entity.getComponentOfType(CreatedAtComponent.class).getEngineTick())));
        }
        RenderService.getInstance().renderComponent(renderComponent, position);
    }

    private static double lerpFactor(double offset) {
        return (Math.sin(offset + RenderService.renderTick * EngineConstants.STEP_TIME_FACTOR * EngineConstants.ITEM_HOVER_SPEED) * 0.5) + 0.5;
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(RenderComponent.class)
                && entity.hasComponentOfType(TransformationComponent.class);
    }
}
