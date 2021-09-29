package engine.object.interfaces;


import engine.enumeration.PrimitiveMeshShape;
import engine.enumeration.ShaderType;
import org.joml.Vector2d;

public interface Renderable extends Comparable<Renderable> {

    PrimitiveMeshShape getPrimitiveMeshShape();

    ShaderType getShaderType();

    String getTextureKey();

    Vector2d getPosition();

    double getScale();

    short getRenderLayer();

    double getTextureRotation();

    @Override
    int compareTo(Renderable other);
}
