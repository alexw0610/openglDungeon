package engine.object;


import engine.enumeration.PrimitiveMeshShape;
import engine.enumeration.ShaderType;
import org.joml.Vector2d;

public interface Renderable {

    PrimitiveMeshShape getPrimitiveMeshShape();
    ShaderType getShaderType();
    String getTextureKey();
    Vector2d getPosition();
    double getScale();

}
