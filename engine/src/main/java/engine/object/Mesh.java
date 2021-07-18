package engine.object;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLContext;
import engine.enumeration.PrimitiveMeshShape;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh extends Primitives {

    private int vaoId;

    private float[] vertices;
    private int[] indices;
    private float[] texture;


    public Mesh(float[] vertices, int[] indices, float[] textCoords) {
        this.vertices = vertices;
        this.indices = indices;
        this.texture = textCoords;
    }

    public Mesh(PrimitiveMeshShape primitive) {
        switch (primitive) {
            case TRIANGLE:
                this.vertices = TRIANGLE_VERTICES;
                this.indices = TRIANGLE_INDICES;
                this.texture = TRIANGLE_TEXTURE;
                break;
            case QUAD:
                this.vertices = QUAD_VERTICES;
                this.indices = QUAD_INDICES;
                this.texture = QUAD_TEXTURE;
        }
    }

    public void loadMesh() {
        GL3 gl = GLContext.getCurrentGL().getGL3();
        int[] vaoids = new int[1];
        gl.glGenVertexArrays(1, vaoids, 0);
        gl.glBindVertexArray(vaoids[0]);

        int[] vboids = new int[4];
        gl.glGenBuffers(4, vboids, 0);
        vaoId = vaoids[0];

        int vertexvboId = vboids[0];
        FloatBuffer verticesBuffer = FloatBuffer.allocate(vertices.length);
        verticesBuffer.put(vertices);
        verticesBuffer.flip();
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboids[0]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, vertices.length * 4, verticesBuffer, GL.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0);

        int indicesVboId = vboids[1];
        IntBuffer indicesBuffer = IntBuffer.allocate(indices.length);
        indicesBuffer.put(indices);
        indicesBuffer.flip();
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, vboids[1]);
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indices.length * 4, indicesBuffer, GL.GL_STATIC_DRAW);

        int texturevboId = vboids[2];
        FloatBuffer textureBuffer = FloatBuffer.allocate(texture.length);
        textureBuffer.put(texture);
        textureBuffer.flip();
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboids[2]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, texture.length * 4, textureBuffer, GL.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(1, 2, GL.GL_FLOAT, false, 0, 0);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
        gl.glBindVertexArray(0);
    }

    public float[] getVertices() {
        return vertices;
    }

    public int[] getIndices() {
        return indices;
    }

    public int getVaoId() {
        return vaoId;
    }

}
