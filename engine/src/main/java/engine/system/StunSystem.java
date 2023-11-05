package engine.system;

import engine.EntityKeyConstants;
import engine.component.StunComponent;
import engine.component.base.AnimationComponent;
import engine.component.base.RenderComponent;
import engine.component.base.TransformationComponent;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.PrimitiveMeshShape;
import engine.enums.ShaderType;
import org.apache.commons.lang3.RandomStringUtils;

public class StunSystem {

    public static void processEntity(Entity entity) {
        StunComponent stunComponent = entity.getComponentOfType(StunComponent.class);
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        if ((System.nanoTime() - stunComponent.getStunStartTime()) > (stunComponent.getStunDurationSeconds() * 1000000000)) {
            entity.removeComponent(StunComponent.class);
        }
        if (stunComponent.getStunSpriteEntity() == null) {
            Entity stunSpriteEntity = EntityBuilder.builder()
                    .withComponent(transformationComponent)
                    .withComponent(new AnimationComponent(120.0, true, 6))
                    .withComponent(new RenderComponent(PrimitiveMeshShape.QUAD.value(), stunComponent.getStunTextureKey(), ShaderType.DEFAULT.value(), 1.1, 7))
                    .buildAndInstantiate(EntityKeyConstants.STUN_ENTITY_PREFIX + RandomStringUtils.randomAlphanumeric(6));
            stunComponent.setStunSpriteEntity(stunSpriteEntity);
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(StunComponent.class);
    }
}
