package engine.object;

import engine.entity.Entity;

public interface OnCollisionFunction {
    void run(Entity self, Entity collisionTarget);
}
