package engine.system;

import engine.component.*;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import org.joml.Vector2d;

public class BleedingSystem {
    public static void processEntity(Entity entity) {
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        CollisionComponent collisionComponent = entity.getComponentOfType(CollisionComponent.class);
        BleedingComponent bleedingComponent = entity.getComponentOfType(BleedingComponent.class);
        while (!collisionComponent.collisions.isEmpty()) {
            Vector2d collisionVector = collisionComponent.collisions.poll();
            Entity particleEmitter = EntityBuilder.builder()
                    .withComponent(new TransformationComponent())
                    .at(transformationComponent.getPositionX(), transformationComponent.getPositionY())
                    .withComponent(new ParticleComponent(
                            bleedingComponent.getBloodColorR(),
                            bleedingComponent.getBloodColorG(),
                            bleedingComponent.getBloodColorB(),
                            50.0,
                            6.0,
                            1.0 / 32.0,
                            1250.0,
                            0.13
                    ))
                    .withComponent(new DestructionComponent(100.0))
                    .buildAndInstantiate();
            particleEmitter.getComponentOfType(ParticleComponent.class).setParticleDirection(collisionVector.normalize());
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(BleedingComponent.class) && entity.hasComponentOfType(CollisionComponent.class);
    }
}
