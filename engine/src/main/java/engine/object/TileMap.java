package engine.object;

import engine.component.*;
import engine.entity.ComponentBuilder;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.NavTileType;
import engine.handler.*;
import engine.loader.template.DungeonTemplate;
import engine.loader.template.EntityInstanceTemplate;
import engine.loader.template.LootTableTemplate;
import engine.loader.template.RoomTemplate;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.joml.Random;
import org.joml.Vector2i;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class TileMap {

    private static final String DUNGEON_ENTITY_PREFIX = "DUNGEON_ENTITY_";
    private final int size;
    private final String[][] tiles;
    private final NavMap navMap;
    private final long seed;

    public TileMap(int size, long seed) {
        this.size = size;
        this.tiles = new String[size][size];
        this.navMap = new NavMap(size, seed);
        this.seed = seed;
    }

    public void initMap(Random random) {
        EntityHandler.getInstance().removeObjectsWithPrefix(DUNGEON_ENTITY_PREFIX);
        generateFloor(random);
        generateWalls(random);
        generateRoomObjects(random);
        generateRoomHostiles(random);
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

    public void setTile(int x, int y, String templateKey) {
        this.tiles[x][y] = templateKey;
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
        EntityHandler.getInstance().addObject(DUNGEON_ENTITY_PREFIX + RandomStringUtils.randomAlphanumeric(8), entity);
    }

    private void generateWalls(Random random) {
        for (short x = 0; x < this.size; x++) {
            for (short y = 0; y < this.size; y++) {
                if (this.tiles[x][y] == null) {
                    if (isTile(x, y - 1)) {
                        RoomTemplate roomTemplate = RoomTemplateHandler.getInstance().getObject(this.tiles[x][y - 1]);
                        Vector2i tileMapDimensions = TextureHandler.TEXTURE_HANDLER.getTileMapDimensions(roomTemplate.getWallTextureKey());
                        Entity entity = EntityBuilder.builder().fromTemplate("wall")
                                .at(x, y)
                                .buildAndInstantiate(DUNGEON_ENTITY_PREFIX + RandomStringUtils.randomAlphanumeric(8));
                        entity.getComponentOfType(RenderComponent.class).setTextureOffSetX(Math.floor(Math.pow(random.nextFloat(), 3) * tileMapDimensions.x()));
                        entity.getComponentOfType(RenderComponent.class).setTextureOffSetY(Math.floor(Math.pow(random.nextFloat(), 3) * tileMapDimensions.y()));
                        entity.getComponentOfType(RenderComponent.class).setTextureKey(roomTemplate.getWallTextureKey());
                        this.navMap.addTile(NavTileType.WALL, new Vector2i(x, y), this.tiles[x][y - 1]);
                    } else if (isAdjacentToTile(x, y)) {
                        Entity entity = EntityBuilder.builder().fromTemplate("wall")
                                .at(x, y)
                                .buildAndInstantiate(DUNGEON_ENTITY_PREFIX + RandomStringUtils.randomAlphanumeric(8));
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
                                    Entity entity = EntityBuilder.builder().fromTemplate(entry.getKey())
                                            .at(x, y)
                                            .buildAndInstantiate(DUNGEON_ENTITY_PREFIX + RandomStringUtils.randomAlphanumeric(8));
                                    if (entity.getComponentOfType(CollisionComponent.class) != null && entity.getComponentOfType(CollisionComponent.class).isObstructsMovement()) {
                                        this.navMap.getTile(new Vector2i(x, y)).setObstructed(true);
                                    }
                                    break;
                                }
                            }
                            break;
                        case FLOOR:
                            if (template.getRoomFloorEntityTemplates() == null) {
                                break;
                            }
                            for (Map.Entry<String, Double> entry : template.getRoomFloorEntityTemplates().entrySet()) {
                                total += entry.getValue();
                                if (rnd <= total) {
                                    Entity entity = EntityBuilder.builder().fromTemplate(entry.getKey())
                                            .at(x, y)
                                            .buildAndInstantiate(DUNGEON_ENTITY_PREFIX + RandomStringUtils.randomAlphanumeric(8));
                                    if (entity.getComponentOfType(CollisionComponent.class) != null && entity.getComponentOfType(CollisionComponent.class).isObstructsMovement()) {
                                        this.navMap.getTile(new Vector2i(x, y)).setObstructed(true);
                                    }
                                    break;
                                }
                            }
                            break;
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
                            Entity entity = EntityBuilder.builder().fromTemplate(entry.getKey())
                                    .at(x, y)
                                    .buildAndInstantiate(DUNGEON_ENTITY_PREFIX + RandomStringUtils.randomAlphanumeric(8));
                            InventoryComponent inventoryComponent = entity.getComponentOfType(InventoryComponent.class);
                            if (inventoryComponent != null && StringUtils.isNotBlank(inventoryComponent.getLootTable())) {
                                LootTableTemplate lootTable = LootTableTemplateHandler.getInstance().getObject(inventoryComponent.getLootTable());
                                for (int i = 0; i < inventoryComponent.getInventorySize(); i++) {
                                    float rndLoot = random.nextFloat();
                                    float totalLoot = 0;
                                    for (Map.Entry<String, Double> loot : lootTable.getLootMap().entrySet()) {
                                        totalLoot += loot.getValue();
                                        if (rndLoot <= totalLoot) {
                                            inventoryComponent.getItems().add((ItemComponent) ComponentBuilder.fromTemplate(loot.getKey()));
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    public void generateGlobalEntities(String dungeonTemplate, List<Room> mainRooms, Random random) {
        DungeonTemplate template = DungeonTemplateHandler.getInstance().getObject(dungeonTemplate);
        List<EntityInstanceTemplate> globalEntities = template.getGlobalEntities();
        if (!globalEntities.isEmpty()) {
            for (EntityInstanceTemplate instanceTemplate : globalEntities) {
                int instanceCount = 0;
                int delta = instanceTemplate.getMaxAmount() - instanceTemplate.getMinAmount();
                float rnd = random.nextFloat();
                int targetAmount = instanceTemplate.getMinAmount() + (int) Math.floor(delta * rnd);
                switch (instanceTemplate.getLocationConstraint()) {
                    case "FARTHEST":
                        Room farthestRoom = mainRooms.stream()
                                .max(Comparator.comparingInt(r -> r.getRoomPosition().x() + r.getRoomPosition().y()))
                                .orElse(mainRooms.get(mainRooms.size() - 1));
                        while (instanceCount < targetAmount) {
                            for (int x = farthestRoom.getRoomBottomLeft().x(); x < farthestRoom.getRoomBottomLeft().x() + farthestRoom.getRoomWidth(); x++) {
                                for (int y = farthestRoom.getRoomBottomLeft().y(); y < farthestRoom.getRoomBottomLeft().y() + farthestRoom.getRoomHeight(); y++) {
                                    if (!this.navMap.getTile(new Vector2i(x, y)).isObstructed()) {
                                        rnd = random.nextFloat();
                                        if (rnd > 0.85f && instanceCount < targetAmount) {
                                            instanceCount++;
                                            EntityBuilder.builder().fromTemplate(instanceTemplate.getTemplateName())
                                                    .at(x, y)
                                                    .buildAndInstantiate(DUNGEON_ENTITY_PREFIX + RandomStringUtils.randomAlphanumeric(8));
                                            this.navMap.getTile(new Vector2i(x, y)).setObstructed(true);
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    default:
                        while (instanceCount < targetAmount) {
                            for (int x = 0; x < this.size; x++) {
                                for (int y = 0; y < this.size; y++) {
                                    if (this.navMap.getTile(new Vector2i(x, y)) != null
                                            && !this.navMap.getTile(new Vector2i(x, y)).isObstructed()
                                            && this.navMap.getTile(new Vector2i(x, y)).getType().equals(NavTileType.FLOOR)) {
                                        rnd = random.nextFloat();
                                        if (rnd > 0.85f && instanceCount < targetAmount) {
                                            instanceCount++;
                                            Entity entity = EntityBuilder.builder().fromTemplate(instanceTemplate.getTemplateName())
                                                    .at(x, y)
                                                    .buildAndInstantiate(DUNGEON_ENTITY_PREFIX + RandomStringUtils.randomAlphanumeric(8));
                                            if (entity.getComponentOfType(CollisionComponent.class) != null && entity.getComponentOfType(CollisionComponent.class).isObstructsMovement()) {
                                                this.navMap.getTile(new Vector2i(x, y)).setObstructed(true);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        break;
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
        return this.navMap;
    }

    public int getSize() {
        return size;
    }
}
