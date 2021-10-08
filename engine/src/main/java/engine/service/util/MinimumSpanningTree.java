package engine.service.util;

import engine.object.Edge;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MinimumSpanningTree {

    public static List<Edge> getMinimumSpanningTreeEdges(List<Edge> paths) {
        List<Vector2d> allNodes = getDistinctNodes(paths);
        List<Vector2d> visitedNodes = new ArrayList<>();
        List<Edge> minimumSpanningTreeEdges = new ArrayList<>();
        List<Edge> sortedEdges = paths.stream().sorted((e1, e2) -> {
            double distanceA = e1.getA().distance(e1.getB());
            double distanceB = e2.getA().distance(e2.getB());
            return Double.compare(distanceA, distanceB);
        }).collect(Collectors.toList());

        int count = 0;
        while (!visitedNodes.containsAll(allNodes) || minimumSpanningTreeEdges.size() < allNodes.size() - 1) {
            if (!introducesCircle(sortedEdges.get(count), visitedNodes, minimumSpanningTreeEdges)) {
                minimumSpanningTreeEdges.add(sortedEdges.get(count));
                visitedNodes.addAll(Arrays.asList(sortedEdges.get(count).getA(), sortedEdges.get(count).getB()));
            }
            count++;
        }
        return minimumSpanningTreeEdges;
    }

    private static boolean introducesCircle(Edge edge, List<Vector2d> visitedNodes, List<Edge> visitedEdges) {
        if (!visitedNodes.contains(edge.getA()) || !visitedNodes.contains(edge.getB())) {
            return false;
        }
        List<Vector2d> componentA = getComponentFor(edge.getA(), visitedEdges);
        List<Vector2d> componentB = getComponentFor(edge.getB(), visitedEdges);
        return componentA.containsAll(componentB);
    }

    private static List<Vector2d> getComponentFor(Vector2d vertex, List<Edge> visitedEdges) {
        List<Vector2d> component = new ArrayList<>();
        recursiveCheckEdges(vertex, component, visitedEdges);
        return component;
    }

    private static void recursiveCheckEdges(Vector2d vertex, List<Vector2d> component, List<Edge> visitedEdges) {
        for (Edge edge : visitedEdges) {
            if (edge.getA().equals(vertex)) {
                component.add(edge.getA());
                if (!component.contains(edge.getB())) {
                    recursiveCheckEdges(edge.getB(), component, visitedEdges);
                }
            }
            if (edge.getB().equals(vertex)) {
                component.add(edge.getB());
                if (!component.contains(edge.getA())) {
                    recursiveCheckEdges(edge.getA(), component, visitedEdges);
                }
            }
        }
    }

    private static List<Vector2d> getDistinctNodes(List<Edge> paths) {
        List<Vector2d> allNodes = new ArrayList<>();
        for (Edge edge : paths) {
            allNodes.add(edge.getA());
            allNodes.add(edge.getB());
        }
        return allNodes.stream().distinct().collect(Collectors.toList());
    }
}
