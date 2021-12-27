package engine.object;

import engine.enums.NavTileType;
import org.joml.Vector2i;

public class NavMap {
    private final NavTile[][] navTiles;
    private final long seed;

    public NavMap(int size, long seed) {
        this.seed = seed;
        this.navTiles = new NavTile[size][size];
    }

    public void addTile(NavTileType type, Vector2i position, String room) {
        this.navTiles[position.x()][position.y()] = new NavTile(type, room);
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
        private final String roomTemplate;

        public NavTile(NavTileType type, String roomTemplate) {
            this.type = type;
            this.roomTemplate = roomTemplate;
        }

        public NavTileType getType() {
            return type;
        }

        public String getRoomTemplate() {
            return roomTemplate;
        }
    }

    public long getSeed() {
        return seed;
    }
}
