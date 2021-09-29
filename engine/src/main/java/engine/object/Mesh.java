package engine.object;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLContext;
import engine.enumeration.PrimitiveMeshShape;
import engine.object.enums.Primitives;
import org.joml.Vector3d;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh extends Primitives {

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
            gl.glBufferData(GL.GL_ARRAY_BUFFER, vertices.length * 4, verticesBuffer, GL.GL_STATIC_DRAW);
            gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0);

            IntBuffer indicesBuffer = IntBuffer.allocate(indices.length);
            indicesBuffer.put(indices);
            indicesBuffer.flip();
            gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, vboIds[1]);
            gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indices.length * 4, indicesBuffer, GL.GL_STATIC_DRAW);

            FloatBuffer textureBuffer = FloatBuffer.allocate(texture.length);
            textureBuffer.put(texture);
            textureBuffer.flip();
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboIds[2]);
            gl.glBufferData(GL.GL_ARRAY_BUFFER, texture.length * 4, textureBuffer, GL.GL_STATIC_DRAW);
            gl.glVertexAttribPointer(1, 2, GL.GL_FLOAT, false, 0, 0);

            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
            gl.glBindVertexArray(0);
            isLoaded = true;
        }
    }

    public Vector3d[] getVertices() {
        Vector3d[] verticesVectors = new Vector3d[this.vertices.length / 3];
        for (int i = 0; i < this.vertices.length / 3; i++) {
            verticesVectors[i] = new Vector3d(
                    this.vertices[(i * 3)],
                    this.vertices[(i * 3) + 1],
                    this.vertices[(i * 3) + 2]);
        }
        return verticesVectors;
    }

    public Edge[] getEdges() {
        Vector3d[] vertices = getVertices();
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
