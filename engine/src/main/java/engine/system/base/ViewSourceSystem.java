package engine.system.base;

import engine.component.base.TransformationComponent;
import engine.component.tag.ViewBlockingTag;
import engine.component.tag.ViewSourceTag;
import engine.entity.Entity;
import engine.handler.EntityHandler;
import engine.handler.MeshHandler;
import engine.object.Mesh;
import engine.service.RenderService;
import engine.service.VisibilityPolygonFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.joml.Vector2d;
import org.joml.Vector3d;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ViewSourceSystem {

    public static final String VIEW_POLYGON_KEY_PREFIX = "viewPolygon_";

    public static void processEntity(Entity entity) {
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        List<Entity> entities = EntityHandler.getInstance().getAllEntitiesWithComponents(TransformationComponent.class, ViewBlockingTag.class);
        entities.remove(entity);
        int[] angles = new int[]{0, 1, 2, 3, 4, 5};
        List<Mesh> meshes = Arrays.stream(angles)
                .parallel()
                .unordered()
                .mapToObj(angle -> {
                    Vector3d tempDir = new Vector3d(1, 0, 0).rotateZ(1.047198 * angle);
                    return VisibilityPolygonFactory.generateVisibilityPolygon(entities, transformationComponent.getPosition().add(new Vector2d(tempDir.x, tempDir.y)), 10);
                })
                .collect(Collectors.toList());
        for (Mesh mesh : meshes) {
            MeshHandler.getInstance().addMesh(VIEW_POLYGON_KEY_PREFIX + RandomStringUtils.randomAlphanumeric(8), mesh);
            RenderService.getInstance().renderToViewMap(mesh);
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(ViewSourceTag.class)
                && entity.hasComponentOfType(TransformationComponent.class);
    }
}
