package engine.handler;

import engine.enums.UIGroupKey;

public class UIStateHandler {

    private static final ThreadLocal<UIStateHandler> INSTANCE = ThreadLocal.withInitial(UIStateHandler::new);

    private boolean inventoryOpen;
    private boolean hudVisible;
    private boolean statsVisible;

    private UIStateHandler() {
        this.inventoryOpen = false;
        this.hudVisible = true;
        this.statsVisible = true;
        updateUIPartsVisiblity();
    }

    public static UIStateHandler getInstance() {
        return INSTANCE.get();
    }

    public static void setInstance(UIStateHandler uiStateHandler) {
        INSTANCE.set(uiStateHandler);
    }

    public void showInventory() {
        this.inventoryOpen = true;
        this.hudVisible = false;
        this.statsVisible = false;
        updateUIPartsVisiblity();
    }

    public void closeInventory() {
        this.inventoryOpen = false;
        this.hudVisible = true;
        this.statsVisible = true;
        updateUIPartsVisiblity();
    }

    private void updateUIPartsVisiblity() {
        UIHandler.getInstance().toggleUIGroupVisible(UIGroupKey.INVENTORY, inventoryOpen);
        UIHandler.getInstance().toggleUIGroupVisible(UIGroupKey.HUD, hudVisible);
        UIHandler.getInstance().toggleUIGroupVisible(UIGroupKey.STATS, statsVisible);
    }

    public boolean isInventoryOpen() {
        return inventoryOpen;
    }
}
