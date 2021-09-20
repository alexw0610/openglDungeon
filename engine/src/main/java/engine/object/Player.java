package engine.object;

import engine.enumeration.PrimitiveMeshShape;
import engine.enumeration.ShaderType;

public class Player extends Character {

    private double momentumX;
    private double momentumY;

    public Player(PrimitiveMeshShape primitiveMeshShape, ShaderType shaderKey) {
        super(primitiveMeshShape, shaderKey);
    }

    public void moveY(double delta) {
        this.positionY += this.getCharacterStats().getMovementSpeed() * delta;
    }

    public void moveX(double delta) {
        this.positionX += this.getCharacterStats().getMovementSpeed() * delta;
    }

    public double getMomentumX() {
        return momentumX;
    }

    public void setMomentumX(double momentumX) {
        this.momentumX = momentumX;
    }

    public void addMomentumX(double delta) {
        this.momentumX += delta;
    }

    public double getMomentumY() {
        return momentumY;
    }

    public void setMomentumY(double momentumY) {
        this.momentumY = momentumY;
    }

    public void addMomentumY(double delta) {
        this.momentumY += delta;
    }
}
