package engine.object;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLContext;
import engine.enums.PrimitiveMeshShape;
import org.joml.Vector2d;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh {

    private int vaoId;

    private float[] vertices;
    private int[] indices;
    private float[] texture;

    private boolean isLoaded = false;

    public Mesh(float[] vertices, int[] indices, float[] textCoords) {
        this.vertices = vertices;
        this.indices = indices;
        this.texture = textCoords;
    }

    public Mesh(PrimitiveMeshShape primitive) {
        switch (primitive) {
            case TRIANGLE:
                this.vertices = Primitive.TRIANGLE_VERTICES;
                this.indices = Primitive.TRIANGLE_INDICES;
                this.texture = Primitive.TRIANGLE_TEXTURE;
                break;
            case QUAD:
                this.vertices = Primitive.QUAD_VERTICES;
                this.indices = Primitive.QUAD_INDICES;
                this.texture = Primitive.QUAD_TEXTURE;
        }
    }

    public void loadMesh() {
        if (!isLoaded) {
            GL3 gl = GLContext.getCurrentGL().getGL3();
            int[] vaoids = new int[1];
            gl.glGenVertexArrays(1, vaoids, 0);
            gl.glBindVertexArray(vaoids[0]);

            int[] vboIds = new int[4];
            gl.glGenBuffers(4, vboIds, 0);
            vaoId = vaoids[0];

            FloatBuffer verticesBuffer = FloatBuffer.allocate(vertices.length);
            verticesBuffer.put(vertices);
            verticesBuffer.flip();
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboIds[0]);
            gl.glBufferData(GL.GL_ARRAY_BUFFER, vertices.length * 4L, verticesBuffer, GL.GL_STATIC_DRAW);
            gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0);

            IntBuffer indicesBuffer = IntBuffer.allocate(indices.length);
            indicesBuffer.put(indices);
            indicesBuffer.flip();
            gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, vboIds[1]);
            gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indices.length * 4L, indicesBuffer, GL.GL_STATIC_DRAW);

            FloatBuffer textureBuffer = FloatBuffer.allocate(texture.length);
            textureBuffer.put(texture);
            textureBuffer.flip();
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboIds[2]);
            gl.glBufferData(GL.GL_ARRAY_BUFFER, texture.length * 4L, textureBuffer, GL.GL_STATIC_DRAW);
            gl.glVertexAttribPointer(1, 2, GL.GL_FLOAT, false, 0, 0);

            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
            gl.glBindVertexArray(0);
            isLoaded = true;
        }
    }

    public Vector2d[] getVertices() {
        Vector2d[] verticesVectors = new Vector2d[this.vertices.length / 3];
        for (int i = 0; i < this.vertices.length / 3; i++) {
            verticesVectors[i] = new Vector2d(
                    this.vertices[(i * 3)],
                    this.vertices[(i * 3) + 1]);
        }
        return verticesVectors;
    }

    public Edge[] getEdges() {
        Vector2d[] vertices = getVertices();
        Edge[] edges = new Edge[this.indices.length];
        for (int i = 0; i < this.indices.length / 3; i++) {
            edges[(i * 3)] = new Edge(vertices[this.indices[(i * 3)]], vertices[this.indices[(i * 3) + 1]]);
            edges[(i * 3) + 1] = new Edge(vertices[this.indices[(i * 3) + 1]], vertices[this.indices[(i * 3) + 2]]);
            edges[(i * 3) + 2] = new Edge(vertices[this.indices[(i * 3) + 2]], vertices[this.indices[(i * 3)]]);
        }
        return edges;
    }

    public int[] getIndices() {
        return indices;
    }

    public int getVaoId() {
        return vaoId;
    }

    public void unload() {
        if (isLoaded) {
            GL3 gl = GLContext.getCurrentGL().getGL3();
            gl.glDeleteVertexArrays(1, new int[]{this.vaoId}, 0);
        }
    }

}
