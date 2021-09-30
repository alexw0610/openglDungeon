package engine.component;

import engine.enums.ShaderType;
import engine.enums.TextureKey;
import engine.object.Mesh;

public class RenderComponent extends Component {

    private final Mesh mesh;
    private final TextureKey textureKey;
    private final ShaderType shaderType;

    public RenderComponent(Mesh mesh, TextureKey textureKey, ShaderType shaderType) {
        super();
        this.mesh = mesh;
        this.textureKey = textureKey;
        this.shaderType = shaderType;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public TextureKey getTextureKey() {
        return textureKey;
    }

    public ShaderType getShaderType() {
        return shaderType;
    }

}
