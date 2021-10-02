package engine.interfaces;

import engine.object.HitBox;

public interface Collidable {

    boolean isSurface();

    boolean isObstacle();

    HitBox getHitbox();
}
