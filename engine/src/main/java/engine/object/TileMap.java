package engine.object;

import engine.component.RenderComponent;
import engine.component.TransformationComponent;
import engine.component.VisibleFaceTag;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.entity.EntityTemplate;
import engine.enums.NavTileType;
import engine.enums.TextureKey;
import engine.handler.EntityHandler;
import engine.handler.EntityTemplateHandler;
import engine.handler.TextureHandler;
import org.apache.commons.lang3.RandomStringUtils;
import org.joml.Random;
import org.joml.Vector2i;

import java.util.List;

public class TileMap {

    private static final String DUNGEON_ENTITY_PREFIX = "DUNGEON_ENTITY";

    private final int size;
    private final Room[][] tiles;
    private final NavMap navMap;

    public TileMap(int size) {
        this.size = size;
        this.tiles = new Room[size][size];
        this.navMap = new NavMap(size);
    }

    public void addRoom(Room room) {
        for (int x = 0; x < room.getWidth(); x++) {
            for (int y = 0; y < room.getHeight(); y++) {
                int tilePosX = x + room.getRoomBottomLeft().x();
                int tilePosY = y + room.getRoomBottomLeft().y();
                if (tiles[tilePosX][tilePosY] == null) {
                    this.tiles[tilePosX][tilePosY] = room;
                    this.navMap.addTile(NavTileType.FLOOR, new Vector2i(tilePosX, tilePosY));
                }
            }
        }
    }

    public void initMap(Random random) {
        EntityHandler.getInstance().removeObjectsWithPrefix(DUNGEON_ENTITY_PREFIX);
        generateFloor();
        generateRoomObjects(random);
        generateWalls();
    }

    private void generateFloor() {
        for (short x = 0; x < this.size; x++) {
            for (short y = 0; y < this.size; y++) {
                if (this.tiles[x][y] != null) {
                    generateEntityFromRoom(this.tiles[x][y], x, y);
                }
            }
        }
    }

    private void generateEntityFromRoom(Room room, int x, int y) {
        Vector2i tileMapDimensions = TextureHandler.TEXTURE_HANDLER.getTileMapDimensions(room.getTextureKey().value());
        EntityTemplate entityTemplate = EntityTemplateHandler.getInstance().getObject("floor");
        Entity entity = EntityBuilder.builder().fromTemplate(entityTemplate).build();
        entity.getComponentOfType(TransformationComponent.class).setPositionX(x);
        entity.getComponentOfType(TransformationComponent.class).setPositionY(y);
        entity.getComponentOfType(RenderComponent.class).setTextureKey(room.getTextureKey().value());
        entity.getComponentOfType(RenderComponent.class).setTextureOffSetX(Math.floor(Math.pow(Math.random(), 3) * tileMapDimensions.x()));
        entity.getComponentOfType(RenderComponent.class).setTextureOffSetY(Math.floor(Math.pow(Math.random(), 3) * tileMapDimensions.y()));
        EntityHandler.getInstance().addObject(DUNGEON_ENTITY_PREFIX + "_" + RandomStringUtils.randomAlphanumeric(8), entity);
    }

    private void generateWalls() {
        for (short x = 0; x < this.size; x++) {
            for (short y = 0; y < this.size; y++) {
                if (this.tiles[x][y] == null) {
                    if (isTile(x, y - 1)) {
                        EntityTemplate template = EntityTemplateHandler.getInstance().getObject("wall");
                        Entity entity = EntityBuilder.builder().fromTemplate(template).build();
                        entity.getComponentOfType(TransformationComponent.class).setPositionX(x);
                        entity.getComponentOfType(TransformationComponent.class).setPositionY(y);
                        entity.getComponentOfType(RenderComponent.class).setTextureKey(TextureKey.WALL_AQUA_BRICK.value());
                        EntityHandler.getInstance().addObject(DUNGEON_ENTITY_PREFIX + "_" + RandomStringUtils.randomAlphanumeric(8), entity);
                        this.navMap.addTile(NavTileType.WALL, new Vector2i(x, y));
                    } else if (isAdjacentToTile(x, y)) {
                        EntityTemplate template = EntityTemplateHandler.getInstance().getObject("wall");
                        Entity entity = EntityBuilder.builder().fromTemplate(template).build();
                        entity.getComponentOfType(TransformationComponent.class).setPositionX(x);
                        entity.getComponentOfType(TransformationComponent.class).setPositionY(y);
                        entity.removeComponent(RenderComponent.class);
                        entity.removeComponent(VisibleFaceTag.class);
                        EntityHandler.getInstance().addObject(DUNGEON_ENTITY_PREFIX + "_" + RandomStringUtils.randomAlphanumeric(8), entity);
                        this.navMap.addTile(NavTileType.WALL, new Vector2i(x, y));
                    }
                }
            }
        }
    }

    private void generateRoomObjects(Random random) {
        for (short x = 0; x < this.size; x++) {
            for (short y = 0; y < this.size; y++) {
                if (this.tiles[x][y] != null) {
                    List<String> templates = this.tiles[x][y].getRoomEntityTemplates();
                    if (!templates.isEmpty()) {
                        if (random.nextFloat() < 0.05) {
                            int index = (int) Math.floor(random.nextFloat() * templates.size());
                            EntityTemplate entityTemplate = EntityTemplateHandler.getInstance().getObject(templates.get(index));
                            Entity entity = EntityBuilder.builder().fromTemplate(entityTemplate).build();
                            entity.getComponentOfType(TransformationComponent.class).setPositionX(x);
                            entity.getComponentOfType(TransformationComponent.class).setPositionY(y);
                            EntityHandler.getInstance().addObject(DUNGEON_ENTITY_PREFIX + "_" + RandomStringUtils.randomAlphanumeric(8), entity);
                        }
                    }
                }
            }
        }
    }

    private boolean isAdjacentToTile(int x, int y) {
        return isTile(x - 1, y - 1) || isTile(x, y - 1) || isTile(x + 1, y - 1) ||
                isTile(x - 1, y) || isTile(x, y) || isTile(x + 1, y) ||
                isTile(x - 1, y + 1) || isTile(x, y + 1) || isTile(x + 1, y + 1);
    }

    private boolean isTile(int x, int y) {
        if (x < 0 || x >= size || y < 0 || y >= size) {
            return false;
        }
        return this.tiles[x][y] != null;
    }

    public NavMap getNavMap() {
        return navMap;
    }
}
