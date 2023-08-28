package engine.object.generation;

import engine.component.base.CollisionComponent;
import engine.component.base.RenderComponent;
import engine.component.tag.ShadowCastTag;
import engine.component.tag.ViewBlockingTag;
import engine.entity.Entity;
import engine.enums.WorldTileType;
import engine.service.util.WorldGenerationUtil;

public class World {

    public int worldSizeX;
    public int worldSizeY;
    private final WorldTile[][] tiles;

    public World(int sizeX, int sizeY) {
        this.worldSizeX = sizeX;
        this.worldSizeY = sizeY;
        this.tiles = new WorldTile[sizeX][sizeY];
    }

    public void addTile(int x, int y, WorldTileType worldTileType) {
        this.tiles[x][y] = new WorldTile(worldTileType);
    }

    public WorldTile getTile(int x, int y) {
        if (isInBounds(x, y)) {
            return this.tiles[x][y];
        } else {
            return null;
        }
    }

    public WorldTileType getTileType(int x, int y) {
        if (isInBounds(x, y)) {
            return this.tiles[x][y].getWorldTileType();
        } else {
            return WorldTileType.OUT_OF_BOUNDS;
        }
    }

    public boolean isWalkable(int x, int y) {
        if (isInBounds(x, y)) {
            return getTile(x, y).getWorldTileType().equals(WorldTileType.GROUND);
        }
        return false;
    }

    private boolean isInBounds(int x, int y) {
        return x >= 0 && x < this.worldSizeX && y >= 0 && y < this.worldSizeY;
    }

    public void updateNeighboursTileOrientation(int x, int y) {
        for (int xN = x - 1; xN <= x + 1; xN++) {
            for (int yN = x - 1; yN <= y + 1; yN++) {
                if (isInBounds(xN, yN) && getTile(xN, yN).getWorldTileType().equals(WorldTileType.ROCK)) {
                    WorldTile worldTile = getTile(xN, yN);
                    double newOffset = WorldGenerationUtil.getTextureOffsetForOrientation(this, xN, yN);
                    worldTile.getEntity().getComponentOfType(RenderComponent.class).setTextureOffSetX(newOffset);
                    if (newOffset == 11 && worldTile.getEntity().hasComponentOfType(ViewBlockingTag.class)) {
                        worldTile.getEntity().removeComponent(ViewBlockingTag.class);
                        worldTile.getEntity().removeComponent(ShadowCastTag.class);
                        worldTile.getEntity().removeComponent(CollisionComponent.class);
                    } else if (newOffset != 11 && !worldTile.getEntity().hasComponentOfType(ViewBlockingTag.class)) {
                        worldTile.getEntity().addComponent(new ViewBlockingTag());
                        worldTile.getEntity().addComponent(new ShadowCastTag());
                        worldTile.getEntity().addComponent(new CollisionComponent("AABB", 0.5));
                    }
                }
            }
        }
    }

    public class WorldTile {
        private WorldTileType worldTileType;
        private Entity entity;

        public WorldTile(WorldTileType worldTileType) {
            this.worldTileType = worldTileType;
        }

        public WorldTileType getWorldTileType() {
            return worldTileType;
        }

        public void setWorldTileType(WorldTileType worldTileType) {
            this.worldTileType = worldTileType;
        }

        public Entity getEntity() {
            return entity;
        }

        public void setEntity(Entity entity) {
            this.entity = entity;
        }
    }
}
