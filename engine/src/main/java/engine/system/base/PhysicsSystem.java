package engine.system.base;

import engine.Engine;
import engine.component.DashComponent;
import engine.component.KnockbackComponent;
import engine.component.StatComponent;
import engine.component.StunComponent;
import engine.component.base.CollisionComponent;
import engine.component.base.PhysicsComponent;
import engine.component.base.TransformationComponent;
import engine.component.tag.SurfaceTag;
import engine.entity.Entity;
import engine.handler.EntityHandler;
import engine.service.util.CollisionUtil;
import org.joml.Vector2d;

import java.util.List;
import java.util.stream.Collectors;

import static engine.EngineConstants.STEP_TIME_FACTOR;

public class PhysicsSystem {

    public static void processEntity(Entity entity) {
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        PhysicsComponent physicsComponent = entity.getComponentOfType(PhysicsComponent.class);
        StatComponent statComponent = entity.getComponentOfType(StatComponent.class);
        if (!entity.hasComponentOfType(KnockbackComponent.class)
                && !entity.hasComponentOfType(StunComponent.class)) {
            applyMoveToTargetMomentum(statComponent, transformationComponent, physicsComponent);
        }
        if (physicsComponent.getMomentumX() != 0
                || physicsComponent.getMomentumY() != 0) {
            applyPhysics(entity, transformationComponent, physicsComponent);
        }
    }

    private static void applyMoveToTargetMomentum(StatComponent statComponent, TransformationComponent transformationComponent, PhysicsComponent physicsComponent) {
        Vector2d moveToTarget = physicsComponent.getMoveToTarget();
        if (moveToTarget != null) {
            Vector2d dir = new Vector2d();
            moveToTarget.sub(transformationComponent.getPosition(), dir);
            dir.normalize();
            double x = physicsComponent.getMomentumX() + ((Engine.stepTimeDelta * STEP_TIME_FACTOR) * (dir.x() * (statComponent.getMovementSpeed() * statComponent.getMovementSpeedModifier())));
            double y = physicsComponent.getMomentumY() + ((Engine.stepTimeDelta * STEP_TIME_FACTOR) * (dir.y() * (statComponent.getMovementSpeed() * statComponent.getMovementSpeedModifier())));
            physicsComponent.setMomentumX(x);
            physicsComponent.setMomentumY(y);
        }
    }

    private static void applyPhysics(Entity entity, TransformationComponent transformationComponent, PhysicsComponent physicsComponent) {
        if (entity.hasComponentOfType(CollisionComponent.class)) {
            CollisionComponent collisionComponent = entity.getComponentOfType(CollisionComponent.class);
            List<Entity> objects = EntityHandler.getInstance().getAllEntitiesWithComponents(TransformationComponent.class, CollisionComponent.class);
            List<Entity> collidableObjects = objects.parallelStream().unordered().distinct().filter(e -> e.getComponentOfType(CollisionComponent.class).isObstructsMovement()).collect(Collectors.toList());
            List<Entity> surfaces = objects.parallelStream().unordered().distinct().filter(e -> e.hasComponentOfType(SurfaceTag.class)).collect(Collectors.toList());
            surfaces.remove(entity);
            collidableObjects.remove(entity);
            Vector2d collisionDirection = willBeColliding(transformationComponent, physicsComponent, collisionComponent, collidableObjects);
            if (collisionDirection != null) {
                deleteMomentum(physicsComponent, collisionDirection);
                handleDashCollision(entity);
                handleKnockbackCollision(entity);
            }
            boolean willBeSurfaced = willBeSurfaced(transformationComponent, physicsComponent, surfaces);
            if (willBeSurfaced) {
                collisionDirection = willBeColliding(transformationComponent, physicsComponent, collisionComponent, collidableObjects);
                if (collisionDirection == null) {
                    applyMomentum(transformationComponent, physicsComponent);
                } else {
                    deleteMomentum(physicsComponent, collisionDirection);
                }
            } else {
                deleteMomentum(physicsComponent);
            }
        } else {
            applyMomentum(transformationComponent, physicsComponent);
        }
    }

    private static void handleDashCollision(Entity entity) {
        if (entity.hasComponentOfType(DashComponent.class)) {
            entity.getComponentOfType(DashComponent.class).setHasCollided();
        }
    }

    private static void handleKnockbackCollision(Entity entity) {
        if (entity.hasComponentOfType(KnockbackComponent.class)) {
            entity.getComponentOfType(KnockbackComponent.class).setHasCollided();
        }
    }

    private static boolean willBeSurfaced(TransformationComponent transformationComponent, PhysicsComponent physicsComponent, List<Entity> surfaces) {
        double x = transformationComponent.getPositionX()
                + (physicsComponent.getMomentumY() != 0 ? physicsComponent.getMomentumX() * (1 / Math.sqrt(2)) : physicsComponent.getMomentumX());
        double y = transformationComponent.getPositionY()
                + (physicsComponent.getMomentumX() != 0 ? physicsComponent.getMomentumY() * (1 / Math.sqrt(2)) : physicsComponent.getMomentumY());
        for (Entity entity : surfaces) {
            TransformationComponent transformationComponentTarget = entity.getComponentOfType(TransformationComponent.class);
            CollisionComponent collisionComponent = entity.getComponentOfType(CollisionComponent.class);
            boolean collision = CollisionUtil.checkInside(new Vector2d(x, y),
                    collisionComponent.getHitBox(),
                    new Vector2d(transformationComponentTarget.getPositionX(), transformationComponentTarget.getPositionY()));
            if (collision) {
                return true;
            }
        }
        return false;
    }

    private static Vector2d willBeColliding(TransformationComponent transformationComponent, PhysicsComponent physicsComponent, CollisionComponent collisionComponent, List<Entity> obstacles) {
        double x = transformationComponent.getPositionX()
                + (physicsComponent.getMomentumY() != 0 ? physicsComponent.getMomentumX() * (1 / Math.sqrt(2)) : physicsComponent.getMomentumX());
        double y = transformationComponent.getPositionY()
                + (physicsComponent.getMomentumX() != 0 ? physicsComponent.getMomentumY() * (1 / Math.sqrt(2)) : physicsComponent.getMomentumY());
        for (Entity obstacle : obstacles) {
            TransformationComponent transformationComponentTarget = obstacle.getComponentOfType(TransformationComponent.class);
            CollisionComponent collisionComponentTarget = obstacle.getComponentOfType(CollisionComponent.class);
            double xTarget = transformationComponentTarget.getPositionX();
            double yTarget = transformationComponentTarget.getPositionY();
            boolean collision = CollisionUtil.checkCollision(new Vector2d(x, y), collisionComponent.getHitBox(), new Vector2d(xTarget, yTarget), collisionComponentTarget.getHitBox());
            if (collision) {
                Vector2d normalizedIntersectionPoint = new Vector2d(new Vector2d(x, y).sub(obstacle.getComponentOfType(TransformationComponent.class).getPosition())).normalize();
                Vector2d[] compass = {
                        new Vector2d(0, 1), //UP
                        new Vector2d(1, 0), //RIGHT
                        new Vector2d(0, -1), //DOWN
                        new Vector2d(-1, 0)  //LEFT
                };
                double maxDotProd = 0.0;
                int maxCompassDir = 0;
                for (int i = 0; i < 4; i++) {
                    double dotProd = normalizedIntersectionPoint.dot(compass[i]);
                    if (dotProd > maxDotProd) {
                        maxDotProd = dotProd;
                        maxCompassDir = i;
                    }
                }
                return compass[maxCompassDir].mul(normalizedIntersectionPoint.dot(compass[maxCompassDir])).mul(2).sub(normalizedIntersectionPoint);
            }
        }
        return null;
    }

    private static void applyMomentum(TransformationComponent transformationComponent, PhysicsComponent physicsComponent) {
        double x = transformationComponent.getPositionX()
                + (physicsComponent.getMomentumY() != 0 ? physicsComponent.getMomentumX() * (1 / Math.sqrt(2)) : physicsComponent.getMomentumX());
        transformationComponent.setPositionX(x);
        double y = transformationComponent.getPositionY()
                + (physicsComponent.getMomentumX() != 0 ? physicsComponent.getMomentumY() * (1 / Math.sqrt(2)) : physicsComponent.getMomentumY());
        transformationComponent.setPositionY(y);
        decayMomentum(physicsComponent);
    }

    private static void deleteMomentum(PhysicsComponent component) {
        component.setMomentumX(0);
        component.setMomentumY(0);
    }

    private static void deleteMomentum(PhysicsComponent component, Vector2d collisionDir) {
        if (Math.abs(collisionDir.x()) > Math.abs(collisionDir.y())) {
            component.setMomentumX(0);
        } else {
            component.setMomentumY(0);
        }
    }

    private static void decayMomentum(PhysicsComponent component) {
        component.setMomentumX(decay(component.getMomentumX()));
        component.setMomentumY(decay(component.getMomentumY()));
    }

    private static double decay(double momentum) {
        if (Math.abs(momentum) < 0.0000001) {
            return 0;
        } else {
            return momentum * ((1 / Engine.stepTimeDelta) * STEP_TIME_FACTOR);
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(TransformationComponent.class)
                && entity.hasComponentOfType(PhysicsComponent.class);
    }
}
