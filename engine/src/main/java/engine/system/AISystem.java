package engine.system;

import engine.component.*;
import engine.entity.ComponentBuilder;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class AISystem {
    public static void processEntity(Entity entity) {
        AIComponent aiComponent = entity.getComponentOfType(AIComponent.class);
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        PhysicsComponent physicsComponent = entity.getComponentOfType(PhysicsComponent.class);
        checkAggro(aiComponent, transformationComponent);
        switch (aiComponent.getCurrentState()) {
            case IDLE:
                if (Math.random() < 0.90) {
                    break;
                }
                idle(aiComponent, transformationComponent);
                break;
            case ATTACKING:
                attacking(aiComponent, transformationComponent);
                break;
            case PATHING:
                pathing(aiComponent, transformationComponent, physicsComponent);
                break;
        }
    }

    private static void idle(AIComponent aiComponent, TransformationComponent transformationComponent) {
        NavMap navMap = NavHandler.getInstance().getNavMap();
        Vector2d targetPosition = new Vector2d();
        List<Vector2i> pathToTarget = new ArrayList<>();
        Vector2d currentPosition = transformationComponent.getPosition();
        while (pathToTarget.isEmpty()) {
            currentPosition.add((int) Math.round(((Math.random() * 2) - 1) * 6), (int) Math.round(((Math.random() * 2) - 1) * 6), targetPosition);
            pathToTarget = new Pathfinding().getPath(navMap, currentPosition, targetPosition);
        }
        aiComponent.setCurrentState(AIBehaviourState.PATHING);
        aiComponent.setPathToTarget(pathToTarget);
    }

    private static void attacking(AIComponent aiComponent, TransformationComponent transformationComponent) {
        if (aiComponent.getAttackedLast() == 0 || aiComponent.getAttackedLast() < System.currentTimeMillis() - 1500.0) {
            AttackComponent attack = (AttackComponent) ComponentBuilder.fromTemplate("slashAttack");
            attack.setTargetComponentConstraint(PlayerTag.class);
            EntityBuilder.builder()
                    .withComponent(attack)
                    .at(transformationComponent.getPosition().x(), transformationComponent.getPosition().y())
                    .buildAndInstantiate();
            aiComponent.setAttackedLast(System.currentTimeMillis());
        }
    }

    private static void pathing(AIComponent aiComponent, TransformationComponent transformationComponent, PhysicsComponent physicsComponent) {
        List<Vector2i> currentPath = aiComponent.getPathToTarget();
        if (currentPath != null && !currentPath.isEmpty()) {
            Vector2i currentTarget = currentPath.get(0);
            Vector2d currentTargetD = new Vector2d(currentTarget.x(), currentTarget.y());
            if (transformationComponent.getPosition().distance(currentTargetD) < 0.01) {
                currentPath.remove(0);
                aiComponent.setPathToTarget(currentPath);
                physicsComponent.setMoveToTarget(null);
            } else if (physicsComponent.getMoveToTarget() == null || !physicsComponent.getMoveToTarget().equals(currentTargetD)) {
                physicsComponent.setMoveToTarget(currentTargetD);
            }
        } else {
            aiComponent.setCurrentState(AIBehaviourState.IDLE);
            aiComponent.setPathToTarget(null);
        }
    }

    private static void checkAggro(AIComponent aiComponent, TransformationComponent transformationComponent) {
        NavMap navMap = NavHandler.getInstance().getNavMap();
        List<Entity> targets = EntityHandler.getInstance().getAllEntitiesWithComponents(AiTargetTag.class);
        Vector2d currentPosition = transformationComponent.getPosition();
        Optional<Entity> nearestTarget = targets.stream().min(Comparator.comparingDouble(targetA -> targetA.getComponentOfType(TransformationComponent.class).getPosition().distance(currentPosition)));
        if (nearestTarget.isPresent()) {
            Vector2d targetPosition = nearestTarget.get().getComponentOfType(TransformationComponent.class).getPosition();
            if (CollisionUtil.hasLineOfSight(currentPosition, targetPosition, 5)) {
                if (currentPosition.distance(targetPosition) < 1) {
                    aiComponent.setCurrentState(AIBehaviourState.ATTACKING);
                } else if (aiComponent.getCurrentState().equals(AIBehaviourState.PATHING)
                        && aiComponent.getPathToTarget() != null
                        && !aiComponent.getPathToTarget().isEmpty()
                        && targetDistanceDeltaSmallerThan(aiComponent, targetPosition, 1)) {
                    aiComponent.setCurrentState(AIBehaviourState.PATHING);
                } else {
                    aiComponent.setCurrentState(AIBehaviourState.PATHING);
                    List<Vector2i> pathToTarget = new Pathfinding().getPath(navMap, currentPosition, targetPosition);
                    aiComponent.setPathToTarget(pathToTarget);
                }
            } else {
                aiComponent.setCurrentState(AIBehaviourState.IDLE);
            }
        }
    }

    private static boolean targetDistanceDeltaSmallerThan(AIComponent aiComponent, Vector2d playerPos, int distance) {
        Vector2i targetI = aiComponent.getPathToTarget().get(aiComponent.getPathToTarget().size() - 1);
        Vector2dc targetDc = new Vector2d(targetI.x(), targetI.y());
        return playerPos.distance(targetDc) < distance;
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(AIComponent.class)
                && entity.hasComponentOfType(PhysicsComponent.class);
    }
}
