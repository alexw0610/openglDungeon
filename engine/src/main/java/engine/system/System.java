package engine.system;

import engine.entity.Entity;

import java.util.List;

public interface System {

    void processEntities(List<Entity> entities);

    boolean isResponsibleFor(Entity entity);
}
