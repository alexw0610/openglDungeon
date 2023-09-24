package engine.system;

import engine.EntityKeyConstants;
import engine.component.HealthBarComponent;
import engine.component.StatComponent;
import engine.component.base.TransformationComponent;
import engine.entity.Entity;
import engine.handler.UIHandler;
import engine.object.ui.UIElement;
import engine.service.util.CoordinateConverter;
import org.apache.commons.lang3.RandomStringUtils;
import org.joml.Vector2d;

public class HealthBarSystem {

    public static void processEntity(Entity entity) {
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        HealthBarComponent healthBarComponent = entity.getComponentOfType(HealthBarComponent.class);
        StatComponent statComponent = entity.getComponentOfType(StatComponent.class);
        Vector2d clipSpaceCoordinates = CoordinateConverter.transformWorldSpaceToClipSpace(transformationComponent.getPosition());
        if (healthBarComponent.getHealthBarElement() != null) {
            healthBarComponent.getHealthBarElement().setWidth(statComponent.getHealthPercentage() * 0.1);
            healthBarComponent.getHealthBarElement().setX(clipSpaceCoordinates.x() - 0.05);
            healthBarComponent.getHealthBarElement().setY(clipSpaceCoordinates.y() + 0.05);
        } else {
            UIElement uiElement = new UIElement(clipSpaceCoordinates.x(),
                    clipSpaceCoordinates.y(),
                    statComponent.getHealthPercentage() * 0.1,
                    0.05,
                    5,
                    "healthbar");
            uiElement.setAlwaysVisible(false);
            UIHandler.getInstance().addObject(EntityKeyConstants.HEALTH_BAR_PREFIX + RandomStringUtils.randomAlphanumeric(6), uiElement);
            healthBarComponent.setHealthBarElement(uiElement);
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(HealthBarComponent.class)
                && entity.hasComponentOfType(StatComponent.class);
    }
}
