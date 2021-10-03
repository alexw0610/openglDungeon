package engine.scene;

import engine.component.CollisionComponent;
import engine.component.RenderComponent;
import engine.component.SurfaceComponent;
import engine.component.TransformationComponent;
import engine.component.tag.ShadowCastTag;
import engine.component.tag.VisibleFaceTag;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.HitBoxType;
import engine.enums.PrimitiveMeshShape;
import engine.enums.ShaderType;
import engine.enums.TextureKey;
import engine.handler.EntityHandler;
import engine.object.HitBox;
import org.apache.commons.lang3.RandomStringUtils;
import org.joml.Vector2i;

import java.util.List;

public class SceneTileMap {

    private final short mapSize;
    private final Tile[][] tiles;
    private List<Vector2i> roomPositions;

    public SceneTileMap(short mapSize) {
        this.mapSize = mapSize;
        this.tiles = new Tile[mapSize][mapSize];
    }

    public void setTile(Tile tile, short x, short y) {
        if (x < mapSize && y < mapSize) {
            this.tiles[x][y] = tile;
        }
    }

    public void loadTiles() {
        for (short x = 0; x < this.mapSize; x++) {
            for (short y = 0; y < this.mapSize; y++) {
                if (tiles[x][y] != null) {
                    EntityHandler.getInstance().addObject(RandomStringUtils.randomAlphanumeric(8), tiles[x][y].getEntity());
                }
            }
        }
    }

    public void applyTileRoom(TileRoom room) {
        for (short x = 0; x < room.getRoomWidth(); x++) {
            for (short y = 0; y < room.getRoomHeight(); y++) {
                int sceneTilePositionX = x + room.getRoomBottomLeftTile().x();
                int sceneTilePositionY = y + room.getRoomBottomLeftTile().y();
                if (tiles[sceneTilePositionX][sceneTilePositionY] == null) {
                    this.tiles[sceneTilePositionX][sceneTilePositionY] = room.getRoomTiles()[x][y];
                }
            }
        }
    }

    //TODO: Improve this method. Accept sets (themes) of room textures.
    public void applyWalls(String textureKey) {
        for (short x = 0; x < this.mapSize; x++) {
            for (short y = 0; y < this.mapSize; y++) {
                if (this.tiles[x][y] == null) {
                    if (isSurface(x, y - 1)) {
                        Entity entity = EntityBuilder.builder()
                                .withComponent(new RenderComponent(PrimitiveMeshShape.QUAD, TextureKey.WALL_AQUA_BRICK, ShaderType.DEFAULT, 1, 3))
                                .withComponent(new TransformationComponent(x, y))
                                .withComponent(new CollisionComponent(new HitBox(HitBoxType.AABB, 0.5)))
                                .withComponent(new VisibleFaceTag())
                                .withComponent(new ShadowCastTag())
                                .build();
                        this.tiles[x][y] = new Tile(entity, x, y);
                    } else if (isSurface(x - 1, y)) {
                        if (isSurface(x + 1, y)) {
                            Entity entity = EntityBuilder.builder()
                                    .withComponent(new RenderComponent(PrimitiveMeshShape.QUAD, TextureKey.WALL_AQUA_BRICK_LEFT_RIGHT, ShaderType.DEFAULT, 1, 3))
                                    .withComponent(new TransformationComponent(x, y))
                                    .withComponent(new CollisionComponent(new HitBox(HitBoxType.AABB, 0.5)))
                                    .withComponent(new ShadowCastTag())
                                    .build();
                            this.tiles[x][y] = new Tile(entity, x, y);
                        } else {
                            Entity entity = EntityBuilder.builder()
                                    .withComponent(new RenderComponent(PrimitiveMeshShape.QUAD, TextureKey.WALL_AQUA_BRICK_RIGHT, ShaderType.DEFAULT, 1, 3))
                                    .withComponent(new TransformationComponent(x, y))
                                    .withComponent(new CollisionComponent(new HitBox(HitBoxType.AABB, 0.5)))
                                    .withComponent(new ShadowCastTag())
                                    .build();
                            this.tiles[x][y] = new Tile(entity, x, y);
                        }
                    } else if (isSurface(x + 1, y)) {
                        if (isSurface(x - 1, y)) {
                            Entity entity = EntityBuilder.builder()
                                    .withComponent(new RenderComponent(PrimitiveMeshShape.QUAD, TextureKey.WALL_AQUA_BRICK_LEFT_RIGHT, ShaderType.DEFAULT, 1, 3))
                                    .withComponent(new TransformationComponent(x, y))
                                    .withComponent(new CollisionComponent(new HitBox(HitBoxType.AABB, 0.5)))
                                    .withComponent(new ShadowCastTag())
                                    .build();
                            this.tiles[x][y] = new Tile(entity, x, y);
                        } else {
                            Entity entity = EntityBuilder.builder()
                                    .withComponent(new RenderComponent(PrimitiveMeshShape.QUAD, TextureKey.WALL_AQUA_BRICK_LEFT, ShaderType.DEFAULT, 1, 3))
                                    .withComponent(new TransformationComponent(x, y))
                                    .withComponent(new CollisionComponent(new HitBox(HitBoxType.AABB, 0.5)))
                                    .withComponent(new ShadowCastTag())
                                    .build();
                            this.tiles[x][y] = new Tile(entity, x, y);
                        }
                    } else if (isSurface(x, y + 1)) {
                        Entity entity = EntityBuilder.builder()
                                .withComponent(new RenderComponent(PrimitiveMeshShape.QUAD, TextureKey.WALL_AQUA_BRICK_BOTTOM, ShaderType.DEFAULT, 1, 3))
                                .withComponent(new TransformationComponent(x, y))
                                .withComponent(new CollisionComponent(new HitBox(HitBoxType.AABB, 0.5)))
                                .withComponent(new ShadowCastTag())
                                .build();
                        this.tiles[x][y] = new Tile(entity, x, y);
                    }
                }
            }
        }
    }

    private boolean isSurface(int x, int y) {
        return x < this.mapSize &&
                x >= 0 &&
                y < this.mapSize &&
                y >= 0 &&
                this.tiles[x][y] != null &&
                this.tiles[x][y].getEntity().hasComponentOfType(SurfaceComponent.class);
    }

    public void setRoomPositions(List<Vector2i> roomPositions) {
        this.roomPositions = roomPositions;
    }

    public List<Vector2i> getRoomPositions() {
        return roomPositions;
    }
}
