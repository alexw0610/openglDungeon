package engine.system;

import engine.component.CameraComponent;
import engine.component.PlayerTag;
import engine.component.TransformationComponent;
import engine.component.ZoneChangeComponent;
import engine.entity.Entity;
import engine.handler.EntityHandler;
import engine.handler.template.ZoneTemplateHandler;
import engine.service.DungeonGenerator;
import engine.service.ZoneGenerator;
import org.apache.commons.lang3.RandomStringUtils;
import org.joml.Vector2d;

public class ZoneChangeSystem {
    public static void processEntity(Entity entity) {
        ZoneChangeComponent zoneChangeComponent = entity.getComponentOfType(ZoneChangeComponent.class);
        Entity camera = EntityHandler.getInstance().getEntityWithComponent(CameraComponent.class);
        Vector2d startPosition;
        if (ZoneTemplateHandler.getInstance().getObject(String.valueOf(zoneChangeComponent.getSeed())) != null) {
            startPosition = ZoneGenerator.generate(String.valueOf(zoneChangeComponent.getSeed()));
        } else if (zoneChangeComponent.getSeed() != 0L) {
            startPosition = DungeonGenerator.generate(zoneChangeComponent.getSeed(), "default_dungeon");
        } else {
            String seed = RandomStringUtils.randomNumeric(8);
            startPosition = DungeonGenerator.generate(Long.parseLong(seed), "default_dungeon");
        }
        entity.getComponentOfType(TransformationComponent.class).setPositionX(startPosition.x());
        entity.getComponentOfType(TransformationComponent.class).setPositionY(startPosition.y());
        camera.getComponentOfType(TransformationComponent.class).setPositionX(startPosition.x());
        camera.getComponentOfType(TransformationComponent.class).setPositionY(startPosition.y());
        entity.removeComponent(ZoneChangeComponent.class);
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(ZoneChangeComponent.class)
                && entity.hasComponentOfType(PlayerTag.class);
    }
}
