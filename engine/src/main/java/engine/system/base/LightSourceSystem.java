package engine.system.base;

import engine.EntityKeyConstants;
import engine.component.base.CameraComponent;
import engine.component.base.LightSourceComponent;
import engine.component.tag.ShadowCastTag;
import engine.component.base.TransformationComponent;
import engine.entity.Entity;
import engine.handler.EntityHandler;
import engine.handler.MeshHandler;
import engine.object.Mesh;
import engine.service.RenderService;
import engine.service.VisibilityPolygonFactory;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;

public class LightSourceSystem {

    public static void processEntity(Entity entity) {
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        LightSourceComponent lightSourceComponent = entity.getComponentOfType(LightSourceComponent.class);
        Entity camera = EntityHandler.getInstance().getEntityWithComponent(CameraComponent.class);
        if (camera != null) {
            List<Entity> entities = EntityHandler.getInstance().getAllEntitiesWithComponents(TransformationComponent.class, ShadowCastTag.class);
            entities.remove(entity);
            Mesh mesh = VisibilityPolygonFactory.generateVisibilityPolygon(entities, transformationComponent.getPosition(), 10);
            MeshHandler.getInstance().addMesh(EntityKeyConstants.LIGHT_POLYGON_KEY_PREFIX + RandomStringUtils.randomAlphanumeric(8), mesh);
            RenderService.getInstance().renderToLightMap(mesh, transformationComponent.getPosition(), lightSourceComponent.getLightStrength(), lightSourceComponent.getLightFallOff(), lightSourceComponent.getLightColor());
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(LightSourceComponent.class)
                && entity.hasComponentOfType(TransformationComponent.class);
    }
}
