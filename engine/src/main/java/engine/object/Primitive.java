package engine.object;

public abstract class Primitive {

    public static final float[] TRIANGLE_VERTICES = {
            0.0f, 0.5f, -1.0f,
            0.5f, -0.5f, -1.0f,
            -0.5f, -0.5f, -1.0f
    };
    public static final int[] TRIANGLE_INDICES = {
            0, 1, 2
    };
    public static final float[] TRIANGLE_TEXTURE = {
            0.0f, 0.5f, -1.0f,
            0.5f, -0.5f, -1.0f,
            -0.5f, -0.5f, -1.0f
    };

    public static final float[] QUAD_VERTICES = {
            -0.5f, 0.5f, -1.0f,
            0.5f, 0.5f, -1.0f,
            0.5f, -0.5f, -1.0f,
            -0.5f, -0.5f, -1.0f
    };
    public static final int[] QUAD_INDICES = {
            0, 1, 2,
            0, 2, 3
    };
    public static final float[] QUAD_TEXTURE = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f
    };


}
