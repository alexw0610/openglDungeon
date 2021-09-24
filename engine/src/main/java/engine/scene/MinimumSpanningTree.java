package engine.scene;

import engine.scene.delauny.DelaunyEdge;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MinimumSpanningTree {

    public static List<DelaunyEdge> getMinimumSpanningTreeEdges(List<DelaunyEdge> paths) {
        List<Vector2f> allNodes = getDistinctNodes(paths);
        List<Vector2f> visitedNodes = new ArrayList<>();
        List<DelaunyEdge> minumumSpanningTreeEdges = new ArrayList<>();
        List<DelaunyEdge> sortedEdges = paths.stream().sorted((e1, e2) -> {
            float distanceA = e1.getVertexA().distance(e1.getVertexB());
            float distanceB = e2.getVertexA().distance(e2.getVertexB());
            return Float.compare(distanceA, distanceB);
        }).collect(Collectors.toList());

        int count = 0;
        while (!visitedNodes.containsAll(allNodes) || minumumSpanningTreeEdges.size() < allNodes.size() - 1) {
            if (!introducesCircle(sortedEdges.get(count), visitedNodes, minumumSpanningTreeEdges)) {
                minumumSpanningTreeEdges.add(sortedEdges.get(count));
                visitedNodes.addAll(Arrays.asList(sortedEdges.get(count).getVertexA(), sortedEdges.get(count).getVertexB()));
            }
            count++;
        }
        return minumumSpanningTreeEdges;
    }

    private static boolean introducesCircle(DelaunyEdge edge, List<Vector2f> visitedNodes, List<DelaunyEdge> visitedEdges) {
        if (!visitedNodes.contains(edge.getVertexA()) || !visitedNodes.contains(edge.getVertexB())) {
            return false;
        }
        List<Vector2f> componentA = getComponentFor(edge.getVertexA(), visitedEdges);
        List<Vector2f> componentB = getComponentFor(edge.getVertexB(), visitedEdges);
        return componentA.containsAll(componentB);
    }

    private static List<Vector2f> getComponentFor(Vector2f vertex, List<DelaunyEdge> visitedEdges) {
        List<Vector2f> component = new ArrayList<>();
        recursiveCheckEdges(vertex, component, visitedEdges);
        return component;
    }

    private static void recursiveCheckEdges(Vector2f vertex, List<Vector2f> component, List<DelaunyEdge> visitedEdges) {
        for (DelaunyEdge edge : visitedEdges) {
            if (edge.getVertexA().equals(vertex)) {
                component.add(edge.getVertexA());
                if (!component.contains(edge.getVertexB())) {
                    recursiveCheckEdges(edge.getVertexB(), component, visitedEdges);
                }
            }
            if (edge.getVertexB().equals(vertex)) {
                component.add(edge.getVertexB());
                if (!component.contains(edge.getVertexA())) {
                    recursiveCheckEdges(edge.getVertexA(), component, visitedEdges);
                }
            }
        }
    }

    private static List<Vector2f> getDistinctNodes(List<DelaunyEdge> paths) {
        List<Vector2f> allNodes = new ArrayList<>();
        for (DelaunyEdge edge : paths) {
            allNodes.add(edge.getVertexA());
            allNodes.add(edge.getVertexB());
        }
        return allNodes.stream().distinct().collect(Collectors.toList());
    }
}
