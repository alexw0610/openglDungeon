package engine.scene;

import engine.handler.SceneHandler;
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

}
