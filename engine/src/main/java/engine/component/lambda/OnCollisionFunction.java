package engine.component.lambda;

import engine.entity.Entity;

public interface OnCollisionFunction {
    void run(Entity self, Entity collisionTarget);
}
