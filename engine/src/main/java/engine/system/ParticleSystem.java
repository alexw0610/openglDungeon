package engine.system;

import engine.component.*;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.PrimitiveMeshShape;
import engine.enums.ShaderType;
import org.joml.Vector3d;

public class ParticleSystem {
    public static void processEntity(Entity entity) {
        ParticleComponent particleComponent = entity.getComponentOfType(ParticleComponent.class);
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        if (((java.lang.System.nanoTime() / 1000000.0) - particleComponent.getEmittedLast()) > particleComponent.getParticleFrequency()) {
            particleComponent.setEmittedLast(java.lang.System.nanoTime() / 1000000.0);
            for (int i = 0; i < particleComponent.getParticleAmount(); i++) {
                Entity particle = EntityBuilder.builder()
                        .withComponent(new TransformationComponent(transformationComponent.getPositionX(), transformationComponent.getPositionY()))
                        .withComponent(new DestructionComponent(particleComponent.getParticleLifeTime() + ((Math.random() - 0.5) * 100)))
                        .withComponent(new PhysicsComponent())
                        .buildAndInstantiate();
                particle.getComponentOfType(PhysicsComponent.class)
                        .setMomentumX((particleComponent.getParticleDirection().x() + ((Math.random() - 0.5) * 0.75)) * particleComponent.getParticleVelocity() + ((Math.random() - 0.5) * 0.1));
                particle.getComponentOfType(PhysicsComponent.class)
                        .setMomentumY((particleComponent.getParticleDirection().y() + ((Math.random() - 0.5) * 0.75)) * particleComponent.getParticleVelocity() + ((Math.random() - 0.5) * 0.1));
                particle.getComponentOfType(PhysicsComponent.class).setGravity(true);
                if (particleComponent.getParticleTexture() != null) {
                    particle.addComponent(new RenderComponent(PrimitiveMeshShape.QUAD.value(),
                            particleComponent.getParticleTexture(),
                            ShaderType.DEFAULT.value(),
                            particleComponent.getParticleSize(),
                            1));
                    particle.getComponentOfType(RenderComponent.class).setTextureRotation(Math.random() * 180);
                    particle.addComponent(new AnimationComponent(0.01));
                } else {
                    particle.addComponent(new RenderComponent(PrimitiveMeshShape.QUAD.value(),
                            null,
                            ShaderType.DEFAULT.value(),
                            particleComponent.getParticleSize(),
                            1));
                    double colorOffset = ((Math.random() - 0.5) * 0.12);
                    particle.getComponentOfType(RenderComponent.class).setColorOverride(new Vector3d(
                            particleComponent.getColorROverride() + colorOffset,
                            particleComponent.getColorGOverride() + colorOffset,
                            particleComponent.getColorBOverride() + colorOffset)
                    );
                }
                particle.getComponentOfType(RenderComponent.class).setShadeless(true);
            }
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(ParticleComponent.class);
    }
}
