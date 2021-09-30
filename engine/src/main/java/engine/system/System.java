package engine.system;

import engine.entity.Entity;

import java.util.List;

public interface System {

    void processEntities(List<Entity> components);

    boolean isResponsibleFor(Entity entity);
}
