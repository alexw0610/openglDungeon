package engine.object;

import engine.entity.Entity;

public class Tile {

    private Entity entity;
    private short positionX;
    private short positionY;

    public Tile(Entity object, short positionX, short positionY) {
        this.entity = object;
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
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
