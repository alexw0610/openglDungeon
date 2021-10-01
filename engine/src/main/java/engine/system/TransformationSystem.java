package engine.system;

import engine.component.PhysicsComponent;
import engine.component.TransformationComponent;
import engine.entity.Entity;

import java.util.List;

public class TransformationSystem implements System {

    @Override
    public void processEntities(List<Entity> entities) {
        for (Entity entity : entities) {
            TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
            PhysicsComponent physicsComponent = entity.getComponentOfType(PhysicsComponent.class);
            double x = transformationComponent.getPositionX() + physicsComponent.getMomentumX();
            double y = transformationComponent.getPositionY() + physicsComponent.getMomentumY();
            transformationComponent.setPositionX(x);
            transformationComponent.setPositionY(y);
        }
    }

    @Override
    public boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(TransformationComponent.class)
                && entity.hasComponentOfType(PhysicsComponent.class);
    }
}
