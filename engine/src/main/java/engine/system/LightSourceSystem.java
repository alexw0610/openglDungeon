package engine.system;

import engine.component.LightSourceComponent;
import engine.component.TransformationComponent;
import engine.component.tag.ShadowCastTag;
import engine.entity.Entity;
import engine.handler.EntityHandler;
import engine.handler.MeshHandler;
import engine.object.Mesh;
import engine.service.RenderService;
import engine.service.VisibilityPolygonFactory;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;

public class LightSourceSystem {

    private static final RenderService renderService = RenderService.getInstance();
    public static final String LIGHT_POLYGON_KEY_PREFIX = "lightSourcePolygon_";

    public static void processEntity(Entity entity) {
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        LightSourceComponent lightSourceComponent = entity.getComponentOfType(LightSourceComponent.class);
        List<Entity> entities = EntityHandler.getInstance().getAllEntitiesWithComponents(TransformationComponent.class, ShadowCastTag.class);
        entities.remove(entity);
        Mesh mesh = VisibilityPolygonFactory.generateVisibilityPolygon(entities, transformationComponent.getPosition(), 10);
        MeshHandler.getInstance().addMesh(LIGHT_POLYGON_KEY_PREFIX + RandomStringUtils.randomAlphanumeric(8), mesh);
        renderService.renderToLightMap(mesh, transformationComponent.getPosition(), lightSourceComponent.getLightStrength(), lightSourceComponent.getLightFallOff(), lightSourceComponent.getLightColor());
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(LightSourceComponent.class)
                && entity.hasComponentOfType(TransformationComponent.class);
    }
}