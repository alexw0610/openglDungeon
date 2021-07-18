package engine.object;

import engine.enumeration.PrimitiveMeshShape;
import engine.enumeration.ShaderType;
import org.joml.Vector2d;

public class GameObject implements Renderable {

    private PrimitiveMeshShape primitiveMeshShape;
    private ShaderType shaderType;
    private String textureKey;
    protected double positionX;
    protected double positionY;
    protected double scale;

    public GameObject() {
        this.primitiveMeshShape = PrimitiveMeshShape.QUAD;
        this.shaderType = ShaderType.DEFAULT;
        this.positionX = 0;
        this.positionY = 0;
        this.scale = 1;
    }

    public GameObject(PrimitiveMeshShape primitiveMeshShape) {
        this.primitiveMeshShape = primitiveMeshShape;
        this.shaderType = ShaderType.DEFAULT;
        this.positionX = 0;
        this.positionY = 0;
        this.scale = 1;
    }

    public GameObject(PrimitiveMeshShape primitiveMeshShape, ShaderType shaderType) {
        this.primitiveMeshShape = primitiveMeshShape;
        this.shaderType = shaderType;
        this.positionX = 0;
        this.positionY = 0;
        this.scale = 1;
    }

    public GameObject(PrimitiveMeshShape primitiveMeshShape, ShaderType shaderType, double positionX, double positionY) {
        this.primitiveMeshShape = primitiveMeshShape;
        this.shaderType = shaderType;
        this.positionX = positionX;
        this.positionY = positionY;
        this.scale = 1;
    }

    @Override
    public PrimitiveMeshShape getPrimitiveMeshShape() {
        return this.primitiveMeshShape;
    }

    public void setShaderType(ShaderType shaderType) {
        this.shaderType = shaderType;
    }

    @Override
    public ShaderType getShaderType() {
        return this.shaderType;
    }

    @Override
    public String getTextureKey() {
        return this.textureKey;
    }

    public void setTextureKey(String textureKey){
        this.textureKey = textureKey;
    }

    @Override
    public double getScale() {
        return this.scale;
    }

    public void setScale(double scale){
        this.scale = scale;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }

    @Override
    public Vector2d getPosition() {
        return new Vector2d(this.positionX, this.positionY);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
