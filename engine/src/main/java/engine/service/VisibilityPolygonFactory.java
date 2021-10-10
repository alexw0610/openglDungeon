package engine.service;

import engine.component.CollisionComponent;
import engine.component.TransformationComponent;
import engine.component.VisibleFaceTag;
import engine.entity.Entity;
import engine.object.Edge;
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

    public static Mesh generateVisibilityPolygon(Collection<Entity> entities, Vector2d viewPoint, double viewDistance) {
        List<Entity> entitiesInViewDistance = getEntitiesInViewDistance(entities, viewPoint, viewDistance);
        List<Edge> objectEdges = getObjectEdges(entitiesInViewDistance);
        List<Vector2d> uniqueObjectVertices = getUniqueObjectVertices(objectEdges);
        uniqueObjectVertices.addAll(getFallbackViewingQuad(viewPoint, viewDistance));

        List<Vector2d> visibilityPolygonVertices = new ArrayList<>();
        for (Vector2d vertex : uniqueObjectVertices) {
            Vector2d dir = new Vector2d();
            vertex.sub(viewPoint.x(), viewPoint.y(), dir).normalize();
            visibilityPolygonVertices.add(getIntersectionRotated(viewPoint, objectEdges, dir, vertex.distance(viewPoint), 0));
            visibilityPolygonVertices.add(getIntersectionRotated(viewPoint, objectEdges, dir, viewDistance, ONE_DEGREE_RADIAN));
            visibilityPolygonVertices.add(getIntersectionRotated(viewPoint, objectEdges, dir, viewDistance, -ONE_DEGREE_RADIAN));
        }
        visibilityPolygonVertices = sortClockwise(visibilityPolygonVertices, viewPoint);

        return generateMesh(visibilityPolygonVertices, viewPoint);
    }

    private static Vector2d getIntersectionRotated(Vector2d viewPoint, List<Edge> objectEdges, Vector2d dir, double maxDistance, double rotation) {
        Vector3d tempDir = new Vector3d(dir.x(), dir.y(), 0);
        tempDir.rotateZ(rotation, tempDir);
        Rayd ray = new Rayd(viewPoint.x(), viewPoint.y(), 0, tempDir.x(), tempDir.y(), 0);
        return getIntersectionVertex(maxDistance, objectEdges, viewPoint, ray);
    }

    private static Vector2d getIntersectionVertex(double viewDistance, List<Edge> objectEdges, Vector2dc origin, Rayd ray) {
        double distance = getClosestEdgeIntersection(viewDistance, objectEdges, ray);
        Vector2d dir = new Vector2d(ray.dX, ray.dY);
        Vector2d intersection = new Vector2d(origin).add(dir.mul(distance));
        return new Vector2d(intersection.x(), intersection.y());
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

    private static List<Vector2d> getFallbackViewingQuad(Vector2d viewPoint, double viewDistance) {
        return Arrays.asList(new Vector2d(viewPoint).add(new Vector2d(viewDistance, viewDistance)),
                new Vector2d(viewPoint).add(new Vector2d(viewDistance, -viewDistance)),
                new Vector2d(viewPoint).add(new Vector2d(-viewDistance, viewDistance)),
                new Vector2d(viewPoint).add(new Vector2d(-viewDistance, -viewDistance)));
    }

    private static List<Entity> getEntitiesInViewDistance(Collection<Entity> entities, Vector2d viewPoint, double viewDistance) {
        List<Entity> entitiesInViewDistance = new ArrayList<>();
        for (Entity entity : entities) {
            TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
            if (transformationComponent.getPosition().distance(new Vector2d(viewPoint.x(), viewPoint.y())) < viewDistance) {
                entitiesInViewDistance.add(entity);
            }
        }
        return entitiesInViewDistance;
    }

    private static List<Vector2d> getUniqueObjectVertices(Collection<Edge> edges) {
        List<Vector2d> objectVertices = new ArrayList<>();
        for (Edge edge : edges) {
            objectVertices.add(edge.getA());
            objectVertices.add(edge.getB());
        }
        return objectVertices.stream().distinct().collect(Collectors.toList());
    }

    private static List<Edge> getObjectEdges(Collection<Entity> entities) {
        List<Edge> entityEdges = new ArrayList<>();
        for (Entity entity : entities) {
            TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
            CollisionComponent collisionComponent = entity.getComponentOfType(CollisionComponent.class);
            if (entity.hasComponentOfType(CollisionComponent.class) && entity.hasComponentOfType(VisibleFaceTag.class)) {
                entityEdges.addAll(Collections.singletonList(convertEdgeToWorldSpace(collisionComponent.getHitBox().getHitBoxEdges()[0], transformationComponent.getPosition())));
            } else if (entity.hasComponentOfType(CollisionComponent.class)) {
                entityEdges.addAll(Arrays.asList(convertEdgesToWorldSpace(collisionComponent.getHitBox().getHitBoxEdges(), transformationComponent.getPosition())));
            }
        }
        return entityEdges.stream().distinct().collect(Collectors.toList());
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

    private static Vector2d convertVectorToWorldSpace(Vector2d vector, Vector2d position) {
        return new Vector2d(vector.x() + position.x(), vector.y() + position.y());
    }

    private static List<Vector2d> sortClockwise(List<Vector2d> visibilityPolygonVertices, Vector2d viewPoint) {
        return visibilityPolygonVertices.stream().sorted((v1, v2) -> {
            Vector2d up = new Vector2d(1, 1);
            double angleA = new Vector2d(v1.x(), v1.y()).sub(viewPoint).angle(up);
            double angleB = new Vector2d(v2.x(), v2.y()).sub(viewPoint).angle(up);
            return Double.compare(angleA, angleB);
        }).collect(Collectors.toList());
    }

    private static Mesh generateMesh(List<Vector2d> visibilityPolygonVertices, Vector2d origin) {
        float[] vertices = new float[(visibilityPolygonVertices.size() + 1) * 3];
        vertices[0] = (float) origin.x();
        vertices[1] = (float) origin.y();
        vertices[2] = -1;
        int count = 1;
        for (Vector2d vertex : visibilityPolygonVertices) {
            vertices[count * 3] = (float) vertex.x();
            vertices[count * 3 + 1] = (float) vertex.y();
            vertices[count * 3 + 2] = (float) -1;
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
