package engine.object;

import engine.enums.TextureKey;
import org.joml.Intersectiond;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class Room implements Comparable<Room> {

    private final double roomWidth;
    private final double roomHeight;
    private final TextureKey textureKey;
    private final Vector2i roomPosition;
    private final List<String> roomEntityTemplates;

    public Room(double roomWidth, double roomHeight, Vector2i roomPosition, TextureKey textureKey) {
        this.roomWidth = roomWidth;
        this.roomHeight = roomHeight;
        this.textureKey = textureKey;
        this.roomPosition = roomPosition;
        this.roomEntityTemplates = new ArrayList<>();
    }

    public double getWidth() {
        return this.roomWidth;
    }

    public double getHeight() {
        return this.roomHeight;
    }

    public Vector2i getRoomPosition() {
        return this.roomPosition;
    }

    public Vector2i getRoomBottomLeft() {
        return new Vector2i(this.roomPosition.x() - ((int) this.roomWidth / 2), this.roomPosition.y() - ((int) this.roomHeight / 2));
    }

    public TextureKey getTextureKey() {
        return this.textureKey;
    }

    public double getRoomSize() {
        return this.roomHeight * this.roomWidth;
    }

    @Override
    public int compareTo(Room that) {
        return Double.compare(this.getRoomSize(), that.getRoomSize());
    }

    public boolean intersectsRoom(Room other) {
        return Intersectiond.testAarAar(
                other.getRoomPosition().x() - (other.getWidth() / 2),
                other.getRoomPosition().y() - (other.getHeight() / 2),
                other.getRoomPosition().x() + (other.getWidth() / 2),
                other.getRoomPosition().y() + (other.getHeight() / 2),
                this.getRoomPosition().x() - (this.getWidth() / 2),
                this.getRoomPosition().y() - (this.getHeight() / 2),
                this.getRoomPosition().x() + (this.getWidth() / 2),
                this.getRoomPosition().y() + (this.getHeight() / 2));
    }

    public List<String> getRoomEntityTemplates() {
        return roomEntityTemplates;
    }

    public void addEntityTemplate(String template) {
        this.roomEntityTemplates.add(template);
    }
}
