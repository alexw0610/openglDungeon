package engine.scene;

import engine.enums.HitboxType;
import engine.enums.PrimitiveMeshShape;
import engine.enums.ShaderType;
import engine.enums.TextureKey;
import engine.handler.SceneHandler;
import engine.object.GameObject;
import engine.object.Hitbox;
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
                    SceneHandler.getInstance().addObject(RandomStringUtils.randomAlphanumeric(8), tiles[x][y].getObject());
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
                    if (
                        //isSurface(x + 1, y) ||
                        //isSurface(x - 1, y) ||
                        //isSurface(x, y + 1) ||
                            isSurface(x, y - 1)
                    ) {
                        GameObject object = new GameObject(PrimitiveMeshShape.QUAD,
                                ShaderType.DEFAULT,
                                new Hitbox(HitboxType.AABB, 0.5),
                                x,
                                y);
                        object.setTextureKey(TextureKey.STONE_CLEAN_SUNSET_WALL);
                        object.setObstacle(true);
                        object.setVisibleFace(true);
                        this.tiles[x][y] = new Tile(object, x, y);
                    } else if (isSurface(x - 1, y)) {
                        GameObject object = new GameObject(PrimitiveMeshShape.QUAD,
                                ShaderType.DEFAULT,
                                new Hitbox(HitboxType.AABB, 0.5),
                                x,
                                y);
                        object.setTextureKey(TextureKey.STONE_CLEAN_SUNSET_WALL_RIGHT);
                        object.setObstacle(true);
                        object.setVisibleFace(false);
                        this.tiles[x][y] = new Tile(object, x, y);
                    } else if (isSurface(x + 1, y)) {
                        GameObject object = new GameObject(PrimitiveMeshShape.QUAD,
                                ShaderType.DEFAULT,
                                new Hitbox(HitboxType.AABB, 0.5),
                                x,
                                y);
                        object.setTextureKey(TextureKey.STONE_CLEAN_SUNSET_WALL_RIGHT);
                        object.setTextureRotation(180);
                        object.setObstacle(true);
                        object.setVisibleFace(false);
                        this.tiles[x][y] = new Tile(object, x, y);
                    } else if (isSurface(x, y + 1)) {
                        GameObject object = new GameObject(PrimitiveMeshShape.QUAD,
                                ShaderType.DEFAULT,
                                new Hitbox(HitboxType.AABB, 0.5),
                                x,
                                y);
                        object.setTextureKey(TextureKey.STONE_CLEAN_SUNSET_WALL_RIGHT);
                        object.setTextureRotation(270);
                        object.setObstacle(true);
                        object.setVisibleFace(false);
                        this.tiles[x][y] = new Tile(object, x, y);
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
                this.tiles[x][y].getObject().isSurface();
    }

    public void setRoomPositions(List<Vector2i> roomPositions) {
        this.roomPositions = roomPositions;
    }

    public List<Vector2i> getRoomPositions() {
        return roomPositions;
    }
}
