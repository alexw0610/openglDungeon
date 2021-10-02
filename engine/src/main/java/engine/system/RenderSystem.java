package engine.system;

import engine.component.RenderComponent;
import engine.component.TransformationComponent;
import engine.entity.Entity;
import engine.service.RenderService;

public class RenderSystem implements System {

    private final RenderService renderService = new RenderService();

    @Override
    public void processEntity(Entity entity) {
        RenderComponent renderComponent = entity.getComponentOfType(RenderComponent.class);
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        this.renderService.renderComponent(renderComponent, transformationComponent);

    }

    @Override
    public boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(RenderComponent.class)
                && entity.hasComponentOfType(TransformationComponent.class);
    }
}
