package engine.system;

import engine.component.TransformationComponent;
import engine.entity.Entity;

import java.util.List;

public class TransformationSystem implements System {

    @Override
    public void processEntities(List<Entity> entities) {
        for (Entity entity : entities) {
            TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
            transformationComponent.setPositionX(transformationComponent.getPositionX() + 0.001);
        }
    }

    @Override
    public boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(TransformationComponent.class);
    }
}
