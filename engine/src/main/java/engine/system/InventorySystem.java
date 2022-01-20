package engine.system;

import engine.component.CollisionComponent;
import engine.component.InventoryComponent;
import engine.component.ItemComponent;
import engine.component.TransformationComponent;
import engine.entity.Entity;
import engine.enums.HitBoxType;
import engine.handler.EntityHandler;
import engine.object.HitBox;
import engine.service.util.CollisionUtil;

import java.util.List;

public class InventorySystem {
    public static void processEntity(Entity entity) {
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        InventoryComponent inventoryComponent = entity.getComponentOfType(InventoryComponent.class);
        List<Entity> items = EntityHandler.getInstance().getAllEntitiesWithComponents(ItemComponent.class, CollisionComponent.class);
        if (inventoryComponent.getPickUpRange() > 0.0) {
            for (Entity item : items) {
                new HitBox(HitBoxType.CIRCLE, inventoryComponent.getPickUpRange());
                if (CollisionUtil.checkCollision(transformationComponent.getPosition(),
                        item.getComponentOfType(CollisionComponent.class).getHitBox(),
                        item.getComponentOfType(TransformationComponent.class).getPosition(),
                        new HitBox(HitBoxType.CIRCLE, entity.getComponentOfType(InventoryComponent.class).getPickUpRange()))) {
                    if (inventoryComponent.getInventorySize() > inventoryComponent.getItems().size()) {
                        inventoryComponent.getItems().add(item.getComponentOfType(ItemComponent.class));
                        EntityHandler.getInstance().removeObject(item.getEntityId());
                    }
                }
            }
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(InventoryComponent.class)
                && entity.hasComponentOfType(CollisionComponent.class);
    }
}
