package engine.object.ui;

import engine.component.InventoryComponent;
import engine.component.ItemComponent;
import engine.enums.Color;
import org.joml.Vector2d;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UIInventoryElement {

    private static final double ITEM_SCALE = 0.08;
    private static final double SYMBOL_SIZE = 0.15;
    private static final double MAX_INVENTORY_SLOT_WIDTH = 8.0;

    InventoryComponent inventoryComponent;
    UIElement inventoryBackground;
    InventorySlot[] inventorySlots;
    private double xOffset;
    private String parentEntityId;
    private List<UIElement> currencyIcons;
    private List<UIText> currencyText;

    private int inventoryItemsHash;
    private int inventoryCurrency;

    public UIInventoryElement(InventoryComponent inventoryComponent, String parentEntityId) {
        this.inventoryComponent = inventoryComponent;
        this.parentEntityId = parentEntityId;
        generateUIInventoryElement();
        generateCurrencyElements();
    }

    private void generateCurrencyElements() {
        this.currencyIcons = new LinkedList<>();
        this.currencyText = new LinkedList<>();
        this.inventoryCurrency = this.inventoryComponent.getCurrency();
        int bronze = this.inventoryCurrency % 100;
        int silver = (this.inventoryCurrency / 100) % 100;
        int gold = this.inventoryCurrency / 10000;
        double startPositionX = (Math.min(this.inventoryComponent.getInventorySize(), MAX_INVENTORY_SLOT_WIDTH) * ITEM_SCALE * -0.5) + (SYMBOL_SIZE * 0.2);
        double startPositionY = Math.nextUp((this.inventoryComponent.getInventorySize() + 0.25) / MAX_INVENTORY_SLOT_WIDTH) * ITEM_SCALE * -0.5;
        if (gold > 0) {
            generateCurrencyElement(gold, startPositionX, startPositionY, "currency_gold", "Gold");
            startPositionX += (SYMBOL_SIZE * 0.2) + (SYMBOL_SIZE * 0.2) * String.valueOf(gold).length();
        }
        if (silver > 0) {
            generateCurrencyElement(silver, startPositionX, startPositionY, "currency_silver", "Silver");
            startPositionX += (SYMBOL_SIZE * 0.2) + (SYMBOL_SIZE * 0.2) * String.valueOf(silver).length();
        }
        if (bronze > 0) {
            generateCurrencyElement(bronze, startPositionX, startPositionY, "currency_bronze", "Bronze");
        }
    }

    private void generateCurrencyElement(int amount, double startPositionX, double startPositionY, String currencyTextureKey, String currencyType) {
        this.currencyIcons.add(generateCurrencyElement(startPositionX, startPositionY, currencyTextureKey, currencyType));
        startPositionX += SYMBOL_SIZE * ITEM_SCALE;
        UIText text = new UIText(String.valueOf(amount));
        text.setColor(Color.WHITE);
        text.setScreenPosition(new Vector2d(startPositionX, startPositionY));
        text.setFixedSize(true);
        text.fontSize(0.0009);
        text.layer(17);
        this.currencyText.add(text);
    }

    private UIElement generateCurrencyElement(double positionX, double positionY, String currencyTextureKey, String currencyType) {
        UIElement currencyIcon = new UIElement(positionX,
                positionY,
                SYMBOL_SIZE,
                SYMBOL_SIZE);
        currencyIcon.setTextureKey(currencyTextureKey);
        currencyIcon.setLayer(17);
        currencyIcon.setFixedSize(true);
        currencyIcon.setColor(Color.WHITE.value());
        currencyIcon.setTooltip(currencyType);
        return currencyIcon;
    }

    private void generateUIInventoryElement() {
        this.inventoryItemsHash = inventoryComponent.getItems().hashCode();
        this.xOffset = 0.0;
        this.inventorySlots = new InventorySlot[inventoryComponent.getInventorySize()];
        this.inventoryBackground = new UIElement(0,
                0,
                Math.min(inventoryComponent.getInventorySize(), MAX_INVENTORY_SLOT_WIDTH) * ITEM_SCALE,
                (Math.nextUp(inventoryComponent.getInventorySize() / MAX_INVENTORY_SLOT_WIDTH) + 0.5) * ITEM_SCALE);
        this.inventoryBackground.setFixedSize(true);
        this.inventoryBackground.setLayer(19);
        generateInventorySlots();
    }

    private void generateInventorySlots() {
        double currentPosX = inventoryBackground.getPosTopLeftX();
        double currentPosY = inventoryBackground.getPosTopLeftY();
        for (int slot = 0; slot < this.inventorySlots.length; slot++) {
            ItemComponent item = null;
            if (inventoryComponent.getItems().size() > slot) {
                item = inventoryComponent.getItems().get(slot);
            }
            UIElement uiElement = new UIElement(currentPosX + (ITEM_SCALE / 2),
                    currentPosY - (ITEM_SCALE / 2),
                    ITEM_SCALE,
                    ITEM_SCALE);
            currentPosX += ITEM_SCALE;
            if (currentPosX >= inventoryBackground.getPosBottomRightX() - (ITEM_SCALE / 2)) {
                currentPosX = inventoryBackground.getPosTopLeftX();
                currentPosY -= ITEM_SCALE;
            }
            uiElement.setLayer(17);
            uiElement.setFixedSize(true);
            if (item != null) {
                generateItemIcon(slot, item, uiElement);
            } else {
                generateEmtpyIcon(slot, uiElement);
            }
        }
    }

    private void generateEmtpyIcon(int slot, UIElement uiElement) {
        uiElement.setColor(Color.WHITE.value());
        uiElement.setTextureKey("default_item_backdrop");
        uiElement.setTooltip("empty slot");
        uiElement.setLayer(18);
        this.inventorySlots[slot] = new InventorySlot(this, inventoryComponent, null, uiElement);
    }

    private void generateItemIcon(int slot, ItemComponent item, UIElement uiElement) {
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
    }

    public List<UIElement> getCopyOfElementsToDisplay() {
        List<UIElement> items = Arrays.stream(this.inventorySlots)
                .map(InventorySlot::getItemUIElement)
                .map(UIElement::clone)
                .collect(Collectors.toList());
        items.addAll(Arrays.stream(this.inventorySlots)
                .map(InventorySlot::getItemBackdropUIElement)
                .filter(Objects::nonNull)
                .map(UIElement::clone)
                .collect(Collectors.toList()));
        items.add(this.inventoryBackground.clone());
        items.addAll(this.currencyIcons.stream()
                .map(UIElement::clone)
                .collect(Collectors.toList()));
        items.forEach(uiElement -> uiElement.setScreenPositionX(uiElement.getScreenPositionX() + this.xOffset));
        return items;
    }

    public List<UIText> getCopyOfTextElementsToDisplay() {
        List<UIText> items = this.currencyText.stream()
                .map(UIText::clone)
                .collect(Collectors.toList());
        items.forEach(uiText -> uiText.setScreenPosition(uiText.getScreenPosition().add(this.xOffset, 0)));
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
        this.generateCurrencyElements();
    }

    public void reloadIfChanged() {
        if (this.inventoryComponent.getItems().hashCode() != inventoryItemsHash) {
            this.generateUIInventoryElement();
        }
        if (this.inventoryComponent.getCurrency() != this.inventoryCurrency) {
            this.generateCurrencyElements();
        }
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
