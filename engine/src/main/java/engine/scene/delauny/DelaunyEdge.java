package engine.scene.delauny;

import engine.object.Mesh;
import org.joml.Vector2f;

public class DelaunyEdge {

    private final Vector2f vertexA;
    private final Vector2f vertexB;

    public DelaunyEdge(Vector2f vertexA, Vector2f vertexB) {
        this.vertexA = vertexA;
        this.vertexB = vertexB;
    }

    public boolean hasBothVertices(Vector2f otherVertexA, Vector2f otherVertexB) {
        if (this.vertexA.equals(otherVertexA)) {
            return this.vertexB.equals(otherVertexB);
        }
        if (this.vertexA.equals(otherVertexB)) {
            return this.vertexB.equals(otherVertexA);
        }
        return false;
    }

    public Vector2f getVertexA() {
        return vertexA;
    }

    public Vector2f getVertexB() {
        return vertexB;
    }

    public Mesh toMesh() {
        float[] vertices = new float[]{vertexA.x(), vertexA.y(), -1,
                vertexB.x(), vertexB.y(), -1};
        int[] indices = new int[]{0, 1};
        return new Mesh(vertices, indices, vertices);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return hasBothVertices(((DelaunyEdge) o).vertexA, ((DelaunyEdge) o).vertexB);
    }

}
