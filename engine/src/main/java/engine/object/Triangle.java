package engine.object;

import org.joml.Intersectiond;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Triangle {

    private final Vector2d vertexA;
    private final Vector2d vertexB;
    private final Vector2d vertexC;

    public Triangle(Vector2d nodeA, Vector2d nodeB, Vector2d nodeC) {
        this.vertexA = nodeA;
        this.vertexB = nodeB;
        this.vertexC = nodeC;
    }

    public List<Edge> getEdges() {
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(vertexA, vertexB));
        edges.add(new Edge(vertexB, vertexC));
        edges.add(new Edge(vertexC, vertexA));
        return edges;
    }

    public boolean hasCommonVertex(Triangle other) {
        List<Vector2d> otherNodes = new ArrayList<>(Arrays.asList(other.getVertexA(), other.getVertexB(), other.getVertexC()));
        return otherNodes.contains(this.vertexA) || otherNodes.contains(this.vertexB) || otherNodes.contains(this.vertexC);
    }

    /**
     * https://github.com/jdiemke/delaunay-triangulator/blob/414af534e6db101db8ea40d848c9d746dcf31cf2/library/src/main/java/io/github/jdiemke/triangulation/Triangle2D.java
     */
    public boolean isPointInCircumcircle(Vector2d point) {
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

    public boolean isInside(Vector2d point) {
        return Intersectiond.testPointTriangle(point, vertexA, vertexB, vertexC);

    }

    private boolean isOrientedCCW() {
        double a11 = vertexA.x - vertexC.x;
        double a21 = vertexB.x - vertexC.x;

        double a12 = vertexA.y - vertexC.y;
        double a22 = vertexB.y - vertexC.y;

        double det = a11 * a22 - a12 * a21;

        return det > 0.0d;
    }

    public Vector2d getVertexA() {
        return vertexA;
    }

    public Vector2d getVertexB() {
        return vertexB;
    }

    public Vector2d getVertexC() {
        return vertexC;
    }
}
