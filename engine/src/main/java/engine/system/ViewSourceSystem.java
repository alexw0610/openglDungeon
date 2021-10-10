package engine.system;

import engine.component.TransformationComponent;
import engine.component.ViewBlockingTag;
import engine.component.ViewSourceTag;
import engine.entity.Entity;
import engine.handler.EntityHandler;
import engine.handler.MeshHandler;
import engine.object.Mesh;
import engine.service.RenderService;
import engine.service.VisibilityPolygonFactory;

import java.util.List;

public class ViewSourceSystem {

    private static final RenderService renderService = RenderService.getInstance();
    public static final String VIEW_POLYGON_KEY = "viewPolygon";

    public static void processEntity(Entity entity) {
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        List<Entity> entities = EntityHandler.getInstance().getAllEntitiesWithComponents(TransformationComponent.class, ViewBlockingTag.class);
        entities.remove(entity);
        Mesh mesh = VisibilityPolygonFactory.generateVisibilityPolygon(entities, transformationComponent.getPosition(), 20);
        MeshHandler.getInstance().addMesh(VIEW_POLYGON_KEY, mesh);
        renderService.renderToViewMap(mesh);
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(ViewSourceTag.class)
                && entity.hasComponentOfType(TransformationComponent.class);
    }
}
