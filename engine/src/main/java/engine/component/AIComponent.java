package engine.component;

import engine.enums.AIBehaviourState;
import org.joml.Vector2i;

import java.util.List;

public class AIComponent implements Component {
    private List<Vector2i> pathToTarget;
    private AIBehaviourState currentState;

    public AIComponent() {
        this.currentState = AIBehaviourState.IDLE;
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
}
