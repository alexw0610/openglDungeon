package engine.scene;

import engine.object.GameObject;

public class Tile {

    private GameObject object;
    private short positionX;
    private short positionY;

    public Tile(GameObject object, short positionX, short positionY) {
        this.object = object;
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public GameObject getObject() {
        return object;
    }

    public void setObject(GameObject object) {
        this.object = object;
    }

    public short getPositionX() {
        return positionX;
    }

    public void setPositionX(short positionX) {
        this.positionX = positionX;
    }

    public short getPositionY() {
        return positionY;
    }

    public void setPositionY(short positionY) {
        this.positionY = positionY;
    }
}
