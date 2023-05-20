package engine.system;

import engine.Engine;
import engine.component.CollisionComponent;
import engine.component.PhysicsComponent;
import engine.component.SurfaceTag;
import engine.component.TransformationComponent;
import engine.entity.Entity;
import engine.handler.EntityHandler;
import engine.service.util.CollisionUtil;
import org.joml.Vector2d;

import java.util.List;
import java.util.stream.Collectors;

import static engine.EngineConstants.DECAY;
import static engine.EngineConstants.INERTIA;

public class PhysicsSystem {

    public static void processEntity(Entity entity) {
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        PhysicsComponent physicsComponent = entity.getComponentOfType(PhysicsComponent.class);
        applyMoveToTargetMomentum(transformationComponent, physicsComponent);
        applyPhysics(entity, transformationComponent, physicsComponent);
    }

    private static void applyMoveToTargetMomentum(TransformationComponent transformationComponent, PhysicsComponent physicsComponent) {
        double movementSpeed = 50;
        Vector2d moveToTarget = physicsComponent.getMoveToTarget();
        if (moveToTarget != null) {
            Vector2d dir = new Vector2d();
            moveToTarget.sub(transformationComponent.getPosition(), dir);
            dir.normalize();
            double x = physicsComponent.getMomentumX() + Engine.stepTimeDelta * dir.x() * INERTIA * movementSpeed;
            double y = physicsComponent.getMomentumY() + Engine.stepTimeDelta * dir.y() * INERTIA * movementSpeed;
            physicsComponent.setMomentumX(x);
            physicsComponent.setMomentumY(y);
        }
    }

    private static void applyPhysics(Entity entity, TransformationComponent transformationComponent, PhysicsComponent physicsComponent) {
        if (entity.hasComponentOfType(CollisionComponent.class)) {
            CollisionComponent collisionComponent = entity.getComponentOfType(CollisionComponent.class);
            List<Entity> objects = EntityHandler.getInstance().getAllEntitiesWithComponents(TransformationComponent.class, CollisionComponent.class);
            List<Entity> walls = objects.parallelStream().unordered().distinct().filter(e -> e.getComponentOfType(CollisionComponent.class).isObstructsMovement()).collect(Collectors.toList());
            List<Entity> surfaces = objects.parallelStream().unordered().distinct().filter(e -> e.hasComponentOfType(SurfaceTag.class)).collect(Collectors.toList());
            surfaces.remove(entity);
            walls.remove(entity);
            if (willBeSurfaced(transformationComponent, physicsComponent, surfaces)) {
                if (!willBeColliding(transformationComponent, physicsComponent, collisionComponent, walls)) {
                    applyMomentum(transformationComponent, physicsComponent);
                } else {
                    deleteMomentum(physicsComponent);
                }
            } else {
                deleteMomentum(physicsComponent);
            }
        } else {
            applyMomentum(transformationComponent, physicsComponent);
        }
    }

    private static boolean willBeSurfaced(TransformationComponent transformationComponent, PhysicsComponent physicsComponent, List<Entity> surfaces) {
        double x = transformationComponent.getPositionX() + physicsComponent.getMomentumX();
        double y = transformationComponent.getPositionY() + physicsComponent.getMomentumY();
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

    private static boolean willBeColliding(TransformationComponent transformationComponent, PhysicsComponent physicsComponent, CollisionComponent collisionComponent, List<Entity> obstacles) {
        double x = transformationComponent.getPositionX() + physicsComponent.getMomentumX();
        double y = transformationComponent.getPositionY() + physicsComponent.getMomentumY();
        for (Entity entity : obstacles) {
            TransformationComponent transformationComponentTarget = entity.getComponentOfType(TransformationComponent.class);
            CollisionComponent collisionComponentTarget = entity.getComponentOfType(CollisionComponent.class);
            double xTarget = transformationComponentTarget.getPositionX();
            double yTarget = transformationComponentTarget.getPositionY();
            boolean collision = CollisionUtil.checkCollision(new Vector2d(x, y), collisionComponent.getHitBox(), new Vector2d(xTarget, yTarget), collisionComponentTarget.getHitBox());
            if (collision) {
                return true;
            }
        }
        return false;
    }

    private static void applyMomentum(TransformationComponent transformationComponent, PhysicsComponent physicsComponent) {
        double x = transformationComponent.getPositionX() + physicsComponent.getMomentumX();
        double y = transformationComponent.getPositionY() + physicsComponent.getMomentumY();
        if (physicsComponent.isGravity()) {
            double movement = Math.abs(physicsComponent.getMomentumY()) + Math.abs(physicsComponent.getMomentumX());
            if (movement > 0.01) {
                y -= Math.max(0.15 - movement, 0) * 0.8;
            }
        }
        transformationComponent.setPositionX(x);
        transformationComponent.setPositionY(y);
        decayMomentum(physicsComponent);
    }

    private static void deleteMomentum(PhysicsComponent component) {
        component.setMomentumX(0);
        component.setMomentumY(0);
    }

    private static void decayMomentum(PhysicsComponent component) {
        component.setMomentumX(decay(component.getMomentumX()));
        component.setMomentumY(decay(component.getMomentumY()));
    }

    private static double decay(double momentum) {
        if (Math.abs(momentum) < 0.0001) {
            return 0;
        } else {
            return momentum * (DECAY);
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(TransformationComponent.class)
                && entity.hasComponentOfType(PhysicsComponent.class);
    }
}
