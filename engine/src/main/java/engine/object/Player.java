package engine.object;

import engine.enumeration.PrimitiveMeshShape;
import engine.enumeration.ShaderType;

public class Player extends GameObject {

    private String username;

    public Player(String username, PrimitiveMeshShape primitiveMeshShape, ShaderType shaderKey, double positionX, double positionY) {
        super(primitiveMeshShape,shaderKey,positionX,positionY);
        this.username = username;
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
