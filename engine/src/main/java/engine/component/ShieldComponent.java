package engine.component;

import engine.EntityKeyConstants;
import engine.component.base.AnimationComponent;
import engine.component.base.LightSourceComponent;
import engine.component.base.RenderComponent;
import engine.component.base.TransformationComponent;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.PrimitiveMeshShape;
import engine.enums.ShaderType;
import engine.handler.EntityHandler;
import org.apache.commons.lang3.RandomStringUtils;

public class ShieldComponent implements Component {
    private static final long serialVersionUID = -4777911799553051521L;

    private final Entity shieldSpriteEntity;

    public ShieldComponent(TransformationComponent transformationComponent) {
        this.shieldSpriteEntity = EntityBuilder.builder()
                .withComponent(transformationComponent)
                .withComponent(new LightSourceComponent(0.35, 0.3, 0.9, 2.0, 0.1))
                .withComponent(new AnimationComponent(120.0, true, 6))
                .withComponent(new RenderComponent(PrimitiveMeshShape.QUAD.value(), "shield", ShaderType.DEFAULT.value(), 1.1, 7))
                .buildAndInstantiate(EntityKeyConstants.SHIELD_ENTITY_PREFIX + RandomStringUtils.randomAlphanumeric(6));
    }

    @Override
    public void onRemove() {
        EntityHandler.getInstance().removeObject(this.shieldSpriteEntity.getEntityId());
    }
}
