package engine.scene;

import engine.enumeration.PrimitiveMeshShape;
import engine.enumeration.ShaderType;
import engine.object.GameObject;
import engine.object.Hitbox;
import engine.object.enums.HitboxType;
import org.joml.Vector2i;

public class TileRoom implements Comparable<TileRoom> {

    private final short roomWidth;
    private final short roomHeight;
    private final Vector2i roomCenterTile;
    private final Vector2i roomBottomLeftTile;
    private final Tile[][] roomTiles;

    public TileRoom(short roomWidth, short roomHeight, Vector2i roomBottomLeftTile, String... textureKeys) {
        this.roomWidth = roomWidth;
        this.roomHeight = roomHeight;
        this.roomBottomLeftTile = roomBottomLeftTile;
        this.roomCenterTile = new Vector2i(roomBottomLeftTile.x() + (roomWidth / 2), roomBottomLeftTile.y() + (roomHeight / 2));
        this.roomTiles = new Tile[roomWidth][roomHeight];
        fillRoomTiles(roomWidth, roomHeight, roomBottomLeftTile, textureKeys);
    }

    private void fillRoomTiles(short roomWidth, short roomHeight, Vector2i roomTopLeftTile, String[] textureKeys) {
        for (short x = 0; x < roomWidth; x++) {
            for (short y = 0; y < roomHeight; y++) {
                short sceneTilePositionX = (short) (x + (short) roomTopLeftTile.x());
                short sceneTilePositionY = (short) (y + (short) roomTopLeftTile.y());
                GameObject object = new GameObject(PrimitiveMeshShape.QUAD,
                        ShaderType.DEFAULT,
                        new Hitbox(HitboxType.AABB, 0.5),
                        sceneTilePositionX,
                        sceneTilePositionY);
                int index = (int) (Math.random() * textureKeys.length);
                object.setTextureKey(textureKeys[index]);
                object.setSurface(true);
                this.roomTiles[x][y] = new Tile(object, sceneTilePositionX, sceneTilePositionY);
            }
        }
    }

    public Tile[][] getRoomTiles() {
        return roomTiles;
    }

    public short getRoomWidth() {
        return roomWidth;
    }

    public short getRoomHeight() {
        return roomHeight;
    }

    public Vector2i getRoomCenterTile() {
        return roomCenterTile;
    }

    public Vector2i getRoomBottomLeftTile() {
        return roomBottomLeftTile;
    }

    public short getRoomSize() {
        return (short) (this.roomHeight * this.roomWidth);
    }

    @Override
    public int compareTo(TileRoom that) {
        return Short.compare(this.getRoomSize(), that.getRoomSize());
    }
}
