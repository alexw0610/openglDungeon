package engine.scene;

import engine.component.RenderComponent;
import engine.component.SurfaceComponent;
import engine.component.TransformationComponent;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.HitBoxType;
import engine.enums.PrimitiveMeshShape;
import engine.enums.ShaderType;
import engine.enums.TextureKey;
import engine.object.HitBox;
import org.joml.Intersectiond;
import org.joml.Vector2i;

public class TileRoom implements Comparable<TileRoom> {

    private final short roomWidth;
    private final short roomHeight;
    private final Vector2i roomCenterTile;
    private final Vector2i roomBottomLeftTile;
    private final Tile[][] roomTiles;

    public TileRoom(short roomWidth, short roomHeight, Vector2i roomBottomLeftTile, TextureKey... textureKeys) {
        this.roomWidth = roomWidth;
        this.roomHeight = roomHeight;
        this.roomBottomLeftTile = roomBottomLeftTile;
        this.roomCenterTile = new Vector2i(roomBottomLeftTile.x() + (roomWidth / 2), roomBottomLeftTile.y() + (roomHeight / 2));
        this.roomTiles = new Tile[roomWidth][roomHeight];
        fillRoomTiles(roomWidth, roomHeight, roomBottomLeftTile, textureKeys);
    }

    private void fillRoomTiles(short roomWidth, short roomHeight, Vector2i roomTopLeftTile, TextureKey[] textureKeys) {
        for (short x = 0; x < roomWidth; x++) {
            for (short y = 0; y < roomHeight; y++) {
                short sceneTilePositionX = (short) (x + (short) roomTopLeftTile.x());
                short sceneTilePositionY = (short) (y + (short) roomTopLeftTile.y());
                int index = (int) (Math.random() * textureKeys.length);
                Entity entity = EntityBuilder.builder()
                        .withComponent(new RenderComponent(PrimitiveMeshShape.QUAD, textureKeys[index], ShaderType.DEFAULT, 1, 2))
                        .withComponent(new TransformationComponent(sceneTilePositionX, sceneTilePositionY))
                        .withComponent(new SurfaceComponent(new HitBox(HitBoxType.AABB, 1)))
                        .build();
                this.roomTiles[x][y] = new Tile(entity, sceneTilePositionX, sceneTilePositionY);
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

    public boolean intersectsRoom(TileRoom other) {
        return Intersectiond.testAarAar(
                other.getRoomBottomLeftTile().x(), other.getRoomBottomLeftTile().y(),
                other.getRoomBottomLeftTile().x() + other.getRoomWidth(),
                other.getRoomBottomLeftTile().y() + other.getRoomHeight(),
                this.getRoomBottomLeftTile().x(), this.getRoomBottomLeftTile().y(),
                this.getRoomBottomLeftTile().x() + this.getRoomWidth(),
                this.getRoomBottomLeftTile().y() + this.getRoomHeight());
    }
}
