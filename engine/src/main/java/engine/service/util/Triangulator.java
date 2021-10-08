package engine.service.util;

import engine.object.Edge;
import engine.object.Triangle;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Triangulator {

    public static List<Edge> triangulateVectorField(List<Vector2d> vertices, int graphBoundingSize) {
        List<Triangle> triangles = new ArrayList<>();
        Triangle rootTriangle = getRootTriangle(graphBoundingSize);
        triangles.add(rootTriangle);
        for (Vector2d vertex : vertices) {
            addVertexToGraph(triangles, vertex);
        }
        triangles = removeRootTriangles(triangles, rootTriangle);
        List<Edge> edges = new ArrayList<>();
        triangles.forEach(tri -> edges.addAll(tri.getEdges()));
        return edges.stream().distinct().collect(Collectors.toList());
    }

    private static void addVertexToGraph(List<Triangle> triangles, Vector2d vertex) {
        List<Edge> invalidTriangleEdges = new ArrayList<>();
        List<Triangle> validTriangles = new ArrayList<>();
        for (Triangle triangle : triangles) {
            if (triangle.isPointInCircumcircle(vertex)) {
                invalidTriangleEdges.addAll(triangle.getEdges());
            } else {
                validTriangles.add(triangle);
            }
        }
        triangles.clear();
        triangles.addAll(validTriangles);
        if (invalidTriangleEdges.isEmpty()) {
            for (Triangle triangle : triangles) {
                if (triangle.isInside(vertex)) {
                    triangles.addAll(getTrianglesFromEdgesToPoint(triangle.getEdges(), vertex));
                    break;
                }
            }
        } else {
            invalidTriangleEdges = keepUniqueEdges(invalidTriangleEdges);
            triangles.addAll(getTrianglesFromEdgesToPoint(invalidTriangleEdges, vertex));
        }

        validTriangles.clear();
        invalidTriangleEdges.clear();
    }

    private static List<Triangle> removeRootTriangles(List<Triangle> triangles, Triangle rootTriangle) {
        List<Triangle> finalTriangles = new ArrayList<>();
        for (Triangle triangle : triangles) {
            if (!triangle.hasCommonVertex(rootTriangle)) {
                finalTriangles.add(triangle);
            }
        }
        return finalTriangles;
    }

    private static Triangle getRootTriangle(int graphBoundingSize) {
        return new Triangle(new Vector2d(0, 0),
                new Vector2d(graphBoundingSize * 2.0f, 0),
                new Vector2d(0, graphBoundingSize * 2.0f));
    }

    private static List<Triangle> getTrianglesFromEdgesToPoint(List<Edge> edges, Vector2d point) {
        List<Triangle> triangles = new ArrayList<>();
        for (Edge edge : edges) {
            triangles.add(new Triangle(edge.getA(), edge.getB(), point));
        }
        return triangles;
    }

    private static List<Edge> keepUniqueEdges(List<Edge> edgeBuffer) {
        List<Edge> uniqueEdges = new ArrayList<>();
        for (Edge edge : edgeBuffer) {
            if (Collections.frequency(edgeBuffer, edge) == 1) {
                uniqueEdges.add(edge);
            }
        }
        return uniqueEdges;
    }
}
