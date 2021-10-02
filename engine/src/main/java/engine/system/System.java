package engine.system;

import engine.entity.Entity;

public interface System {

    void processEntity(Entity entity);

    boolean isResponsibleFor(Entity entity);
}
