package engine.object.ui;

import engine.component.InventoryComponent;
import engine.component.ItemComponent;
import engine.enums.Color;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UIInventoryElement {

    private static final double ITEM_SCALE = 0.08;
    private static final double MAX_INVENTORY_SLOT_WIDTH = 8.0;

    InventoryComponent inventoryComponent;
    UIElement inventoryBackground;
    InventorySlot[] inventorySlots;
    private double xOffset;
    private String parentEntityId;

    public UIInventoryElement(InventoryComponent inventoryComponent, String parentEntityId) {
        this.inventoryComponent = inventoryComponent;
        this.parentEntityId = parentEntityId;
        generateUIInventoryElement();
    }

    private void generateUIInventoryElement() {
        this.xOffset = 0.0;
        this.inventorySlots = new InventorySlot[inventoryComponent.getInventorySize()];
        this.inventoryBackground = new UIElement(0,
                0,
                Math.min(inventoryComponent.getInventorySize(), MAX_INVENTORY_SLOT_WIDTH) * ITEM_SCALE,
                Math.nextUp(inventoryComponent.getInventorySize() / MAX_INVENTORY_SLOT_WIDTH) * ITEM_SCALE);
        this.inventoryBackground.setFixedSize(true);
        this.inventoryBackground.setLayer(19);
        double currentPosX = inventoryBackground.getPosTopLeftX();
        double currentPosY = inventoryBackground.getPosTopLeftY();
        for (int slot = 0; slot < this.inventorySlots.length; slot++) {
            ItemComponent item = null;
            if (inventoryComponent.getItems().size() > slot) {
                item = inventoryComponent.getItems().get(slot);
            }
            UIElement uiElement = new UIElement(currentPosX + (ITEM_SCALE / 2), currentPosY - (ITEM_SCALE / 2), ITEM_SCALE, ITEM_SCALE);
            currentPosX += ITEM_SCALE;
            if (currentPosX >= inventoryBackground.getPosBottomRightX() - (ITEM_SCALE / 2)) {
                currentPosX = inventoryBackground.getPosTopLeftX();
                currentPosY -= ITEM_SCALE;
            }
            uiElement.setLayer(17);
            uiElement.setFixedSize(true);
            if (item != null) {
                uiElement.setColor(Color.WHITE.value());
                uiElement.setTextureKey(item.getItemTexture());
                uiElement.setTooltip(item.getItemName());
                InventorySlot inventorySlot = new InventorySlot(this, inventoryComponent, item, uiElement);
                UIElement backdropElement = uiElement.clone();
                backdropElement.setTooltip(null);
                backdropElement.setTextureKey(item.getItemBackdrop());
                backdropElement.setLayer(18);
                inventorySlot.setItemBackdropUIElement(backdropElement);
                this.inventorySlots[slot] = inventorySlot;
            } else {
                uiElement.setColor(Color.WHITE.value());
                uiElement.setTextureKey("default_item_backdrop");
                uiElement.setTooltip("empty slot");
                uiElement.setLayer(18);
                this.inventorySlots[slot] = new InventorySlot(this, inventoryComponent, null, uiElement);
            }
        }
    }

    public UIElement getInventoryBackground() {
        UIElement background = this.inventoryBackground.clone();
        background.setScreenPositionX(background.getScreenPositionX() + this.xOffset);
        return background;
    }

    public List<UIElement> getCopyOfItemsToDisplay() {
        List<UIElement> items = Arrays.stream(this.inventorySlots).map(InventorySlot::getItemUIElement).map(UIElement::clone).collect(Collectors.toList());
        items.addAll(Arrays.stream(this.inventorySlots).map(InventorySlot::getItemBackdropUIElement).filter(Objects::nonNull).map(UIElement::clone).collect(Collectors.toList()));
        items.forEach(uiElement -> uiElement.setScreenPositionX(uiElement.getScreenPositionX() + this.xOffset));
        return items;
    }

    public List<InventorySlot> getInventorySlots() {
        return Arrays.stream(this.inventorySlots).collect(Collectors.toList());
    }

    public void setxOffset(double xOffset) {
        this.xOffset = xOffset;
    }

    public double getxOffset() {
        return xOffset;
    }

    public void reload() {
        this.generateUIInventoryElement();
    }

    public String getParentEntityId() {
        return parentEntityId;
    }

    public void setParentEntityId(String parentEntityId) {
        this.parentEntityId = parentEntityId;
    }

    public static class InventorySlot {
        private UIInventoryElement uiInventoryElement;
        private InventoryComponent inventoryComponent;
        private ItemComponent itemComponent;
        private UIElement itemUIElement;
        private UIElement itemBackdropUIElement;

        public InventorySlot(UIInventoryElement uiInventoryElement, InventoryComponent inventoryComponent, ItemComponent itemComponent, UIElement uiElement) {
            this.uiInventoryElement = uiInventoryElement;
            this.inventoryComponent = inventoryComponent;
            this.itemComponent = itemComponent;
            this.itemUIElement = uiElement;
        }

        public ItemComponent getItemComponent() {
            return itemComponent;
        }

        public void setItemComponent(ItemComponent itemComponent) {
            this.itemComponent = itemComponent;
        }

        public UIElement getItemUIElement() {
            return itemUIElement;
        }

        public void setItemUIElement(UIElement itemUIElement) {
            this.itemUIElement = itemUIElement;
        }

        public InventoryComponent getInventoryComponent() {
            return inventoryComponent;
        }

        public void setInventoryComponent(InventoryComponent inventoryComponent) {
            this.inventoryComponent = inventoryComponent;
        }

        public UIInventoryElement getUiInventoryElement() {
            return uiInventoryElement;
        }

        public void setUiInventoryElement(UIInventoryElement uiInventoryElement) {
            this.uiInventoryElement = uiInventoryElement;
        }

        public UIElement getItemBackdropUIElement() {
            return itemBackdropUIElement;
        }

        public void setItemBackdropUIElement(UIElement itemBackdropUIElement) {
            this.itemBackdropUIElement = itemBackdropUIElement;
        }
    }
}
