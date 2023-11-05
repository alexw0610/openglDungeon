package engine.service;

import engine.enums.UIGroupKey;

import java.util.LinkedList;
import java.util.List;

public class UISceneService {

    private static final ThreadLocal<UISceneService> INSTANCE = ThreadLocal.withInitial(UISceneService::new);

    private boolean inventoryVisible;
    private boolean hudVisible;
    private boolean statsVisible;
    private boolean closeDialogVisible;
    private boolean gameOverDialogVisible;

    private final List<UIGroupKey> visibleUIGroups;

    private UISceneService() {
        this.inventoryVisible = false;
        this.hudVisible = true;
        this.statsVisible = false;
        this.closeDialogVisible = false;
        this.gameOverDialogVisible = false;
        visibleUIGroups = new LinkedList<>();
        updateUIPartsVisiblity();
    }

    public static UISceneService getInstance() {
        return INSTANCE.get();
    }

    public static void setInstance(UISceneService uiSceneService) {
        INSTANCE.set(uiSceneService);
    }

    public void showCombatUI() {
        this.inventoryVisible = false;
        this.hudVisible = true;
        this.statsVisible = false;
        this.gameOverDialogVisible = false;
        updateUIPartsVisiblity();
    }

    public void showOutOfCombatUI() {
        this.inventoryVisible = false;
        this.hudVisible = true;
        this.statsVisible = false;
        this.gameOverDialogVisible = false;
        updateUIPartsVisiblity();
    }

    public void showInventory() {
        this.inventoryVisible = true;
        this.statsVisible = true;
        updateUIPartsVisiblity();
    }

    public void hideInventory() {
        this.inventoryVisible = false;
        this.statsVisible = false;
        updateUIPartsVisiblity();
    }

    public void showCloseDialog() {
        this.closeDialogVisible = true;
        this.inventoryVisible = false;
        this.gameOverDialogVisible = false;
        updateUIPartsVisiblity();

    }

    public void showGameOverDialog() {
        this.gameOverDialogVisible = true;
        this.inventoryVisible = false;
        this.closeDialogVisible = false;
        updateUIPartsVisiblity();

    }

    public void hideCloseDialog() {
        this.closeDialogVisible = false;
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
        if (closeDialogVisible) {
            this.visibleUIGroups.add(UIGroupKey.CLOSE_DIALOG);
        } else {
            this.visibleUIGroups.remove(UIGroupKey.CLOSE_DIALOG);
        }
        if (gameOverDialogVisible) {
            this.visibleUIGroups.add(UIGroupKey.GAME_OVER_DIALOG);
        } else {
            this.visibleUIGroups.remove(UIGroupKey.GAME_OVER_DIALOG);
        }
    }

    public boolean isInventoryVisible() {
        return this.inventoryVisible;
    }

    public boolean isCloseDialogVisible() {
        return this.closeDialogVisible;
    }

    public boolean isGameOverDialogVisible() {
        return this.gameOverDialogVisible;
    }

    public List<UIGroupKey> getVisibleUIGroups() {
        return this.visibleUIGroups;
    }
}
