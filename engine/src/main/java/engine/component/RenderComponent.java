package engine.component;

import engine.enums.PrimitiveMeshShape;
import engine.enums.ShaderType;
import engine.enums.TextureKey;

public class RenderComponent implements Component {

    private final String meshKey;
    private final TextureKey textureKey;
    private final ShaderType shaderType;
    private final double scale;
    private final int layer;
    private boolean shadeless;
    private boolean alwaysVisible;
    private double textureOffSetX;
    private double textureOffSetY;
    private double textureRotation;

    public RenderComponent(PrimitiveMeshShape primitive, TextureKey textureKey, ShaderType shaderType, double scale, int layer) {
        super();
        this.meshKey = primitive.getKey();
        this.textureKey = textureKey;
        this.shaderType = shaderType;
        this.scale = scale;
        this.layer = layer;
        this.shadeless = false;
        this.alwaysVisible = false;
        this.textureOffSetX = 0;
        this.textureOffSetY = 0;
        this.textureRotation = 0;
    }

    public String getMeshKey() {
        return meshKey;
    }

    public TextureKey getTextureKey() {
        return textureKey;
    }

    public ShaderType getShaderType() {
        return shaderType;
    }

    public double getScale() {
        return scale;
    }

    public int getLayer() {
        return layer;
    }

    public double getTextureOffSetX() {
        return textureOffSetX;
    }

    public void setTextureOffSetX(double textureOffSetX) {
        this.textureOffSetX = textureOffSetX;
    }

    public double getTextureOffSetY() {
        return textureOffSetY;
    }

    public void setTextureOffSetY(double textureOffSetY) {
        this.textureOffSetY = textureOffSetY;
    }

    public boolean isShadeless() {
        return shadeless;
    }

    public void setShadeless(boolean shadeless) {
        this.shadeless = shadeless;
    }

    public boolean isAlwaysVisible() {
        return alwaysVisible;
    }

    public void setAlwaysVisible(boolean alwaysVisible) {
        this.alwaysVisible = alwaysVisible;
    }

    public double getTextureRotation() {
        return textureRotation;
    }

    public void setTextureRotation(double textureRotation) {
        this.textureRotation = textureRotation;
    }
}
