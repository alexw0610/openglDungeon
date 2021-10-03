package engine.system;

import engine.entity.Entity;

public interface System {

    void processEntity(Entity entity);

    static boolean isResponsibleFor(Entity entity) {
        return false;
    }

    ;
}
