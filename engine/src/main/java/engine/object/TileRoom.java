package engine.object;

import engine.component.*;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.HitBoxType;
import engine.enums.PrimitiveMeshShape;
import engine.enums.ShaderType;
import engine.enums.TextureKey;
import engine.handler.TextureHandler;
import org.joml.Intersectiond;
import org.joml.Vector2i;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class TileRoom implements Comparable<TileRoom> {

    private final short roomWidth;
    private final short roomHeight;
    private final TextureKey textureKey;
    private final Vector2i roomCenterTile;
    private final Vector2i roomBottomLeftTile;
    private final Tile[][] roomTiles;
    private final List<Entity> roomEntities;

    public TileRoom(short roomWidth, short roomHeight, Vector2i roomBottomLeftTile, TextureKey textureKey) {
        this.roomWidth = roomWidth;
        this.roomHeight = roomHeight;
        this.textureKey = textureKey;
        this.roomBottomLeftTile = roomBottomLeftTile;
        this.roomCenterTile = new Vector2i(roomBottomLeftTile.x() + (roomWidth / 2), roomBottomLeftTile.y() + (roomHeight / 2));
        this.roomTiles = new Tile[roomWidth][roomHeight];
        this.roomEntities = new ArrayList<>();
        fillRoomTiles();
        generateRoomEntities();
    }

    private void fillRoomTiles() {
        for (short x = 0; x < this.roomWidth; x++) {
            for (short y = 0; y < this.roomHeight; y++) {
                short worldSpacePositionX = (short) (x + (short) this.roomBottomLeftTile.x());
                short worldSpacePositionY = (short) (y + (short) this.roomBottomLeftTile.y());
                Vector2i tileMapDimensions = TextureHandler.TEXTURE_HANDLER.getTileMapDimensions(this.textureKey);
                Entity entity = EntityBuilder.builder()
                        .withComponent(new RenderComponent(PrimitiveMeshShape.QUAD, this.textureKey, ShaderType.DEFAULT, 1, 2))
                        .withComponent(new TransformationComponent(worldSpacePositionX, worldSpacePositionY))
                        .withComponent(new SurfaceComponent(new HitBox(HitBoxType.AABB, 1)))
                        .build();
                entity.getComponentOfType(RenderComponent.class).setTextureOffSetX(Math.floor(Math.pow(Math.random(), 3) * tileMapDimensions.x()));
                entity.getComponentOfType(RenderComponent.class).setTextureOffSetY(Math.floor(Math.pow(Math.random(), 3) * tileMapDimensions.y()));
                this.roomTiles[x][y] = new Tile(entity, worldSpacePositionX, worldSpacePositionY);
            }
        }
    }

    private void generateRoomEntities() {
        Entity light = EntityBuilder.builder()
                .withComponent(new TransformationComponent(this.roomCenterTile.x(), this.roomCenterTile.y()))
                .withComponent(new RenderComponent(PrimitiveMeshShape.QUAD, TextureKey.LANTERN_HANGING, ShaderType.DEFAULT, 1, 5))
                .withComponent(new LightSourceComponent(new Vector3d(Math.random(), Math.random(), Math.random()), 1, 0.01))
                .withComponent(new AnimationComponent(100))
                .build();
        light.getComponentOfType(RenderComponent.class).setShadeless(true);
        this.roomEntities.add(light);
        for (short x = 0; x < this.roomWidth; x++) {
            for (short y = 0; y < this.roomHeight; y++) {
                short worldSpacePositionX = (short) (x + (short) this.roomBottomLeftTile.x());
                short worldSpacePositionY = (short) (y + (short) this.roomBottomLeftTile.y());
                if (this.roomTiles[x][y] != null) {
                    if (Math.random() < 0.005) {
                        Entity skeletonPile = EntityBuilder.builder()
                                .withComponent(new RenderComponent(PrimitiveMeshShape.QUAD, TextureKey.SKELETON_PILE, ShaderType.DEFAULT, 1, 3))
                                .withComponent(new TransformationComponent(worldSpacePositionX, worldSpacePositionY))
                                .withComponent(new CollisionComponent(new HitBox(HitBoxType.CIRCLE, 0.2), false))
                                .build();
                        skeletonPile.getComponentOfType(RenderComponent.class).setShadeless(true);
                        skeletonPile.getComponentOfType(CollisionComponent.class).setOnCollisionFunction((self, collider) -> {
                            self.addIfNotExistsComponent(new AnimationComponent(75, false, 7));
                            self.removeComponent(CollisionComponent.class);
                        });
                        this.roomEntities.add(skeletonPile);
                    }
                }
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

    public List<Entity> getRoomEntities() {
        return roomEntities;
    }
}
