package engine.object.interfaces;

import engine.object.Hitbox;

public interface Collidable {

    boolean isSurface();

    boolean isObstacle();

    Hitbox getHitbox();
}
