package engine.system;

import engine.component.DamageTextComponent;
import engine.component.base.TransformationComponent;
import engine.entity.Entity;
import engine.handler.UIHandler;
import engine.object.ui.UIText;
import engine.service.util.CoordinateConverter;
import org.apache.commons.lang3.RandomStringUtils;
import org.joml.Math;
import org.joml.Vector2d;

import static engine.EngineConstants.*;

public class DamageTextSystem {
    public static void processEntity(Entity entity) {
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        DamageTextComponent damageTextComponent = entity.getComponentOfType(DamageTextComponent.class);
        if (System.nanoTime() - damageTextComponent.getInitTime() > (1.2 * 1000000000)) {
            UIHandler.getInstance().removeTextObject(damageTextComponent.getDamageText().getKey());
            entity.removeComponent(DamageTextComponent.class);
        } else {
            if (damageTextComponent.getDamageText() == null) {
                createDamageText(transformationComponent, damageTextComponent);
            } else {
                UIHandler.getInstance().removeTextObject(damageTextComponent.getDamageText().getKey());
                createDamageText(transformationComponent, damageTextComponent);
            }
        }
    }

    private static void createDamageText(TransformationComponent transformationComponent, DamageTextComponent damageTextComponent) {
        Vector2d position = transformationComponent.getPosition();
        position.y = Math.lerp(position.y, position.y + 2.5, getYOffset(damageTextComponent));
        Vector2d uiPosition = CoordinateConverter.transformWorldSpaceToClipSpace(position);
        UIText damageText = new UIText(String.valueOf(damageTextComponent.getDamage()),
                uiPosition.x(),
                uiPosition.y(),
                0.5,
                0.5,
                damageTextComponent.isCriticalHit() ? 1.5 : 1.0);
        if (damageTextComponent.isPlayer()) {
            damageText.setColor(DAMAGE_TEXT_PLAYER_COLOR);
        } else if (damageTextComponent.isCriticalHit()) {
            damageText.setColor(DAMAGE_TEXT_CRIT_COLOR);
        } else {
            damageText.setColor(DAMAGE_TEXT_COLOR);
        }
        String key = "DT_" + RandomStringUtils.randomAlphanumeric(8);
        UIHandler.getInstance().addObject(key, damageText);
        damageText.setKey(key);
        damageTextComponent.setDamageText(damageText);
    }

    private static double getYOffset(DamageTextComponent damageTextComponent) {
        return Math.min((System.nanoTime() - damageTextComponent.getInitTime()) / (1.2 * 1000000000), 1.0);
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(DamageTextComponent.class);
    }
}
