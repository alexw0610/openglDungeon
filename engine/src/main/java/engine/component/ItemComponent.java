package engine.component;

import engine.enums.ItemType;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Objects;

public class ItemComponent implements Component {

    private static final long serialVersionUID = -8558888374421418388L;
    private String itemId;
    private String itemName;
    private ItemType itemType;
    private String itemTexture;
    private String itemBackdrop;
    private int merchantPrice;

    public ItemComponent(String itemName, String itemType, String itemTexture) {
        this.itemId = RandomStringUtils.randomAlphanumeric(16);
        this.itemName = itemName;
        this.itemType = ItemType.valueOf(itemType);
        this.itemTexture = itemTexture;
        this.itemBackdrop = "default_item_backdrop";
    }

    public ItemComponent(String itemId, String itemName, String itemType, String itemTexture) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemType = ItemType.valueOf(itemType);
        this.itemTexture = itemTexture;
        this.itemBackdrop = "default_item_backdrop";
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
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
