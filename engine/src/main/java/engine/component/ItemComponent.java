package engine.component;

import engine.enums.ItemType;
import org.apache.commons.lang3.RandomStringUtils;

public class ItemComponent implements Component {

    private static final long serialVersionUID = -8558888374421418388L;
    private String itemId;
    private String itemName;
    private ItemType itemType;
    private String itemTexture;

    public ItemComponent(String itemName, String itemType, String itemTexture) {
        this.itemId = RandomStringUtils.randomAlphanumeric(16);
        this.itemName = itemName;
        this.itemType = ItemType.valueOf(itemType);
        this.itemTexture = itemTexture;
    }

    public ItemComponent(String itemId, String itemName, String itemType, String itemTexture) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemType = ItemType.valueOf(itemType);
        this.itemTexture = itemTexture;
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
}
