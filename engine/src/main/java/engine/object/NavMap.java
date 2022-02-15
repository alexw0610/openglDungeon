package engine.object;

import engine.component.RenderComponent;
import engine.entity.EntityBuilder;
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

    public boolean isWalkable(Vector2i position) {
        return position.x() > 0 && position.x() < this.navTiles.length
                && position.y() > 0 && position.y() < this.navTiles.length
                && this.navTiles[position.x()][position.y()] != null
                && this.navTiles[position.x()][position.y()].getType().equals(NavTileType.FLOOR)
                && !this.navTiles[position.x()][position.y()].isObstructed();
    }

    public void instantiateDebugMeshes() {
        for (int x = 0; x < navTiles.length; x++) {
            for (int y = 0; y < navTiles.length; y++) {
                if (navTiles[x][y] != null && navTiles[x][y].getType().equals(NavTileType.FLOOR)) {
                    RenderComponent renderComponent = new RenderComponent("QUAD", "default", "shader", 0.9, 9);
                    renderComponent.setShadeless(true);
                    renderComponent.setAlwaysVisible(true);
                    EntityBuilder.builder()
                            .at(x, y)
                            .withComponent(renderComponent)
                            .buildAndInstantiate();
                }
            }
        }
    }

    public static class NavTile {
        private final NavTileType type;
        private boolean isObstructed;
        private final String roomTemplate;

        public NavTile(NavTileType type, String roomTemplate) {
            this.type = type;
            this.roomTemplate = roomTemplate;
        }

        public boolean isObstructed() {
            return isObstructed;
        }

        public void setObstructed(boolean obstructed) {
            isObstructed = obstructed;
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
