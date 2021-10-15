package engine.object;

import engine.component.RenderComponent;
import engine.component.VisibleFaceTag;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.NavTileType;
import engine.handler.EntityHandler;
import engine.handler.RoomTemplateHandler;
import engine.handler.TextureHandler;
import engine.loader.template.RoomTemplate;
import org.apache.commons.lang3.RandomStringUtils;
import org.joml.Random;
import org.joml.Vector2i;

import java.util.Map;

public class TileMap {

    private static final String DUNGEON_ENTITY_PREFIX = "DUNGEON_ENTITY";

    private final int size;
    private final String[][] tiles;
    private final NavMap navMap;

    public TileMap(int size) {
        this.size = size;
        this.tiles = new String[size][size];
        this.navMap = new NavMap(size);
    }

    public void addRoom(Room room) {
        for (int x = 0; x < room.getRoomWidth(); x++) {
            for (int y = 0; y < room.getRoomHeight(); y++) {
                int tilePosX = x + room.getRoomBottomLeft().x();
                int tilePosY = y + room.getRoomBottomLeft().y();
                if (tiles[tilePosX][tilePosY] == null) {
                    this.tiles[tilePosX][tilePosY] = room.getTemplate();
                    this.navMap.addTile(NavTileType.FLOOR, new Vector2i(tilePosX, tilePosY), room.getTemplate());
                }
            }
        }
    }

    public void initMap(Random random) {
        EntityHandler.getInstance().removeObjectsWithPrefix(DUNGEON_ENTITY_PREFIX);
        generateFloor(random);
        generateWalls(random);
        generateRoomObjects(random);
        generateRoomHostiles(random);
    }

    private void generateFloor(Random random) {
        for (short x = 0; x < this.size; x++) {
            for (short y = 0; y < this.size; y++) {
                if (this.tiles[x][y] != null) {
                    generateEntityFromRoom(this.tiles[x][y], x, y, random);
                }
            }
        }
    }

    private void generateEntityFromRoom(String template, int x, int y, Random random) {
        RoomTemplate roomTemplate = RoomTemplateHandler.getInstance().getObject(template);
        Vector2i tileMapDimensions = TextureHandler.TEXTURE_HANDLER.getTileMapDimensions(roomTemplate.getFloorTextureKey());
        Entity entity = EntityBuilder.builder().fromTemplate("floor").at(x, y).build();
        entity.getComponentOfType(RenderComponent.class).setTextureKey(roomTemplate.getFloorTextureKey());
        entity.getComponentOfType(RenderComponent.class).setTextureOffSetX(Math.floor(Math.pow(random.nextFloat(), 3) * tileMapDimensions.x()));
        entity.getComponentOfType(RenderComponent.class).setTextureOffSetY(Math.floor(Math.pow(random.nextFloat(), 3) * tileMapDimensions.y()));
        EntityHandler.getInstance().addObject(DUNGEON_ENTITY_PREFIX + "_" + RandomStringUtils.randomAlphanumeric(8), entity);
    }

    private void generateWalls(Random random) {
        for (short x = 0; x < this.size; x++) {
            for (short y = 0; y < this.size; y++) {
                if (this.tiles[x][y] == null) {
                    if (isTile(x, y - 1)) {
                        RoomTemplate roomTemplate = RoomTemplateHandler.getInstance().getObject(this.tiles[x][y - 1]);
                        float rnd = random.nextFloat();
                        if (rnd < 0.25 && isTile(x, y + 1)) {
                            roomTemplate = RoomTemplateHandler.getInstance().getObject(this.tiles[x][y + 1]);
                            Vector2i tileMapDimensions = TextureHandler.TEXTURE_HANDLER.getTileMapDimensions(roomTemplate.getFloorTextureKey());
                            Entity entity = EntityBuilder.builder().fromTemplate("floor").at(x, y).build();
                            entity.getComponentOfType(RenderComponent.class).setTextureKey(roomTemplate.getFloorTextureKey());
                            entity.getComponentOfType(RenderComponent.class).setTextureOffSetX(Math.floor(Math.pow(random.nextFloat(), 3) * tileMapDimensions.x()));
                            entity.getComponentOfType(RenderComponent.class).setTextureOffSetY(Math.floor(Math.pow(random.nextFloat(), 3) * tileMapDimensions.y()));
                            EntityHandler.getInstance().addObject(DUNGEON_ENTITY_PREFIX + "_" + RandomStringUtils.randomAlphanumeric(8), entity);
                            EntityBuilder.builder()
                                    .fromTemplate("bars").at(x, y)
                                    .buildAndInstantiate(DUNGEON_ENTITY_PREFIX + "_" + RandomStringUtils.randomAlphanumeric(8));
                            this.navMap.addTile(NavTileType.OBSTRUCTED, new Vector2i(x, y), this.tiles[x][y - 1]);
                        } else {
                            Entity entity = EntityBuilder.builder().fromTemplate("wall")
                                    .at(x, y)
                                    .buildAndInstantiate(DUNGEON_ENTITY_PREFIX + "_" + RandomStringUtils.randomAlphanumeric(8));
                            entity.getComponentOfType(RenderComponent.class).setTextureKey(roomTemplate.getWallTextureKey());
                            this.navMap.addTile(NavTileType.WALL, new Vector2i(x, y), this.tiles[x][y - 1]);
                        }
                    } else if (isAdjacentToTile(x, y)) {
                        Entity entity = EntityBuilder.builder().fromTemplate("wall")
                                .at(x, y)
                                .buildAndInstantiate(DUNGEON_ENTITY_PREFIX + "_" + RandomStringUtils.randomAlphanumeric(8));
                        entity.removeComponent(RenderComponent.class);
                        entity.removeComponent(VisibleFaceTag.class);
                        this.navMap.addTile(NavTileType.WALL, new Vector2i(x, y), null);
                    }
                }
            }
        }
    }

    private void generateRoomObjects(Random random) {
        for (short x = 0; x < this.size; x++) {
            for (short y = 0; y < this.size; y++) {
                if (this.navMap.getTile(new Vector2i(x, y)) != null && this.navMap.getTile(new Vector2i(x, y)).getRoomTemplate() != null) {
                    RoomTemplate template = RoomTemplateHandler.getInstance().getObject(this.navMap.getTile(new Vector2i(x, y)).getRoomTemplate());
                    NavTileType tileType = this.navMap.getTile(new Vector2i(x, y)).getType();
                    float rnd = random.nextFloat();
                    float total = 0;
                    switch (tileType) {
                        case WALL:
                            if (template.getRoomWallEntityTemplates() == null) {
                                break;
                            }
                            for (Map.Entry<String, Double> entry : template.getRoomWallEntityTemplates().entrySet()) {
                                total += entry.getValue();
                                if (rnd <= total) {
                                    EntityBuilder.builder().fromTemplate(entry.getKey())
                                            .at(x, y)
                                            .buildAndInstantiate(DUNGEON_ENTITY_PREFIX + "_" + RandomStringUtils.randomAlphanumeric(8));
                                    break;
                                }
                            }

                        case FLOOR:
                            if (template.getRoomFloorEntityTemplates() == null) {
                                break;
                            }
                            for (Map.Entry<String, Double> entry : template.getRoomFloorEntityTemplates().entrySet()) {
                                total += entry.getValue();
                                if (rnd <= total) {
                                    EntityBuilder.builder().fromTemplate(entry.getKey())
                                            .at(x, y)
                                            .buildAndInstantiate(DUNGEON_ENTITY_PREFIX + "_" + RandomStringUtils.randomAlphanumeric(8));
                                    break;
                                }
                            }

                        case OBSTRUCTED:

                        default:
                    }
                }
            }
        }
    }

    private void generateRoomHostiles(Random random) {
        for (short x = 0; x < this.size; x++) {
            for (short y = 0; y < this.size; y++) {
                NavMap.NavTile tile = this.navMap.getTile(new Vector2i(x, y));
                if (tile != null && tile.getRoomTemplate() != null && tile.getType().equals(NavTileType.FLOOR)) {
                    RoomTemplate template = RoomTemplateHandler.getInstance().getObject(this.navMap.getTile(new Vector2i(x, y)).getRoomTemplate());
                    float rnd = random.nextFloat();
                    float total = 0;
                    if (template.getRoomHostileEntityTemplates() == null) {
                        continue;
                    }
                    for (Map.Entry<String, Double> entry : template.getRoomHostileEntityTemplates().entrySet()) {
                        total += entry.getValue();
                        if (rnd <= total) {
                            EntityBuilder.builder().fromTemplate(entry.getKey())
                                    .at(x, y)
                                    .buildAndInstantiate(DUNGEON_ENTITY_PREFIX + "_" + RandomStringUtils.randomAlphanumeric(8));
                            break;
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
