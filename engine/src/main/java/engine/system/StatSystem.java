package engine.system;

import engine.component.*;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.HitBoxType;
import org.joml.Vector2d;

public class StatSystem {
    public static void processEntity(Entity entity) {
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        StatComponent statComponent = entity.getComponentOfType(StatComponent.class);
        if (!statComponent.isDead()) {
            if (statComponent.getHealthPoints() <= 0) {
                System.out.println("Combat Log: " + entity.getEntityId() + " died!");
                entity.removeComponent(PhysicsComponent.class);
                entity.removeComponent(AIComponent.class);
                entity.removeComponent(CollisionComponent.class);
                entity.removeComponent(AnimationComponent.class);
                InventoryComponent inventoryComponent = entity.getComponentOfType(InventoryComponent.class);
                if (inventoryComponent != null) {
                    for (ItemComponent item : inventoryComponent.getItems()) {
                        Entity droppedItem = EntityBuilder.builder()
                                .at(transformationComponent.getPositionX(), transformationComponent.getPositionY())
                                .withComponent(new RenderComponent("QUAD", item.getItemTexture(), "shader", 0.5, 2))
                                .withComponent(item)
                                .withComponent(new CollisionComponent(HitBoxType.CIRCLE.value(), 0.5, false))
                                .withComponent(new PhysicsComponent()).buildAndInstantiate();
                        droppedItem.getComponentOfType(RenderComponent.class).setShadeless(true);
                        Vector2d dir = new Vector2d((Math.random() * 2 - 1), (Math.random() * 2 - 1)).normalize();
                        droppedItem.getComponentOfType(PhysicsComponent.class).setMomentumX(dir.x() / 10);
                        droppedItem.getComponentOfType(PhysicsComponent.class).setMomentumY(dir.y() / 10);
                        droppedItem.getComponentOfType(PhysicsComponent.class).setGravity(true);
                    }
                    entity.removeComponent(InventoryComponent.class);
                }
                statComponent.setDead(true);
            }
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(StatComponent.class);
    }
}
