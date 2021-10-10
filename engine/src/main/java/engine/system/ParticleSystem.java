package engine.system;

import engine.component.*;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.PrimitiveMeshShape;
import engine.enums.ShaderType;

public class ParticleSystem {
    public static void processEntity(Entity entity) {
        ParticleComponent particleComponent = entity.getComponentOfType(ParticleComponent.class);
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        if (((java.lang.System.nanoTime() / 1000000.0) - particleComponent.getEmittedLast()) > particleComponent.getParticleFrequency()) {
            particleComponent.setEmittedLast(java.lang.System.nanoTime() / 1000000.0);
            for (int i = 0; i < particleComponent.getParticleAmount(); i++) {
                Entity particle = EntityBuilder.builder()
                        .withComponent(new TransformationComponent(transformationComponent.getPositionX(), transformationComponent.getPositionY()))
                        .withComponent(new RenderComponent(PrimitiveMeshShape.QUAD.value(), particleComponent.getParticleTexture(), ShaderType.DEFAULT.value(), particleComponent.getParticleSize(), 5))
                        .withComponent(new AnimationComponent(0.01))
                        .withComponent(new DestructionComponent(particleComponent.getParticleLifeTime()))
                        .withComponent(new ProjectileComponent(particleComponent.getParticleDirection().getVector().x(),
                                particleComponent.getParticleDirection().getVector().y(),
                                particleComponent.getParticleVelocity()))
                        .buildAndInstantiate();
                particle.getComponentOfType(RenderComponent.class).setShadeless(true);
                particle.getComponentOfType(RenderComponent.class).setAlwaysVisible(true);
                particle.getComponentOfType(RenderComponent.class).setTextureRotation(Math.random() * 180);
            }
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(ParticleComponent.class);
    }
}
