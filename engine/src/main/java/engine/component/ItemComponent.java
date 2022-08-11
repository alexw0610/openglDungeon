package engine.component;

import engine.enums.ItemType;

import java.util.Objects;

public class ItemComponent implements Component {

    private static final long serialVersionUID = -8558888374421418388L;
    private int itemId;
    private int itemTypeId;
    private String itemName;
    private ItemType itemType;
    private String itemTexture;
    private String itemBackdrop;
    private int merchantPrice;

    public ItemComponent(String itemName, String itemType, Integer itemTypeId, String itemTexture) {
        this.itemName = itemName;
        this.itemType = ItemType.valueOf(itemType);
        this.itemTexture = itemTexture;
        this.itemBackdrop = "default_item_backdrop";
        this.itemTypeId = itemTypeId;
    }

    public ItemComponent(int itemId, String itemName, String itemType, String itemTexture, Integer itemTypeId) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemType = ItemType.valueOf(itemType);
        this.itemTexture = itemTexture;
        this.itemBackdrop = "default_item_backdrop";
        this.itemTypeId = itemTypeId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public String getItemTexture() {
        return itemTexture;
    }

    public void setItemTexture(String itemTexture) {
        this.itemTexture = itemTexture;
    }

    public String getItemBackdrop() {
        return itemBackdrop;
    }

    public void setItemBackdrop(String itemBackdrop) {
        this.itemBackdrop = itemBackdrop;
    }

    public int getMerchantPrice() {
        return merchantPrice;
    }

    public void setMerchantPrice(Integer merchantPrice) {
        this.merchantPrice = merchantPrice;
    }

    public int getItemTypeId() {
        return itemTypeId;
    }

    public void setItemTypeId(Integer itemTypeId) {
        this.itemTypeId = itemTypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemComponent that = (ItemComponent) o;
        return Objects.equals(itemId, that.itemId) && Objects.equals(itemName, that.itemName) && itemType == that.itemType && Objects.equals(itemTexture, that.itemTexture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, itemName, itemType, itemTexture);
    }
}
