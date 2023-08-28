package engine.system;

import engine.Engine;
import engine.component.AttackComponent;
import engine.component.DashComponent;
import engine.component.base.AudioComponent;
import engine.component.base.PhysicsComponent;
import engine.component.base.TransformationComponent;
import engine.component.internal.CreatedByComponent;
import engine.entity.ComponentBuilder;
import engine.entity.Entity;
import engine.entity.EntityBuilder;

import static engine.EngineConstants.SECONDS_TO_NANOSECONDS_FACTOR;
import static engine.EngineConstants.STEP_TIME_FACTOR;

public class DashSystem {
    public static void processEntity(Entity entity) {
        DashComponent dashComponent = entity.getComponentOfType(DashComponent.class);
        PhysicsComponent physicsComponent = entity.getComponentOfType(PhysicsComponent.class);
        if (dashComponent.hasCollided()) {
            handleDashCollision(entity);
            entity.removeComponent(DashComponent.class);
        } else if ((System.nanoTime() - dashComponent.getDashStartedGameTimeNano()) > (0.25 * SECONDS_TO_NANOSECONDS_FACTOR)) {
            entity.removeComponent(DashComponent.class);
        } else {
            double x = physicsComponent.getMomentumX();
            x += dashComponent.getDashDirection().x() * dashComponent.getDashVelocity() * (Engine.stepTimeDelta * STEP_TIME_FACTOR);
            physicsComponent.setMomentumX(x);
            double y = physicsComponent.getMomentumY();
            y += dashComponent.getDashDirection().y() * dashComponent.getDashVelocity() * (Engine.stepTimeDelta * STEP_TIME_FACTOR);
            physicsComponent.setMomentumY(y);
        }
    }

    private static void handleDashCollision(Entity entity) {
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        AttackComponent attack = (AttackComponent) ComponentBuilder.fromTemplate("dashHit");
        EntityBuilder.builder()
                .withComponent(attack)
                .at(transformationComponent.getPosition().x(), transformationComponent.getPosition().y())
                .withComponent(new CreatedByComponent(entity))
                .buildAndInstantiate();
        AudioComponent audio = new AudioComponent();
        audio.setPlayOnce(true);
        audio.setAudioKey("impact");
        EntityBuilder.builder()
                .withComponent(audio)
                .at(transformationComponent.getPosition().x(), transformationComponent.getPosition().y())
                .buildAndInstantiate();
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(DashComponent.class);
    }
}
