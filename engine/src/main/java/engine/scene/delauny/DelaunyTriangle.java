package engine.scene.delauny;

import engine.object.Mesh;
import org.joml.Intersectionf;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DelaunyTriangle {

    private final Vector2f vertexA;
    private final Vector2f vertexB;
    private final Vector2f vertexC;

    public DelaunyTriangle(Vector2f nodeA, Vector2f nodeB, Vector2f nodeC) {
        this.vertexA = nodeA;
        this.vertexB = nodeB;
        this.vertexC = nodeC;
    }

    public List<DelaunyEdge> getEdges() {
        List<DelaunyEdge> edges = new ArrayList<>();
        edges.add(new DelaunyEdge(vertexA, vertexB));
        edges.add(new DelaunyEdge(vertexB, vertexC));
        edges.add(new DelaunyEdge(vertexC, vertexA));
        return edges;
    }

    public boolean hasCommonVertex(DelaunyTriangle other) {
        List<Vector2f> otherNodes = new ArrayList<>(Arrays.asList(other.getVertexA(), other.getVertexB(), other.getVertexC()));
        return otherNodes.contains(this.vertexA) || otherNodes.contains(this.vertexB) || otherNodes.contains(this.vertexC);
    }

    /**
     * https://github.com/jdiemke/delaunay-triangulator/blob/414af534e6db101db8ea40d848c9d746dcf31cf2/library/src/main/java/io/github/jdiemke/triangulation/Triangle2D.java
     */
    public boolean isPointInCircumcircle(Vector2f point) {
        double a11 = vertexA.x - point.x;
        double a21 = vertexB.x - point.x;
        double a31 = vertexC.x - point.x;

        double a12 = vertexA.y - point.y;
        double a22 = vertexB.y - point.y;
        double a32 = vertexC.y - point.y;

        double a13 = (vertexA.x - point.x) * (vertexA.x - point.x) + (vertexA.y - point.y) * (vertexA.y - point.y);
        double a23 = (vertexB.x - point.x) * (vertexB.x - point.x) + (vertexB.y - point.y) * (vertexB.y - point.y);
        double a33 = (vertexC.x - point.x) * (vertexC.x - point.x) + (vertexC.y - point.y) * (vertexC.y - point.y);

        double det = a11 * a22 * a33 + a12 * a23 * a31 + a13 * a21 * a32 - a13 * a22 * a31 - a12 * a21 * a33
                - a11 * a23 * a32;

        if (isOrientedCCW()) {
            return det > 0.0d;
        }

        return det < 0.0d;
    }

    public boolean isInside(Vector2f point) {
        return Intersectionf.testPointTriangle(point, vertexA, vertexB, vertexC);

    }

    private boolean isOrientedCCW() {
        double a11 = vertexA.x - vertexC.x;
        double a21 = vertexB.x - vertexC.x;

        double a12 = vertexA.y - vertexC.y;
        double a22 = vertexB.y - vertexC.y;

        double det = a11 * a22 - a12 * a21;

        return det > 0.0d;
    }

    public Mesh toMesh() {
        float[] vertices = new float[]{vertexA.x(), vertexA.y(), -1,
                vertexB.x(), vertexB.y(), -1,
                vertexC.x(), vertexC.y(), -1};
        int[] indices = new int[]{0, 1, 1, 2, 2, 0};
        return new Mesh(vertices, indices, vertices);
    }

    public Vector2f getVertexA() {
        return vertexA;
    }

    public Vector2f getVertexB() {
        return vertexB;
    }

    public Vector2f getVertexC() {
        return vertexC;
    }
}
