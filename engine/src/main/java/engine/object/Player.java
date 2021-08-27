package engine.object;

import engine.enumeration.PrimitiveMeshShape;
import engine.enumeration.ShaderType;

public class Player extends Character {

    public Player(PrimitiveMeshShape primitiveMeshShape, ShaderType shaderKey) {
        super(primitiveMeshShape, shaderKey);
    }

    public void moveUp(double delta) {
        this.positionY += delta;
    }

    public void moveDown(double delta) {
        this.positionY -= delta;
    }

    public void moveLeft(double delta) {
        this.positionX -= delta;
    }

    public void moveRight(double delta) {
        this.positionX += delta;
    }

}
