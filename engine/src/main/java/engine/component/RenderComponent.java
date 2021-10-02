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

    public RenderComponent(PrimitiveMeshShape primitive, TextureKey textureKey, ShaderType shaderType, double scale, int layer) {
        super();
        this.meshKey = primitive.getKey();
        this.textureKey = textureKey;
        this.shaderType = shaderType;
        this.scale = scale;
        this.layer = layer;
    }

    public RenderComponent(String meshKey, TextureKey textureKey, ShaderType shaderType, double scale, int layer) {
        super();
        this.meshKey = meshKey;
        this.textureKey = textureKey;
        this.shaderType = shaderType;
        this.scale = scale;
        this.layer = layer;
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
}
