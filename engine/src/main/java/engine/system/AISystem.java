package engine.system;

import engine.Engine;
import engine.component.AIComponent;
import engine.component.PhysicsComponent;
import engine.component.TransformationComponent;
import engine.entity.Entity;
import engine.enums.AIBehaviourState;
import engine.enums.NavTileType;
import engine.handler.NavHandler;
import engine.object.NavMap;
import org.joml.Vector2d;
import org.joml.Vector2i;

import static engine.EngineConstants.INERTIA;

public class AISystem {
    public static void processEntity(Entity entity) {
        AIComponent aiComponent = entity.getComponentOfType(AIComponent.class);
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        PhysicsComponent physicsComponent = entity.getComponentOfType(PhysicsComponent.class);
        Vector2i currentTilePosition = new Vector2i((int) transformationComponent.getPositionX(), (int) transformationComponent.getPositionY());
        switch (aiComponent.getCurrentState()) {
            case IDLE:
                if (Math.random() < 0.999) {
                    break;
                }
                NavMap navMap = NavHandler.getInstance().getNavMap();
                NavTileType type = NavTileType.WALL;
                Vector2i targetPosition = new Vector2i();
                while (type != NavTileType.FLOOR) {
                    currentTilePosition.add((int) Math.round(((Math.random() * 2) - 1) * 6), (int) Math.round(((Math.random() * 2) - 1) * 6), targetPosition);
                    type = navMap.getTile(targetPosition) != null ? navMap.getTile(targetPosition).getType() : null;
                }
                aiComponent.setCurrentState(AIBehaviourState.PATHING);
                aiComponent.setCurrentTarget(targetPosition);
                break;
            case ATTACKING:
                break;
            case PATHING:
                Vector2i currentTarget = aiComponent.getCurrentTarget();
                Vector2d currentTargetD = new Vector2d(currentTarget.x(), currentTarget.y());
                if (transformationComponent.getPosition().distance(currentTargetD) < 0.01) {
                    aiComponent.setCurrentState(AIBehaviourState.IDLE);
                    aiComponent.setCurrentTarget(null);
                } else {
                    Vector2d dir = new Vector2d();
                    currentTargetD.sub(transformationComponent.getPosition(), dir);
                    dir.normalize();
                    double x = physicsComponent.getMomentumX() + Engine.stepTimeDelta * dir.x() * INERTIA * 0.02;
                    double y = physicsComponent.getMomentumY() + Engine.stepTimeDelta * dir.y() * INERTIA * 0.02;
                    physicsComponent.setMomentumX(x);
                    physicsComponent.setMomentumY(y);
                }
                break;
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(AIComponent.class) && entity.hasComponentOfType(PhysicsComponent.class);
    }
}
