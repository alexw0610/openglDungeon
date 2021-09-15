package engine.object;

import engine.enumeration.PrimitiveMeshShape;
import engine.enumeration.ShaderType;

public class Player extends Character {

    public Player(PrimitiveMeshShape primitiveMeshShape, ShaderType shaderKey) {
        super(primitiveMeshShape, shaderKey);
    }

    public void moveUp(double delta) {
        this.positionY += this.getCharacterStats().getMovementSpeed() * delta;
    }

    public void moveDown(double delta) {
        this.positionY -= this.getCharacterStats().getMovementSpeed() * delta;
    }

    public void moveLeft(double delta) {
        this.positionX -= this.getCharacterStats().getMovementSpeed() * delta;
    }

    public void moveRight(double delta) {
        this.positionX += this.getCharacterStats().getMovementSpeed() * delta;
    }

}
