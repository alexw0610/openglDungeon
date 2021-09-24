package engine.scene.delauny;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DelaunyTriangulator {

    public static List<DelaunyEdge> generateDelaunyGraph(List<Vector2f> vertices, int graphBoundingSize) {
        List<DelaunyTriangle> triangles = new ArrayList<>();
        DelaunyTriangle rootTriangle = getRootTriangle(graphBoundingSize);
        triangles.add(rootTriangle);
        for (Vector2f vertex : vertices) {
            addVertexToGraph(triangles, vertex);
        }
        triangles = removeRootTriangles(triangles, rootTriangle);
        List<DelaunyEdge> edges = new ArrayList<>();
        triangles.forEach(tri -> edges.addAll(tri.getEdges()));
        return edges.stream().distinct().collect(Collectors.toList());
    }

    private static void addVertexToGraph(List<DelaunyTriangle> triangles, Vector2f vertex) {
        List<DelaunyEdge> invalidTriangleEdges = new ArrayList<>();
        List<DelaunyTriangle> validTriangles = new ArrayList<>();
        for (DelaunyTriangle triangle : triangles) {
            if (triangle.isPointInCircumcircle(vertex)) {
                invalidTriangleEdges.addAll(triangle.getEdges());
            } else {
                validTriangles.add(triangle);
            }
        }
        triangles.clear();
        triangles.addAll(validTriangles);
        if (invalidTriangleEdges.isEmpty()) {
            for (DelaunyTriangle triangle : triangles) {
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

    private static List<DelaunyTriangle> removeRootTriangles(List<DelaunyTriangle> triangles, DelaunyTriangle rootTriangle) {
        List<DelaunyTriangle> finalTriangles = new ArrayList<>();
        for (DelaunyTriangle delaunyTriangle : triangles) {
            if (!delaunyTriangle.hasCommonVertex(rootTriangle)) {
                finalTriangles.add(delaunyTriangle);
            }
        }
        return finalTriangles;
    }

    private static DelaunyTriangle getRootTriangle(int graphBoundingSize) {
        return new DelaunyTriangle(new Vector2f(0, 0),
                new Vector2f(graphBoundingSize * 2.0f, 0),
                new Vector2f(0, graphBoundingSize * 2.0f));
    }

    private static List<DelaunyTriangle> getTrianglesFromEdgesToPoint(List<DelaunyEdge> edges, Vector2f point) {
        List<DelaunyTriangle> triangles = new ArrayList<>();
        for (DelaunyEdge edge : edges) {
            triangles.add(new DelaunyTriangle(edge.getVertexA(), edge.getVertexB(), point));
        }
        return triangles;
    }

    private static List<DelaunyEdge> keepUniqueEdges(List<DelaunyEdge> delaunyEdgeBuffer) {
        List<DelaunyEdge> uniqueEdges = new ArrayList<>();
        for (DelaunyEdge delaunyEdge : delaunyEdgeBuffer) {
            if (Collections.frequency(delaunyEdgeBuffer, delaunyEdge) == 1) {
                uniqueEdges.add(delaunyEdge);
            }
        }
        return uniqueEdges;
    }
}
