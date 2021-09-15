package engine.object;

public class CharacterStats {

    private short level = 0;
    private short health = 100;
    private short movementSpeed = 100;

    public CharacterStats() {
    }

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

    public short getHealth() {
        return health;
    }

    public void setHealth(short health) {
        this.health = health;
    }

    public short getMovementSpeed() {
        return movementSpeed;
    }

    public void setMovementSpeed(short movementSpeed) {
        this.movementSpeed = movementSpeed;
    }
}
