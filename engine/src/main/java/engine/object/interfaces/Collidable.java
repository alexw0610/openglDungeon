package engine.object.interfaces;

import engine.object.Hitbox;

public interface Collidable {

    boolean isCollidable();

    Hitbox getHitbox();
}
