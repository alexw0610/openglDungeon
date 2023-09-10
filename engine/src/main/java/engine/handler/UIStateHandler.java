package engine.handler;

import engine.enums.UIGroupKey;

import java.util.LinkedList;
import java.util.List;

public class UIStateHandler {

    private static final ThreadLocal<UIStateHandler> INSTANCE = ThreadLocal.withInitial(UIStateHandler::new);

    private boolean inventoryVisible;
    private boolean hudVisible;
    private boolean statsVisible;

    private final List<UIGroupKey> visibleUIGroups;

    private UIStateHandler() {
        this.inventoryVisible = false;
        this.hudVisible = true;
        this.statsVisible = true;
        visibleUIGroups = new LinkedList<>();
        updateUIPartsVisiblity();
    }

    public static UIStateHandler getInstance() {
        return INSTANCE.get();
    }

    public static void setInstance(UIStateHandler uiStateHandler) {
        INSTANCE.set(uiStateHandler);
    }

    public void showCombatUI() {
        this.inventoryVisible = false;
        this.hudVisible = true;
        this.statsVisible = false;
        updateUIPartsVisiblity();
    }

    public void showOutOfCombatUI() {
        this.inventoryVisible = false;
        this.hudVisible = true;
        this.statsVisible = true;
        updateUIPartsVisiblity();
    }

    public void showInventory() {
        this.inventoryVisible = true;
        updateUIPartsVisiblity();
    }

    public void closeInventory() {
        this.inventoryVisible = false;
        updateUIPartsVisiblity();
    }

    private void updateUIPartsVisiblity() {
        if (inventoryVisible) {
            this.visibleUIGroups.add(UIGroupKey.INVENTORY);
        } else {
            this.visibleUIGroups.remove(UIGroupKey.INVENTORY);
        }
        if (hudVisible) {
            this.visibleUIGroups.add(UIGroupKey.HUD);
        } else {
            this.visibleUIGroups.remove(UIGroupKey.HUD);
        }
        if (statsVisible) {
            this.visibleUIGroups.add(UIGroupKey.STATS);
        } else {
            this.visibleUIGroups.remove(UIGroupKey.STATS);
        }
    }

    public boolean isInventoryVisible() {
        return inventoryVisible;
    }

    public List<UIGroupKey> getVisibleUIGroups() {
        return visibleUIGroups;
    }
}
