package engine.object;

import engine.component.*;
import engine.component.tag.ShadowCastTag;
import engine.component.tag.ViewBlockingTag;
import engine.component.tag.VisibleFaceTag;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.*;
import engine.handler.EntityHandler;
import engine.handler.TextureHandler;
import org.apache.commons.lang3.RandomStringUtils;
import org.joml.Random;
import org.joml.Vector2i;
import org.joml.Vector3d;

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
        Vector2i tileMapDimensions = TextureHandler.TEXTURE_HANDLER.getTileMapDimensions(room.getTextureKey());
        Entity entity = EntityBuilder.builder()
                .withComponent(new RenderComponent(PrimitiveMeshShape.QUAD, room.getTextureKey(), ShaderType.DEFAULT, 1, 2))
                .withComponent(new TransformationComponent(x, y))
                .withComponent(new SurfaceComponent(new HitBox(HitBoxType.AABB, 1)))
                .build();
        entity.getComponentOfType(RenderComponent.class).setTextureOffSetX(Math.floor(Math.pow(Math.random(), 3) * tileMapDimensions.x()));
        entity.getComponentOfType(RenderComponent.class).setTextureOffSetY(Math.floor(Math.pow(Math.random(), 3) * tileMapDimensions.y()));
        EntityHandler.getInstance().addObject(DUNGEON_ENTITY_PREFIX + "_" + RandomStringUtils.randomAlphanumeric(8), entity);
    }

    private void generateWalls() {
        for (short x = 0; x < this.size; x++) {
            for (short y = 0; y < this.size; y++) {
                if (this.tiles[x][y] == null) {
                    if (isTile(x, y - 1)) {
                        Entity entity = EntityBuilder.builder()
                                .withComponent(new RenderComponent(PrimitiveMeshShape.QUAD, TextureKey.WALL_AQUA_BRICK, ShaderType.DEFAULT, 1, 3))
                                .withComponent(new TransformationComponent(x, y))
                                .withComponent(new CollisionComponent(new HitBox(HitBoxType.AABB, 0.5)))
                                .withTag(VisibleFaceTag.class)
                                .withTag(ShadowCastTag.class)
                                .withTag(ViewBlockingTag.class)
                                .build();
                        EntityHandler.getInstance().addObject(DUNGEON_ENTITY_PREFIX + "_" + RandomStringUtils.randomAlphanumeric(8), entity);
                        this.navMap.addTile(NavTileType.WALL, new Vector2i(x, y));
                    } else if (isAdjacentToTile(x, y)) {
                        Entity entity = EntityBuilder.builder()
                                .withComponent(new TransformationComponent(x, y))
                                .withComponent(new CollisionComponent(new HitBox(HitBoxType.AABB, 0.5)))
                                .withTag(ShadowCastTag.class)
                                .withTag(ViewBlockingTag.class)
                                .build();
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
                    if (random.nextFloat() < 0.01) {
                        Entity skeletonPile = EntityBuilder.builder()
                                .withComponent(new RenderComponent(PrimitiveMeshShape.QUAD, TextureKey.SKELETON_PILE, ShaderType.DEFAULT, 1, 3))
                                .withComponent(new TransformationComponent(x, y))
                                .withComponent(new CollisionComponent(new HitBox(HitBoxType.CIRCLE, 0.2), false))
                                .build();
                        skeletonPile.getComponentOfType(RenderComponent.class).setShadeless(true);
                        skeletonPile.getComponentOfType(CollisionComponent.class).setOnCollisionFunction((self, collider) -> {
                            self.addIfNotExistsComponent(new AnimationComponent(50, false, 7));
                            self.removeComponent(CollisionComponent.class);
                        });
                        EntityHandler.getInstance().addObject(DUNGEON_ENTITY_PREFIX + "_" + RandomStringUtils.randomAlphanumeric(8), skeletonPile);
                    }
                    if (this.tiles[x][y].getRoomPosition().equals(new Vector2i(x, y))) {
                        Entity lamp = EntityBuilder.builder()
                                .withComponent(new TransformationComponent(x, y))
                                .withComponent(new RenderComponent(PrimitiveMeshShape.QUAD, TextureKey.LANTERN_HANGING, ShaderType.DEFAULT, 1, 5))
                                .withComponent(new LightSourceComponent(new Vector3d(Math.random(), Math.random(), Math.random()), 1, 0.01))
                                .withComponent(new AnimationComponent(75))
                                .build();
                        lamp.getComponentOfType(RenderComponent.class).setShadeless(true);
                        EntityHandler.getInstance().addObject(DUNGEON_ENTITY_PREFIX + "_" + RandomStringUtils.randomAlphanumeric(8), lamp);
                        Entity monk = EntityBuilder.builder()
                                .withComponent(new RenderComponent(PrimitiveMeshShape.QUAD, TextureKey.ENEMY_MONK, ShaderType.DEFAULT, 1.5, 4))
                                .withComponent(new TransformationComponent(x, y))
                                .withComponent(new AnimationComponent(100))
                                //.withComponent(new CollisionComponent(new HitBox(HitBoxType.CIRCLE, 0.25)))
                                .withComponent(new PhysicsComponent())
                                .withComponent(new AIComponent())
                                .build();
                        monk.getComponentOfType(AnimationComponent.class).setMovementDriven(true);
                        monk.getComponentOfType(RenderComponent.class).setShadeless(true);
                        EntityHandler.getInstance().addObject(DUNGEON_ENTITY_PREFIX + "_" + RandomStringUtils.randomAlphanumeric(8), monk);
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
