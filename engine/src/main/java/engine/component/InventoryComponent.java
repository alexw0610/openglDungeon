package engine.component;

import java.util.ArrayList;
import java.util.List;

public class InventoryComponent implements Component {

    private int inventorySize;
    private List<ItemComponent> items;
    private double pickUpRange;
    private String lootTable;

    public InventoryComponent(Integer inventorySize, Double pickUpRange) {
        this.inventorySize = inventorySize;
        this.pickUpRange = pickUpRange;
        this.items = new ArrayList<>(inventorySize);
    }

    public int getInventorySize() {
        return inventorySize;
    }

    public void setInventorySize(Integer inventorySize) {
        this.inventorySize = inventorySize;
    }

    public List<ItemComponent> getItems() {
        return items;
    }

    public void setItems(List<ItemComponent> items) {
        this.items = items;
    }

    public double getPickUpRange() {
        return pickUpRange;
    }

    public void setPickUpRange(Double pickUpRange) {
        this.pickUpRange = pickUpRange;
    }

    public String getLootTable() {
        return lootTable;
    }

    public void setLootTable(String lootTable) {
        this.lootTable = lootTable;
    }
}
