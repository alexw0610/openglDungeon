package engine.object;

import org.joml.Intersectiond;
import org.joml.Vector2i;

public class Room implements Comparable<Room> {

    private final double roomWidth;
    private final double roomHeight;
    private final Vector2i roomPosition;
    private final String roomTemplate;

    public Room(double roomWidth, double roomHeight, Vector2i roomPosition, String roomTemplate) {
        this.roomWidth = roomWidth;
        this.roomHeight = roomHeight;
        this.roomPosition = roomPosition;
        this.roomTemplate = roomTemplate;
    }

    public Vector2i getRoomBottomLeft() {
        return new Vector2i(this.roomPosition.x() - ((int) this.roomWidth / 2), this.roomPosition.y() - ((int) this.roomHeight / 2));
    }

    public double getRoomSize() {
        return this.roomHeight * this.roomWidth;
    }

    public double getRoomWidth() {
        return roomWidth;
    }

    public double getRoomHeight() {
        return roomHeight;
    }

    public Vector2i getRoomPosition() {
        return roomPosition;
    }

    public String getTemplate() {
        return roomTemplate;
    }

    @Override
    public int compareTo(Room that) {
        return Double.compare(this.getRoomSize(), that.getRoomSize());
    }

    public boolean intersectsRoom(Room other) {
        return Intersectiond.testAarAar(
                other.getRoomPosition().x() - (other.getRoomWidth() / 2),
                other.getRoomPosition().y() - (other.getRoomHeight() / 2),
                other.getRoomPosition().x() + (other.getRoomWidth() / 2),
                other.getRoomPosition().y() + (other.getRoomHeight() / 2),
                this.getRoomPosition().x() - (this.getRoomWidth() / 2),
                this.getRoomPosition().y() - (this.getRoomHeight() / 2),
                this.getRoomPosition().x() + (this.getRoomWidth() / 2),
                this.getRoomPosition().y() + (this.getRoomHeight() / 2));
    }
}
