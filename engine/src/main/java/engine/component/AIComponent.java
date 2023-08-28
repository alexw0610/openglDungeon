package engine.component;

import engine.entity.Entity;
import engine.enums.AIBehaviourState;
import org.joml.Vector2i;

import java.util.List;

public class AIComponent implements Component {
    private static final long serialVersionUID = 6264644310722620263L;
    private List<Vector2i> pathToTarget;
    private AIBehaviourState currentState;
    private boolean hostile;
    private double attackedLast;

    private Entity currentTarget;

    public AIComponent() {
        this.currentState = AIBehaviourState.IDLE;
        this.hostile = true;
    }

    public List<Vector2i> getPathToTarget() {
        return pathToTarget;
    }

    public void setPathToTarget(List<Vector2i> pathToTarget) {
        this.pathToTarget = pathToTarget;
    }

    public AIBehaviourState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(AIBehaviourState currentState) {
        this.currentState = currentState;
    }

    public double getAttackedLast() {
        return attackedLast;
    }

    public void setAttackedLast(double attackedLast) {
        this.attackedLast = attackedLast;
    }

    public boolean isHostile() {
        return hostile;
    }

    public void setHostile(Boolean hostile) {
        this.hostile = hostile;
    }

    public Entity getCurrentTarget() {
        return currentTarget;
    }

    public void setCurrentTarget(Entity currentTarget) {
        this.currentTarget = currentTarget;
    }
}
