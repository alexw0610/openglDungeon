package engine.system;

import engine.component.AttackComponent;
import engine.component.BombComponent;
import engine.component.base.TransformationComponent;
import engine.entity.ComponentBuilder;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.handler.EntityHandler;

public class BombSystem {
    public static void processEntity(Entity entity) {
        BombComponent bombComponent = entity.getComponentOfType(BombComponent.class);
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);

        if (bombComponent.getCreationTime() == 0.0) {
            bombComponent.setCreationTime(System.currentTimeMillis());
        }
        if (System.currentTimeMillis() - bombComponent.getCreationTime() >= (bombComponent.getFuseTime() * 1000)) {
            AttackComponent attack = (AttackComponent) ComponentBuilder.fromTemplate(bombComponent.getAttackComponentTemplate());
            EntityBuilder.builder()
                    .withComponent(attack)
                    .at(transformationComponent.getPosition().x(), transformationComponent.getPosition().y())
                    .buildAndInstantiate();
            EntityHandler.getInstance().removeObject(entity.getEntityId());
        }

    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(BombComponent.class);
    }
}
