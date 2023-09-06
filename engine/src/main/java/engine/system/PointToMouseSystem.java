package engine.system;

import engine.component.base.RenderComponent;
import engine.component.base.TransformationComponent;
import engine.component.tag.PointToMouseTag;
import engine.entity.Entity;
import engine.handler.MouseHandler;
import org.joml.Vector2d;

public class PointToMouseSystem {

    public static void processEntity(Entity entity) {
        rotateSprite(entity);
    }

    private static void rotateSprite(Entity entity) {
        Vector2d mousePosWS = MouseHandler.getInstance().getMousePositionWorldSpace();
        Vector2d direction = mousePosWS.sub(entity.getComponentOfType(TransformationComponent.class).getPosition()).normalize();
        double angle = new Vector2d(1.0, 0.0).angle(direction) * (180 / 3.14159265359);
        if (angle > 90 || angle < -90) {
            entity.getComponentOfType(RenderComponent.class)
                    .setMirrored(true);
            entity.getComponentOfType(RenderComponent.class)
                    .setTextureRotation(angle + 180);
        } else {
            entity.getComponentOfType(RenderComponent.class)
                    .setMirrored(false);
            entity.getComponentOfType(RenderComponent.class)
                    .setTextureRotation(angle);
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(PointToMouseTag.class);
    }
}
