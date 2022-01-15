package engine.system;

import engine.Engine;
import engine.component.AIComponent;
import engine.component.PhysicsComponent;
import engine.component.PlayerComponent;
import engine.component.TransformationComponent;
import engine.entity.Entity;
import engine.enums.AIBehaviourState;
import engine.handler.EntityHandler;
import engine.handler.NavHandler;
import engine.object.NavMap;
import engine.service.util.CollisionUtil;
import engine.service.util.Pathfinding;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

import static engine.EngineConstants.INERTIA;

public class AISystem {
    public static void processEntity(Entity entity) {
        AIComponent aiComponent = entity.getComponentOfType(AIComponent.class);
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        PhysicsComponent physicsComponent = entity.getComponentOfType(PhysicsComponent.class);
        Vector2d currentPosition = transformationComponent.getPosition();
        switch (aiComponent.getCurrentState()) {
            case IDLE:
                if (!checkAggro(aiComponent, currentPosition)) {
                    if (Math.random() < 0.90) {
                        break;
                    }
                    idle(aiComponent, currentPosition);
                }
                break;
            case ATTACKING:
                attacking(aiComponent, currentPosition);
                break;
            case PATHING:
                checkAggro(aiComponent, currentPosition);
                pathing(aiComponent, transformationComponent, physicsComponent);
                break;
        }
    }

    private static void idle(AIComponent aiComponent, Vector2d currentPosition) {
        NavMap navMap = NavHandler.getInstance().getNavMap();
        Vector2d targetPosition = new Vector2d();
        List<Vector2i> pathToTarget = new ArrayList<>();
        while (pathToTarget.isEmpty()) {
            currentPosition.add((int) Math.round(((Math.random() * 2) - 1) * 6), (int) Math.round(((Math.random() * 2) - 1) * 6), targetPosition);
            pathToTarget = new Pathfinding().getPath(navMap, currentPosition, targetPosition);
        }
        aiComponent.setCurrentState(AIBehaviourState.PATHING);
        aiComponent.setPathToTarget(pathToTarget);
    }

    private static void attacking(AIComponent aiComponent, Vector2d currentTilePosition) {

    }

    private static void pathing(AIComponent aiComponent, TransformationComponent transformationComponent, PhysicsComponent physicsComponent) {
        List<Vector2i> currentPath = aiComponent.getPathToTarget();
        if (currentPath != null && !currentPath.isEmpty()) {
            Vector2i currentTarget = currentPath.get(0);
            Vector2d currentTargetD = new Vector2d(currentTarget.x(), currentTarget.y());
            if (transformationComponent.getPosition().distance(currentTargetD) < 0.01) {
                currentPath.remove(0);
                aiComponent.setPathToTarget(currentPath);
            } else {
                Vector2d dir = new Vector2d();
                currentTargetD.sub(transformationComponent.getPosition(), dir);
                dir.normalize();
                double x = physicsComponent.getMomentumX() + Engine.stepTimeDelta * dir.x() * INERTIA * 0.02;
                double y = physicsComponent.getMomentumY() + Engine.stepTimeDelta * dir.y() * INERTIA * 0.02;
                physicsComponent.setMomentumX(x);
                physicsComponent.setMomentumY(y);
            }
        } else {
            aiComponent.setCurrentState(AIBehaviourState.IDLE);
            aiComponent.setPathToTarget(null);
        }
    }

    private static boolean checkAggro(AIComponent aiComponent, Vector2d currentPosition) {
        NavMap navMap = NavHandler.getInstance().getNavMap();
        Vector2d playerPos = EntityHandler.getInstance()
                .getEntityWithComponent(PlayerComponent.class)
                .getComponentOfType(TransformationComponent.class)
                .getPosition();
        if (aiComponent.getCurrentState().equals(AIBehaviourState.PATHING)
                && aiComponent.getPathToTarget() != null
                && !aiComponent.getPathToTarget().isEmpty()
                && targetDistanceDeltaSmallerThan(aiComponent, playerPos, 1)) {
            return true;
        }
        if (CollisionUtil.hasLineOfSight(currentPosition, playerPos, 5)) {
            aiComponent.setCurrentState(AIBehaviourState.PATHING);
            List<Vector2i> pathToTarget = new Pathfinding().getPath(navMap, currentPosition, playerPos);
            aiComponent.setPathToTarget(pathToTarget);
            return true;
        }
        return false;
    }

    private static boolean targetDistanceDeltaSmallerThan(AIComponent aiComponent, Vector2d playerPos, int distance) {
        Vector2i targetI = aiComponent.getPathToTarget().get(aiComponent.getPathToTarget().size() - 1);
        Vector2dc targetDc = new Vector2d(targetI.x(), targetI.y());
        return playerPos.distance(targetDc) < distance;
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(AIComponent.class) && entity.hasComponentOfType(PhysicsComponent.class);
    }
}
