package engine.object;

import engine.enums.NavTileType;
import org.joml.Vector2i;

public class NavMap {
    private final NavTile[][] navTiles;

    public NavMap(int size) {
        this.navTiles = new NavTile[size][size];
    }

    public void addTile(NavTileType type, Vector2i position) {
        this.navTiles[position.x()][position.y()] = new NavTile(type);
    }

    public NavTile getTile(Vector2i position) {
        if (position.x() > 0 && position.x() < this.navTiles.length
                && position.y() > 0 && position.y() < this.navTiles.length) {
            return this.navTiles[position.x()][position.y()];
        } else {
            return null;
        }
    }

    public static class NavTile {
        private final NavTileType type;

        public NavTile(NavTileType type) {
            this.type = type;
        }

        public NavTileType getType() {
            return type;
        }
    }
}
