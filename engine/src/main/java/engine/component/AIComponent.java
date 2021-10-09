package engine.component;

import engine.enums.AIBehaviourState;
import org.joml.Vector2i;

public class AIComponent implements Component {
    private Vector2i currentTarget;
    private AIBehaviourState currentState;

    public AIComponent() {
        this.currentState = AIBehaviourState.IDLE;
    }

    public Vector2i getCurrentTarget() {
        return currentTarget;
    }

    public void setCurrentTarget(Vector2i currentTarget) {
        this.currentTarget = currentTarget;
    }

    public AIBehaviourState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(AIBehaviourState currentState) {
        this.currentState = currentState;
    }
}
