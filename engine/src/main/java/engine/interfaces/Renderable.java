package engine.interfaces;


import engine.enums.PrimitiveMeshShape;
import engine.enums.ShaderType;
import engine.enums.TextureKey;
import org.joml.Vector2d;

public interface Renderable extends Comparable<Renderable> {

    PrimitiveMeshShape getPrimitiveMeshShape();

    ShaderType getShaderType();

    TextureKey getTextureKey();

    Vector2d getPosition();

    double getScale();

    short getRenderLayer();

    double getTextureRotation();

    @Override
    int compareTo(Renderable other);
}
