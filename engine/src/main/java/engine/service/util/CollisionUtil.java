package engine.service.util;

import engine.component.base.CollisionComponent;
import engine.component.base.TransformationComponent;
import engine.component.tag.ViewBlockingTag;
import engine.entity.Entity;
import engine.handler.EntityHandler;
import engine.object.Edge;
import engine.object.HitBox;
import org.joml.Intersectiond;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CollisionUtil {

    public static boolean checkInside(Vector2d point, HitBox hitbox, Vector2d hitboxPosition) {
        switch (hitbox.getHitBoxType()) {
            case AABB:
                return checkInside(point,
                        hitboxPosition.x() - (hitbox.getSize() / 2),
                        hitboxPosition.y() - (hitbox.getSize() / 2),
                        hitboxPosition.x() + (hitbox.getSize() / 2),
                        hitboxPosition.y() + (hitbox.getSize() / 2)
                );
            case CIRCLE:
                return Intersectiond.testPointCircle(point.x(), point.y(), hitboxPosition.x(), hitboxPosition.y(), Math.pow(hitbox.getSize(), 2));
            default:
                return false;
        }
    }

    public static boolean checkInside(Vector2d point, double minX, double minY, double maxX, double maxY) {
        return Intersectiond.testPointAar(point.x(), point.y(),
                minX,
                minY,
                maxX,
                maxY
        );
    }


    public static boolean checkCollision(Vector2d positionA, HitBox hitBoxA, Vector2d positionB, HitBox hitBoxB) {
        switch (hitBoxA.getHitBoxType()) {
            case AABB:
                Vector2d minA = new Vector2d(positionA.x() - hitBoxA.getSize(), positionA.y() - hitBoxA.getSize());
                Vector2d maxA = new Vector2d(positionA.x() + hitBoxA.getSize(), positionA.y() + hitBoxA.getSize());
                switch (hitBoxB.getHitBoxType()) {
                    case AABB:
                        Vector2d minB = new Vector2d(positionB.x() - hitBoxB.getSize(), positionB.y() - hitBoxB.getSize());
                        Vector2d maxB = new Vector2d(positionB.x() + hitBoxB.getSize(), positionB.y() + hitBoxB.getSize());
                        return Intersectiond.testAarAar(minA, maxA, minB, maxB);
                    case CIRCLE:
                        return Intersectiond.testAarCircle(minA, maxA, positionB, Math.pow(hitBoxB.getSize(), 2));
                    default:
                        return false;
                }
            case CIRCLE:
                switch (hitBoxB.getHitBoxType()) {
                    case AABB:
                        Vector2d minB = new Vector2d(positionB.x() - hitBoxB.getSize(), positionB.y() - hitBoxB.getSize());
                        Vector2d maxB = new Vector2d(positionB.x() + hitBoxB.getSize(), positionB.y() + hitBoxB.getSize());
                        return Intersectiond.testAarCircle(minB, maxB, positionA, Math.pow(hitBoxA.getSize(), 2));
                    case CIRCLE:
                        return Intersectiond.testCircleCircle(positionA.x(), positionA.y(), hitBoxA.getSize(),
                                positionB.x(), positionB.y(), hitBoxB.getSize());
                    default:
                        return false;
                }
            default:
                return false;
        }
    }

    public static List<Entity> getCollisions(TransformationComponent transformationComponent, CollisionComponent collisionComponent, List<Entity> obstacles) {
        double x = transformationComponent.getPositionX();
        double y = transformationComponent.getPositionY();
        List<Entity> collisions = new ArrayList<>();
        for (Entity entity : obstacles) {
            TransformationComponent transformationComponentTarget = entity.getComponentOfType(TransformationComponent.class);
            CollisionComponent collisionComponentTarget = entity.getComponentOfType(CollisionComponent.class);
            double xTarget = transformationComponentTarget.getPositionX();
            double yTarget = transformationComponentTarget.getPositionY();
            if (checkCollision(new Vector2d(x, y), collisionComponent.getHitBox(), new Vector2d(xTarget, yTarget), collisionComponentTarget.getHitBox())) {
                collisions.add(entity);
            }
        }
        return collisions;
    }

    public static boolean hasLineOfSight(Vector2d positionA, Vector2d positionB, double viewDistance) {
        if (positionA.distance(positionB) > viewDistance) {
            return false;
        }
        List<Entity> obstacles = EntityHandler.getInstance().getAllEntitiesWithComponents(ViewBlockingTag.class, CollisionComponent.class);
        obstacles = getEntitiesInViewDistance(obstacles, positionA, viewDistance);
        for (Entity entity : obstacles) {
            Edge[] edges = convertEdgesToWorldSpace(entity.getComponentOfType(CollisionComponent.class).getHitBox().getHitBoxEdges(),
                    entity.getComponentOfType(TransformationComponent.class).getPosition());
            for (Edge edge : edges) {
                Vector2d intersection = new Vector2d();
                if (Intersectiond.intersectLineLine(positionA.x(), positionA.y(), positionB.x(), positionB.y(), edge.getA().x(), edge.getA().y(), edge.getB().x(), edge.getB().y(), intersection)) {
                    if (pointIsOnLineSegment(positionA, positionB, intersection)) {
                        if (pointIsOnLineSegment(edge.getA(), edge.getB(), intersection)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private static boolean pointIsOnLineSegment(Vector2d positionA, Vector2d positionB, Vector2d intersection) {
        return intersection.x() >= Math.min(positionA.x(), positionB.x())
                && intersection.x() <= Math.max(positionA.x(), positionB.x())
                && intersection.y() >= Math.min(positionA.y(), positionB.y())
                && intersection.y() <= Math.max(positionA.y(), positionB.y());
    }

    private static List<Entity> getEntitiesInViewDistance(Collection<Entity> entities, Vector2d viewPoint, double viewDistance) {
        return entities
                .parallelStream()
                .unordered()
                .filter(entity -> entity.getComponentOfType(TransformationComponent.class).getPosition().distance(new Vector2d(viewPoint.x(), viewPoint.y())) < viewDistance)
                .collect(Collectors.toList());
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

    public static boolean distanceLessThan(Entity entityA, Entity entityB, double maximumDistance) {
        return entityA.getComponentOfType(TransformationComponent.class).getPosition()
                .distance(entityB.getComponentOfType(TransformationComponent.class).getPosition()) < maximumDistance;
    }
}
