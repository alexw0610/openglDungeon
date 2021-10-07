package engine.system;

import engine.component.*;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.PrimitiveMeshShape;
import engine.enums.ShaderType;
import engine.handler.EntityHandler;
import org.joml.Vector2d;

public class ParticleSystem {
    public static void processEntity(Entity entity) {
        ParticleComponent particleComponent = entity.getComponentOfType(ParticleComponent.class);
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        if (((java.lang.System.nanoTime() / 1000000.0) - particleComponent.getEmittedLast()) > particleComponent.getParticleFrequency()) {
            particleComponent.setEmittedLast(java.lang.System.nanoTime() / 1000000.0);
            for (int i = 0; i < particleComponent.getParticleAmount(); i++) {
                Entity particle = EntityBuilder.builder()
                        .withComponent(new TransformationComponent(transformationComponent.getPositionX(), transformationComponent.getPositionY()))
                        .withComponent(new RenderComponent(PrimitiveMeshShape.QUAD, particleComponent.getParticleTexture(), ShaderType.DEFAULT, particleComponent.getParticleSize(), 5))
                        .withComponent(new AnimationComponent(0.01))
                        .withComponent(new DestructionComponent(200))
                        .withComponent(new ProjectileComponent(new Vector2d(0.5 - Math.random(), 0.5 - Math.random()), 0.05))
                        .build();
                particle.getComponentOfType(RenderComponent.class).setShadeless(true);
                particle.getComponentOfType(RenderComponent.class).setAlwaysVisible(true);
                particle.getComponentOfType(RenderComponent.class).setTextureRotation(Math.random() * 180);
                EntityHandler.getInstance().addObject(particle);
            }
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(ParticleComponent.class);
    }
}
