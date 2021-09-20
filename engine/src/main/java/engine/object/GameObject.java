package engine.object;

import engine.enumeration.PrimitiveMeshShape;
import engine.enumeration.ShaderType;
import engine.object.interfaces.Collidable;
import engine.object.interfaces.Renderable;
import org.joml.Vector2d;

public class GameObject implements Renderable, Collidable {

    private final PrimitiveMeshShape primitiveMeshShape;
    private ShaderType shaderType;
    private String textureKey;
    private Hitbox hitbox;
    protected double positionX;
    protected double positionY;
    protected double scale;
    protected short renderLayer;
    private boolean collision;

    public GameObject() {
        this(PrimitiveMeshShape.QUAD);
    }

    public GameObject(PrimitiveMeshShape primitiveMeshShape) {
        this(primitiveMeshShape, ShaderType.DEFAULT);
    }

    public GameObject(PrimitiveMeshShape primitiveMeshShape, ShaderType shaderType) {
        this(primitiveMeshShape, shaderType, 0, 0);
    }

    public GameObject(PrimitiveMeshShape primitiveMeshShape, ShaderType shaderType, double positionX, double positionY) {
        this.primitiveMeshShape = primitiveMeshShape;
        this.shaderType = shaderType;
        this.positionX = positionX;
        this.positionY = positionY;
        this.scale = 1;
        this.renderLayer = 0;
        this.collision = false;
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

    public void setTextureKey(String textureKey) {
        this.textureKey = textureKey;
    }

    @Override
    public double getScale() {
        return this.scale;
    }

    @Override
    public short getRenderLayer() {
        return this.renderLayer;
    }

    public void setScale(double scale) {
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

    public void setRenderLayer(short renderLayer) {
        this.renderLayer = renderLayer;
    }

    @Override
    public int compareTo(Renderable other) {
        return Short.compare(this.renderLayer, other.getRenderLayer());
    }

    @Override
    public boolean isCollidable() {
        return this.collision;
    }

    public void setCollidable(boolean collision) {
        this.collision = collision;
    }

    @Override
    public Hitbox getHitbox() {
        return null;
    }
}
