package engine.system;

import engine.component.ColorShadeComponent;
import engine.component.RenderComponent;
import engine.entity.Entity;
import org.joml.Vector3d;

public class ColorShadeSystem {
    public static void processEntity(Entity entity) {
        ColorShadeComponent colorShadeComponent = entity.getComponentOfType(ColorShadeComponent.class);
        RenderComponent renderComponent = entity.getComponentOfType(RenderComponent.class);
        if (System.currentTimeMillis() < colorShadeComponent.getKeepAliveUntil()) {
            if (renderComponent.getColorOverride() == null) {
                renderComponent.setColorOverride(new Vector3d(colorShadeComponent.getRedMultiplier(), colorShadeComponent.getGreenMultiplier(), colorShadeComponent.getBlueMultiplier()));
            }
        } else {
            entity.removeComponent(ColorShadeComponent.class);
            renderComponent.setColorOverride(null);
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(ColorShadeComponent.class)
                && entity.hasComponentOfType(RenderComponent.class);
    }
}
