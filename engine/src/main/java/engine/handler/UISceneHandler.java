package engine.handler;

import engine.component.InventoryComponent;
import engine.component.PlayerTag;
import engine.object.ui.UIInventoryElement;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UISceneHandler {
    private static final ThreadLocal<UISceneHandler> INSTANCE = ThreadLocal.withInitial(UISceneHandler::new);
    private final Map<String, UIInventoryElement> generatedInventories = new HashMap<>();

    private UIInventoryElement activePlayerInventory;
    private UIInventoryElement activeTargetInventory;
    private UIInventoryElement.InventorySlot pickedItem;

    public static UISceneHandler getInstance() {
        return INSTANCE.get();
    }

    public void toggleInventory(String entityId) {
        if (!this.generatedInventories.containsKey(entityId)) {
            this.generateInventory(entityId);
        }
        if (EntityHandler.getInstance().getEntityWithId(entityId).hasComponentOfType(PlayerTag.class)) {
            if (this.activePlayerInventory != null) {
                this.activePlayerInventory = null;
                this.activeTargetInventory = null;
            } else {
                this.activePlayerInventory = this.generatedInventories.get(entityId);
                this.activePlayerInventory.reload();
            }
        } else {
            if (this.activeTargetInventory != null) {
                this.activeTargetInventory = null;
            } else {
                this.activeTargetInventory = this.generatedInventories.get(entityId);
                this.activeTargetInventory.reload();
            }
        }
    }

    public void setInventoryActive(String entityId) {
        if (!this.generatedInventories.containsKey(entityId)) {
            this.generateInventory(entityId);
        }
        if (EntityHandler.getInstance().getEntityWithId(entityId).hasComponentOfType(PlayerTag.class)) {
            this.activePlayerInventory = this.generatedInventories.get(entityId);
        } else {
            this.activeTargetInventory = this.generatedInventories.get(entityId);
        }
    }

    private void generateInventory(String entityId) {
        if (!this.generatedInventories.containsKey(entityId)) {
            this.generatedInventories.put(entityId,
                    new UIInventoryElement(EntityHandler.getInstance()
                            .getEntityWithId(entityId)
                            .getComponentOfType(InventoryComponent.class), entityId));
        }
    }

    public List<UIInventoryElement> getActiveInventories() {
        List<UIInventoryElement> activeInventories = new LinkedList<>();
        if (activePlayerInventory != null) {
            if (activeTargetInventory != null) {
                activePlayerInventory.setxOffset(-0.5);
            }
            activeInventories.add(activePlayerInventory);
        }
        if (activeTargetInventory != null) {
            activeTargetInventory.setxOffset(0.5);
            activeInventories.add(activeTargetInventory);
        }
        return activeInventories;
    }

    public UIInventoryElement.InventorySlot getPickedItem() {
        return this.pickedItem;
    }

    public void setPickedItem(UIInventoryElement.InventorySlot pickedItem) {
        this.pickedItem = pickedItem;
    }
}
