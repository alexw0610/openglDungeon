package engine.service;

import engine.exception.MeshNotFoundException;
import engine.handler.MeshHandler;
import engine.object.Edge;
import engine.object.GameObject;
import engine.object.Mesh;
import org.joml.Intersectiond;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector3d;
import org.joml.primitives.Rayd;

import java.util.*;
import java.util.stream.Collectors;

public class VisibilityPolygonFactory {

    private static final double ONE_DEGREE_RADIAN = 0.008726646;

    public static Mesh generateVisibilityPolygon(Collection<GameObject> objects, Vector2d viewPoint, double viewDistance) {
        Vector3d origin = new Vector3d(viewPoint.x(), viewPoint.y(), -1);
        List<GameObject> objectsInViewDistance = getObjectsInViewDistance(objects, viewPoint, viewDistance);
        List<Edge> objectEdges = getObjectEdges(objectsInViewDistance);
        List<Vector3d> uniqueObjectVertices = getUniqueObjectVertices(objectEdges);
        uniqueObjectVertices.addAll(getFallbackViewingQuad(origin, viewDistance));
        List<Vector3d> visibilityPolygonVertices = new ArrayList<>();
        for (Vector3d vertex : uniqueObjectVertices) {
            Vector3d dir = new Vector3d(vertex).sub(origin);
            Vector3d hitPoint = getIntersectionVertex(vertex.distance(origin), objectEdges, viewPoint, new Rayd(origin, dir.normalize()));
            visibilityPolygonVertices.add(hitPoint);
            if (hitPoint.equals(vertex)) {
                Vector3d dirRight = new Vector3d();
                dir.rotateZ(ONE_DEGREE_RADIAN, dirRight);
                visibilityPolygonVertices.add(getIntersectionVertex(viewDistance, objectEdges, viewPoint, new Rayd(origin, dirRight.normalize())));
                Vector3d dirLeft = new Vector3d();
                dir.rotateZ(-ONE_DEGREE_RADIAN, dirLeft);
                visibilityPolygonVertices.add(getIntersectionVertex(viewDistance, objectEdges, viewPoint, new Rayd(origin, dirLeft.normalize())));
            }
        }
        visibilityPolygonVertices = sortClockwise(visibilityPolygonVertices, viewPoint);
        return generateMesh(visibilityPolygonVertices, viewPoint);
    }

    private static Vector3d getIntersectionVertex(double viewDistance, List<Edge> objectEdges, Vector2dc origin, Rayd ray) {
        double distance = getClosestEdgeIntersection(viewDistance, objectEdges, ray);
        Vector2d dir = new Vector2d(ray.dX, ray.dY);
        Vector2d intersection = new Vector2d(origin).add(dir.mul(distance));
        return new Vector3d(intersection.x(), intersection.y(), -1);
    }

    private static double getClosestEdgeIntersection(double viewDistance, List<Edge> objectEdges, Rayd ray) {
        double distance = viewDistance;
        for (Edge edge : objectEdges) {
            double t = Intersectiond.intersectRayLineSegment(ray.oX, ray.oY, ray.dX, ray.dY,
                    edge.getA().x(), edge.getA().y(), edge.getB().x(), edge.getB().y());
            distance = Math.min(t == -1.0 ? viewDistance : t, distance);
        }
        return distance;
    }

    private static List<Vector3d> getFallbackViewingQuad(Vector3d viewPoint, double viewDistance) {
        return Arrays.asList(new Vector3d(viewPoint).add(new Vector3d(viewDistance, viewDistance, 0)),
                new Vector3d(viewPoint).add(new Vector3d(viewDistance, -viewDistance, 0)),
                new Vector3d(viewPoint).add(new Vector3d(-viewDistance, viewDistance, 0)),
                new Vector3d(viewPoint).add(new Vector3d(-viewDistance, -viewDistance, 0)));
    }

    private static List<GameObject> getObjectsInViewDistance(Collection<GameObject> objects, Vector2d viewPoint, double viewDistance) {
        List<GameObject> objectsInViewDistance = new ArrayList<>();
        for (GameObject object : objects) {
            if (object.getPosition().distance(new Vector2d(viewPoint.x(), viewPoint.y())) < viewDistance) {
                objectsInViewDistance.add(object);
            }
        }
        return objectsInViewDistance;
    }

    private static List<Vector3d> getUniqueObjectVertices(Collection<Edge> edges) {
        List<Vector3d> objectVertices = new ArrayList<>();
        for (Edge edge : edges) {
            objectVertices.add(edge.getA());
            objectVertices.add(edge.getB());
        }
        return objectVertices.stream().distinct().collect(Collectors.toList());
    }

    private static List<Edge> getObjectEdges(Collection<GameObject> objects) {
        List<Edge> objectVertices = new ArrayList<>();
        for (GameObject object : objects) {
            try {
                Mesh mesh = MeshHandler.MESH_HANDLER.getMeshForKey(object.getPrimitiveMeshShape());
                if (object.isVisibleFace()) {
                    objectVertices.addAll(Collections.singletonList(convertEdgeToWorldSpace(mesh.getEdges()[0], object.getPosition())));
                } else {
                    objectVertices.addAll(Arrays.asList(convertEdgesToWorldSpace(mesh.getEdges(), object.getPosition())));
                }
            } catch (MeshNotFoundException e) {
                System.err.println(e.getMessage());
            }
        }
        return objectVertices.stream().distinct().collect(Collectors.toList());
    }

    private static Edge[] convertEdgesToWorldSpace(Edge[] edges, Vector2d position) {
        Edge[] worldSpaceEdges = new Edge[edges.length];
        for (int i = 0; i < edges.length; i++) {
            worldSpaceEdges[i] = convertEdgeToWorldSpace(edges[i], position);
        }
        return worldSpaceEdges;
    }

    private static Edge convertEdgeToWorldSpace(Edge edge, Vector2d position) {
        return new Edge(convertVectorToWorldSpace(edge.getA(), position),
                convertVectorToWorldSpace(edge.getB(), position));
    }

    private static Vector3d convertVectorToWorldSpace(Vector3d vector, Vector2d position) {
        return new Vector3d(vector.x() + position.x(), vector.y() + position.y(), vector.z());
    }

    private static List<Vector3d> sortClockwise(List<Vector3d> visibilityPolygonVertices, Vector2d viewPoint) {
        return visibilityPolygonVertices.stream().sorted((v1, v2) -> {
            Vector2d up = new Vector2d(1, 1);
            double angleA = new Vector2d(v1.x(), v1.y()).sub(viewPoint).angle(up);
            double angleB = new Vector2d(v2.x(), v2.y()).sub(viewPoint).angle(up);
            return Double.compare(angleA, angleB);
        }).collect(Collectors.toList());
    }

    private static Mesh generateMesh(List<Vector3d> visibilityPolygonVertices, Vector2d origin) {
        float[] vertices = new float[(visibilityPolygonVertices.size() + 1) * 3];
        vertices[0] = (float) origin.x();
        vertices[1] = (float) origin.y();
        vertices[2] = -1;
        int count = 1;
        for (Vector3d vertex : visibilityPolygonVertices) {
            vertices[count * 3] = (float) vertex.x();
            vertices[count * 3 + 1] = (float) vertex.y();
            vertices[count * 3 + 2] = (float) vertex.z();
            count++;
        }
        int[] indices = new int[visibilityPolygonVertices.size() * 3];
        int indexCount = 0;
        for (int i = 0; i < (indices.length) / 3; i++) {
            indices[i * 3] = 0;
            indices[i * 3 + 1] = indexCount + 1;
            indices[i * 3 + 2] = indexCount + 2;
            indexCount++;
        }
        indices[indices.length - 1] = 1;
        return new Mesh(vertices, indices, vertices);
    }
}
