package engine.system;

import engine.component.CameraComponent;
import engine.component.PlayerComponent;
import engine.component.TransformationComponent;
import engine.component.ZoneChangeComponent;
import engine.entity.Entity;
import engine.handler.EntityHandler;
import engine.object.Room;
import engine.service.DungeonGenerator;
import org.apache.commons.lang3.RandomStringUtils;
import org.joml.Vector2i;

import java.util.Comparator;
import java.util.List;

public class ZoneChangeSystem {
    public static void processEntity(Entity entity) {
        ZoneChangeComponent zoneChangeComponent = entity.getComponentOfType(ZoneChangeComponent.class);
        Entity camera = EntityHandler.getInstance().getEntityWithComponent(CameraComponent.class);
        if (zoneChangeComponent.getSeed() != 0L) {
            List<Room> dungeonRooms = DungeonGenerator.generate(zoneChangeComponent.getSeed(), "default_dungeon");
            Room startRoom = dungeonRooms.stream()
                    .sorted(Comparator.comparingInt(r -> r.getRoomPosition().x()))
                    .sorted(Comparator.comparingInt(r -> r.getRoomPosition().y()))
                    .findFirst().orElse(dungeonRooms.get(0));
            Vector2i startPosition = startRoom.getRoomPosition();
            entity.getComponentOfType(TransformationComponent.class).setPositionX(startPosition.x());
            entity.getComponentOfType(TransformationComponent.class).setPositionY(startPosition.y());
            camera.getComponentOfType(TransformationComponent.class).setPositionX(startPosition.x());
            camera.getComponentOfType(TransformationComponent.class).setPositionY(startPosition.y());
        } else {
            String seed = RandomStringUtils.randomNumeric(8);
            List<Room> dungeonRooms = DungeonGenerator.generate(Long.parseLong(seed), "default_dungeon");
            Room startRoom = dungeonRooms.stream()
                    .sorted(Comparator.comparingInt(r -> r.getRoomPosition().x()))
                    .sorted(Comparator.comparingInt(r -> r.getRoomPosition().y()))
                    .findFirst().orElse(dungeonRooms.get(0));
            Vector2i startPosition = startRoom.getRoomPosition();
            entity.getComponentOfType(TransformationComponent.class).setPositionX(startPosition.x());
            entity.getComponentOfType(TransformationComponent.class).setPositionY(startPosition.y());
            camera.getComponentOfType(TransformationComponent.class).setPositionX(startPosition.x());
            camera.getComponentOfType(TransformationComponent.class).setPositionY(startPosition.y());
        }
        entity.removeComponent(ZoneChangeComponent.class);
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(ZoneChangeComponent.class)
                && entity.hasComponentOfType(PlayerComponent.class);
    }
}
