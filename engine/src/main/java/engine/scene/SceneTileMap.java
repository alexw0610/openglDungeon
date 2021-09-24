package engine.scene;

import engine.enumeration.PrimitiveMeshShape;
import engine.enumeration.ShaderType;
import engine.handler.SceneHandler;
import engine.object.GameObject;
import engine.object.Hitbox;
import engine.object.enums.HitboxType;
import org.apache.commons.lang3.RandomStringUtils;

public class SceneTileMap {

    private final short mapSize;
    private final Tile[][] tiles;

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

    public void applyWalls(String textureKey) {
        for (short x = 0; x < this.mapSize; x++) {
            for (short y = 0; y < this.mapSize; y++) {
                if (this.tiles[x][y] == null) {
                    if (isSurface(x + 1, y) ||
                            isSurface(x - 1, y) ||
                            isSurface(x, y + 1) ||
                            isSurface(x, y - 1)) {
                        System.out.println("addedWalls");
                        GameObject object = new GameObject(PrimitiveMeshShape.QUAD,
                                ShaderType.DEFAULT,
                                new Hitbox(HitboxType.AABB, 0.5),
                                x,
                                y);
                        object.setTextureKey(textureKey);
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

}
