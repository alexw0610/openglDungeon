package engine.service;

import engine.component.*;
import engine.component.base.CollisionComponent;
import engine.component.base.RenderComponent;
import engine.component.base.TransformationComponent;
import engine.component.tag.ItemTag;
import engine.component.tag.PlayerTag;
import engine.entity.Entity;
import engine.enums.Slot;
import engine.enums.UIGroupKey;
import engine.handler.EntityHandler;
import engine.handler.MouseHandler;
import engine.handler.UIHandler;
import engine.object.ui.UIComponentElement;
import engine.object.ui.UIElement;
import engine.object.ui.UIGroup;
import engine.object.ui.UIText;
import engine.service.util.CollisionUtil;
import engine.service.util.CoordinateConverter;
import engine.service.util.UIUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.joml.Vector2d;
import org.joml.Vector3d;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static engine.EngineConstants.*;
import static engine.service.util.AudioUtil.createSoundEntity;
import static engine.service.util.UIUtil.instantiateUIText;
import static engine.service.util.UIUtil.instantiateUITextTitleValuePair;

public class UIService {

    private static final double DEFAULT_BAR_WIDTH = 7.11 * 0.1;
    private static final String MOD_SLOT_AVAILABLE_TEXTURE = "modifier_slot_available";
    private static final String MOD_SLOT_BLOCKED_TEXTURE = "modifier_slot_blocked";
    private static final String UI_BACKGROUND_TEXTURE = "uibg";
    private static final double X_MARGIN = 0.01;
    private static final double Y_MARGIN = 0.01;
    private static final double ROW_SPACING_TEXT = 0.06;
    private static UIService INSTANCE;
    private UIElement healthBar;
    private UIElement shieldBar;
    private UIElement xpBar;
    private UIElement primaryAttackCooldownBar;
    private UIElement secondaryAttackCooldownBar;
    private UIElement dashCooldownBar;
    private UIElement bossHealthBar;
    private UIText levelIndicator;
    private UpgradeComponent selectedUpgradeComponent;

    public static UIService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UIService();
        }
        return INSTANCE;
    }

    public UIService() {
        initPlayerHUD();
        initStatUI();
        initStatUpgradeUI();
        initGunUpgradeUI();
        initCloseDialog();
        initGameOverDialog();
    }

    public void updateUI() {
        updateHUD();
        updateStatUI();
        updateStatUpgradeUI();
        updateGunUpgradeUI();
        updateMouseOver();
        updateMouseSelection();
    }

    private void updateHUD() {
        updatePlayerHUD();
        updateBossHealthbar();
    }

    private void initPlayerHUD() {
        UIElement healthBarBorder = new UIElement(-DEFAULT_BAR_WIDTH - 0.1, 0.85, DEFAULT_BAR_WIDTH, 0.1, 3, "healthBarBorder");
        healthBarBorder.setUiGroupKey(UIGroupKey.HUD);
        UIHandler.getInstance().addObject(healthBarBorder);

        this.healthBar = new UIElement(-DEFAULT_BAR_WIDTH - 0.1, 0.85, DEFAULT_BAR_WIDTH, 0.1, 1, "healthbar");
        this.healthBar.setUiGroupKey(UIGroupKey.HUD);
        UIHandler.getInstance().addObject(healthBar);

        this.shieldBar = new UIElement(-DEFAULT_BAR_WIDTH - 0.1, 0.85, DEFAULT_BAR_WIDTH, 0.1, 2, "shieldBar");
        this.shieldBar.setUiGroupKey(UIGroupKey.HUD);
        UIHandler.getInstance().addObject(shieldBar);

        this.levelIndicator = new UIText("Lvl. 1", -0.06, 0.95, 1, 1, 0.85);
        this.levelIndicator.setUiGroupKey(UIGroupKey.HUD);
        UIHandler.getInstance().addObject(levelIndicator);

        UIElement xpBarBorder = new UIElement(0.1, 0.85, DEFAULT_BAR_WIDTH, 0.1, 2, "xpBarBorder");
        xpBarBorder.setUiGroupKey(UIGroupKey.HUD);
        UIHandler.getInstance().addObject(xpBarBorder);

        this.xpBar = new UIElement(0.1, 0.85, DEFAULT_BAR_WIDTH, 0.1, 1, "xpbar");
        this.xpBar.setUiGroupKey(UIGroupKey.HUD);
        UIHandler.getInstance().addObject(xpBar);

        UIText primaryAttackButton = new UIText("[LMB]", -DEFAULT_BAR_WIDTH / 2.0 - 0.13, 0.80, 1, 1, 0.75);
        primaryAttackButton.setUiGroupKey(UIGroupKey.HUD);
        UIHandler.getInstance().addObject(primaryAttackButton);

        this.primaryAttackCooldownBar = new UIElement(-DEFAULT_BAR_WIDTH / 2.0, 0.75, DEFAULT_BAR_WIDTH, 0.05, 1, "attack_speed_bar");
        this.primaryAttackCooldownBar.setUiGroupKey(UIGroupKey.HUD);
        UIHandler.getInstance().addObject(primaryAttackCooldownBar);

        UIText secondaryAttackButton = new UIText("[RMB]", -DEFAULT_BAR_WIDTH / 2.0 - 0.13, 0.75, 1, 1, 0.75);
        secondaryAttackButton.setUiGroupKey(UIGroupKey.HUD);
        UIHandler.getInstance().addObject(secondaryAttackButton);

        this.secondaryAttackCooldownBar = new UIElement(-DEFAULT_BAR_WIDTH / 2.0, 0.70, DEFAULT_BAR_WIDTH, 0.05, 1, "attack_speed_bar");
        this.secondaryAttackCooldownBar.setUiGroupKey(UIGroupKey.HUD);
        UIHandler.getInstance().addObject(secondaryAttackCooldownBar);

        UIText dashButton = new UIText("[Q]", -DEFAULT_BAR_WIDTH / 2.0 - 0.13, 0.70, 1, 1, 0.75);
        dashButton.setUiGroupKey(UIGroupKey.HUD);
        UIHandler.getInstance().addObject(dashButton);

        this.dashCooldownBar = new UIElement(-DEFAULT_BAR_WIDTH / 2.0, 0.65, DEFAULT_BAR_WIDTH, 0.05, 1, "attack_speed_bar");
        this.dashCooldownBar.setUiGroupKey(UIGroupKey.HUD);
        UIHandler.getInstance().addObject(dashCooldownBar);

        this.bossHealthBar = new UIElement(-DEFAULT_BAR_WIDTH / 2.0, -0.90, DEFAULT_BAR_WIDTH, 0.1, 1, "healthbar");
        this.bossHealthBar.setUiGroupKey(UIGroupKey.HUD);
        UIHandler.getInstance().addObject(bossHealthBar);

    }

    private void initStatUI() {
        UIElement statSummaryPrimary = new UIElement(-1.35, 0.45, 0.525, 0.5, 1, UI_BACKGROUND_TEXTURE);
        statSummaryPrimary.setUiGroupKey(UIGroupKey.STATS);
        UIHandler.getInstance().addObject(statSummaryPrimary);
    }

    private void initStatUpgradeUI() {
        UIElement statUpgradeBackground = new UIElement(-1.35, 0.15, 0.525, 0.275, 1, UI_BACKGROUND_TEXTURE);
        statUpgradeBackground.setUiGroupKey(UIGroupKey.STATS);
        UIHandler.getInstance().addObject(statUpgradeBackground);
    }

    private void initGunUpgradeUI() {
        UIElement gunBackground = new UIElement(-0.6, -0.2, 1.2, 0.8, 1, UI_BACKGROUND_TEXTURE);
        gunBackground.setUiGroupKey(UIGroupKey.INVENTORY);
        UIHandler.getInstance().addObject(gunBackground);

        UIElement gunStatBackground = new UIElement(0.625, -0.2, 0.525, 0.8, 1, UI_BACKGROUND_TEXTURE);
        gunStatBackground.setUiGroupKey(UIGroupKey.INVENTORY);
        UIHandler.getInstance().addObject(gunStatBackground);

        UIElement bulletModifierBackground = new UIElement(-0.6, -0.5, 1.2, 0.275, 1, UI_BACKGROUND_TEXTURE);
        bulletModifierBackground.setUiGroupKey(UIGroupKey.INVENTORY);
        UIHandler.getInstance().addObject("BULLET_MOD_INVENTORY", bulletModifierBackground);

        UIElement gunStatUpgradeBackground = new UIElement(0.625, -0.5, 0.525, 0.275, 1, UI_BACKGROUND_TEXTURE);
        gunStatUpgradeBackground.setUiGroupKey(UIGroupKey.INVENTORY);
        UIHandler.getInstance().addObject(gunStatUpgradeBackground);
    }

    private void initCloseDialog() {
        UIElement closeDialogBackground = new UIElement(-0.35, -0.2, 0.7, 0.4, 1, UI_BACKGROUND_TEXTURE);
        closeDialogBackground.setUiGroupKey(UIGroupKey.CLOSE_DIALOG);
        UIHandler.getInstance().addObject(closeDialogBackground);

        UIText closeDialogPrompt = new UIText("Close the game?", -0.325, 0.1, 0.4, 0.4, 0.8);
        closeDialogPrompt.setUiGroupKey(UIGroupKey.CLOSE_DIALOG);
        closeDialogPrompt.setLayer(2);
        UIHandler.getInstance().addObject(closeDialogPrompt);

        UIText closeDialogInfo = new UIText("Progress can not be saved!", -0.325, 0.0, 0.4, 0.4, 0.7);
        closeDialogInfo.setUiGroupKey(UIGroupKey.CLOSE_DIALOG);
        closeDialogInfo.setLayer(2);
        UIHandler.getInstance().addObject(closeDialogInfo);

        UIText closeDialogOptions = new UIText("Resume[ENTER]   Close[ESC]", -0.325, -0.1, 0.4, 0.4, 0.8);
        closeDialogOptions.setColor(TEXT_COLOR_YELLOW);
        closeDialogOptions.setUiGroupKey(UIGroupKey.CLOSE_DIALOG);
        closeDialogOptions.setLayer(2);
        UIHandler.getInstance().addObject(closeDialogOptions);
    }

    private void initGameOverDialog() {
        UIText gOverDialogPrompt = new UIText("GAME OVER!", -0.225, 0.1, 0.4, 0.4, 1.2);
        gOverDialogPrompt.setUiGroupKey(UIGroupKey.GAME_OVER_DIALOG);
        gOverDialogPrompt.setLayer(2);
        gOverDialogPrompt.setColor(TEXT_COLOR_YELLOW);
        UIHandler.getInstance().addObject(gOverDialogPrompt);

        UIText gOverDialogOptions = new UIText("Retry[ENTER]   Close[ESC]", -0.285, -0.1, 0.4, 0.4, 0.8);
        gOverDialogOptions.setColor(TEXT_COLOR_YELLOW);
        gOverDialogOptions.setUiGroupKey(UIGroupKey.GAME_OVER_DIALOG);
        gOverDialogOptions.setLayer(2);
        UIHandler.getInstance().addObject(gOverDialogOptions);
    }

    private void updateStatUI() {
        UIHandler.getInstance().removeAllObjectsWithPrefix("STATS_");
        StatComponent statComponent = EntityHandler.getInstance()
                .getEntityWithComponent(PlayerTag.class)
                .getComponentOfType(StatComponent.class);
        instantiateUIText("Player Stats:", 0.8, -1.35 + X_MARGIN, 0.95 - Y_MARGIN, TEXT_COLOR_YELLOW, UIGroupKey.STATS, "STATS_");
        instantiateUITextTitleValuePair("Health:", 0.7, -1.35 + X_MARGIN, 0.95 - Y_MARGIN - ROW_SPACING_TEXT, TEXT_COLOR_WHITE, statComponent.getMaxHealthPoints(), 0.375, TEXT_COLOR_YELLOW, UIGroupKey.STATS, "STATS_");
        instantiateUITextTitleValuePair("Shield:", 0.7, -1.35 + X_MARGIN, 0.95 - Y_MARGIN - ROW_SPACING_TEXT * 2, TEXT_COLOR_WHITE, statComponent.getMaxShield(), 0.375, TEXT_COLOR_YELLOW, UIGroupKey.STATS, "STATS_");
        instantiateUITextTitleValuePair("Movement Speed:", 0.7, -1.35 + X_MARGIN, 0.95 - Y_MARGIN - ROW_SPACING_TEXT * 3, TEXT_COLOR_WHITE, statComponent.getMovementSpeed(), 0.375, TEXT_COLOR_YELLOW, UIGroupKey.STATS, "STATS_");
        instantiateUITextTitleValuePair("Dash Cooldown:", 0.7, -1.35 + X_MARGIN, 0.95 - Y_MARGIN - ROW_SPACING_TEXT * 4, TEXT_COLOR_WHITE, statComponent.getDashCooldownSpeed(), 0.375, TEXT_COLOR_YELLOW, UIGroupKey.STATS, "STATS_");
        instantiateUITextTitleValuePair("Dash Stun:", 0.7, -1.35 + X_MARGIN, 0.95 - Y_MARGIN - ROW_SPACING_TEXT * 5, TEXT_COLOR_WHITE, statComponent.getDashStunDuration(), 0.375, TEXT_COLOR_YELLOW, UIGroupKey.STATS, "STATS_");
        instantiateUITextTitleValuePair("Crit. Chance:", 0.7, -1.35 + X_MARGIN, 0.95 - Y_MARGIN - ROW_SPACING_TEXT * 6, TEXT_COLOR_WHITE, statComponent.getCritChance() * 100.0, 0.375, TEXT_COLOR_YELLOW, UIGroupKey.STATS, "STATS_");
        instantiateUITextTitleValuePair("Crit. Damage:", 0.7, -1.35 + X_MARGIN, 0.95 - Y_MARGIN - ROW_SPACING_TEXT * 7, TEXT_COLOR_WHITE, statComponent.getCritBonusDamage(), 0.375, TEXT_COLOR_YELLOW, UIGroupKey.STATS, "STATS_");
    }

    private void updateStatUpgradeUI() {
        UIHandler.getInstance().removeAllObjectsWithPrefix("STAT_UPGRADE_");
        StatComponent statComponent = EntityHandler.getInstance()
                .getEntityWithComponent(PlayerTag.class)
                .getComponentOfType(StatComponent.class);
        displayUpgradeIcons(statComponent, -1.35 + X_MARGIN, 0.325 - Y_MARGIN, 0.11, 0.11, "STAT_UPGRADE_", "playerStatModifier", "unusable_backdrop", 4, true);
    }

    private void updateGunUpgradeUI() {
        StatComponent statComponent = EntityHandler.getInstance()
                .getEntityWithComponent(PlayerTag.class)
                .getComponentOfType(StatComponent.class);
        updateGunDisplayInventory(statComponent);
        displayUpgradeIcons(statComponent, -0.6 + X_MARGIN, -0.325 - Y_MARGIN, 0.11, 0.11, "GUN_UPGRADE_", "bulletModifier", "usable_backdrop", 8, false);
        displayUpgradeIcons(statComponent, 0.625 + X_MARGIN, -0.325 - Y_MARGIN, 0.11, 0.11, "GUN_STAT_UPGRADE_", "gunStatModifier", "unusable_backdrop", 4, true);
        updateGunStatsUI(statComponent);
    }

    private static void updateGunStatsUI(StatComponent statComponent) {
        UIHandler.getInstance().removeAllObjectsWithPrefix("GUN_STATS_");
        GunComponent equippedGun = statComponent.getEquippedGun();
        if (equippedGun != null) {
            instantiateUIText("Gun Stats:", 0.8, 0.625 + X_MARGIN, 0.6 - Y_MARGIN, TEXT_COLOR_YELLOW, UIGroupKey.INVENTORY, "GUN_STATS_");
            instantiateUIText("Primary:", 0.8, 0.625 + X_MARGIN, 0.6 - Y_MARGIN - ROW_SPACING_TEXT * 1, TEXT_COLOR_WHITE, UIGroupKey.INVENTORY, "GUN_STATS_");
            instantiateUITextTitleValuePair("Attack Damage:", 0.7, 0.625 + X_MARGIN, 0.6 - Y_MARGIN - ROW_SPACING_TEXT * 2, TEXT_COLOR_WHITE, equippedGun.getPrimaryBaseDamage() * statComponent.getBaseDamagePrimary(), 0.35, TEXT_COLOR_YELLOW, UIGroupKey.INVENTORY, "GUN_STATS_");
            instantiateUITextTitleValuePair("Attack Speed:", 0.7, 0.625 + X_MARGIN, 0.6 - Y_MARGIN - ROW_SPACING_TEXT * 3, TEXT_COLOR_WHITE, equippedGun.getPrimaryBaseAttackSpeed(), 0.35, TEXT_COLOR_YELLOW, UIGroupKey.INVENTORY, "GUN_STATS_");
            instantiateUITextTitleValuePair("Bullet Speed:", 0.7, 0.625 + X_MARGIN, 0.6 - Y_MARGIN - ROW_SPACING_TEXT * 4, TEXT_COLOR_WHITE, equippedGun.getPrimaryBaseBulletSpeed(), 0.35, TEXT_COLOR_YELLOW, UIGroupKey.INVENTORY, "GUN_STATS_");
            Vector3d secondaryStatColor = TEXT_COLOR_YELLOW;
            if (!equippedGun.isSecondaryAttack()) {
                secondaryStatColor = TEXT_COLOR_GRAY;
            }
            instantiateUIText("Secondary:", 0.8, 0.625 + X_MARGIN, 0.6 - Y_MARGIN - ROW_SPACING_TEXT * 5, TEXT_COLOR_WHITE, UIGroupKey.INVENTORY, "GUN_STATS_");
            instantiateUITextTitleValuePair("Attack Damage:", 0.7, 0.625 + X_MARGIN, 0.6 - Y_MARGIN - ROW_SPACING_TEXT * 6, TEXT_COLOR_WHITE, equippedGun.getSecondaryBaseDamage(), 0.35, secondaryStatColor, UIGroupKey.INVENTORY, "GUN_STATS_");
            instantiateUITextTitleValuePair("Attack Speed:", 0.7, 0.625 + X_MARGIN, 0.6 - Y_MARGIN - ROW_SPACING_TEXT * 7, TEXT_COLOR_WHITE, equippedGun.getSecondaryBaseAttackSpeed(), 0.35, secondaryStatColor, UIGroupKey.INVENTORY, "GUN_STATS_");
            instantiateUITextTitleValuePair("Bullet Speed:", 0.7, 0.625 + X_MARGIN, 0.6 - Y_MARGIN - ROW_SPACING_TEXT * 8, TEXT_COLOR_WHITE, equippedGun.getSecondaryBaseBulletSpeed(), 0.35, secondaryStatColor, UIGroupKey.INVENTORY, "GUN_STATS_");
        }
    }

    private static void updateGunDisplayInventory(StatComponent statComponent) {
        UIHandler.getInstance().removeAllObjectsWithPrefix("GUN_INVENTORY_");
        GunComponent equippedGun = statComponent.getEquippedGun();
        if (equippedGun != null) {
            displayEquipedGun(equippedGun);
            displayModSlots(statComponent);
        }
        if (!statComponent.getGuns().isEmpty()) {
            List<GunComponent> altGuns = statComponent.getGuns().stream().filter(gun -> !gun.equals(equippedGun)).collect(Collectors.toList());
            for (int i = 0; i < altGuns.size(); i++) {
                GunComponent gunComponent = altGuns.get(i);
                UIComponentElement gunIconSprite = new UIComponentElement(-0.6 + X_MARGIN,
                        0.35 - (0.15 * i),
                        0.125,
                        0.125,
                        3,
                        gunComponent.getGunSprite());
                gunIconSprite.setComponent(gunComponent);
                gunIconSprite.setComponentClass(GunComponent.class);
                UIElement gunIconBackdrop = new UIElement(-0.6 + X_MARGIN,
                        0.35 - (0.15 * i),
                        0.125,
                        0.125,
                        2,
                        "usable_backdrop");
                gunIconSprite.setUiGroupKey(UIGroupKey.INVENTORY);
                gunIconBackdrop.setUiGroupKey(UIGroupKey.INVENTORY);
                UIHandler.getInstance().addObject("GUN_INVENTORY_GUN_ICON_" + RandomStringUtils.randomAlphanumeric(6), gunIconSprite);
                UIHandler.getInstance().addObject("GUN_INVENTORY_GUN_ICON_BACKDROP_" + RandomStringUtils.randomAlphanumeric(6), gunIconBackdrop);
            }
        }
    }

    private static void displayModSlots(StatComponent statComponent) {
        UIComponentElement modSlotPrimA = new UIComponentElement(0.0,
                0.3,
                0.2,
                0.2,
                3,
                statComponent.getEquippedGun().getPrimaryModifierSlotA() == null
                        ? (statComponent.getEquippedGun().isPrimaryModifierSlotAAvailable()
                        ? MOD_SLOT_AVAILABLE_TEXTURE
                        : MOD_SLOT_BLOCKED_TEXTURE)
                        : statComponent.getEquippedGun().getPrimaryModifierSlotA().getUpgradeIcon());
        modSlotPrimA.setUiGroupKey(UIGroupKey.INVENTORY);
        modSlotPrimA.setComponent(statComponent.getEquippedGun().getPrimaryModifierSlotA());
        modSlotPrimA.setComponentClass(UpgradeComponent.class);
        String key = statComponent.getEquippedGun().isPrimaryModifierSlotAAvailable()
                ? "GUN_INVENTORY_MOD_SLOT_PRIM_A_"
                : "GUN_INVENTORY_BLOCKED_SLOT_";
        key += RandomStringUtils.randomAlphanumeric(6);
        UIHandler.getInstance().addObject(key, modSlotPrimA);

        UIComponentElement modSlotPrimB = new UIComponentElement(0.3,
                0.3,
                0.2,
                0.2,
                3,
                statComponent.getEquippedGun().getPrimaryModifierSlotB() == null
                        ? (statComponent.getEquippedGun().isPrimaryModifierSlotBAvailable()
                        ? MOD_SLOT_AVAILABLE_TEXTURE
                        : MOD_SLOT_BLOCKED_TEXTURE)
                        : statComponent.getEquippedGun().getPrimaryModifierSlotB().getUpgradeIcon());
        modSlotPrimB.setUiGroupKey(UIGroupKey.INVENTORY);
        modSlotPrimB.setComponent(statComponent.getEquippedGun().getPrimaryModifierSlotB());
        modSlotPrimB.setComponentClass(UpgradeComponent.class);
        key = statComponent.getEquippedGun().isPrimaryModifierSlotBAvailable()
                ? "GUN_INVENTORY_MOD_SLOT_PRIM_B_"
                : "GUN_INVENTORY_BLOCKED_SLOT_";
        key += RandomStringUtils.randomAlphanumeric(6);
        UIHandler.getInstance().addObject(key, modSlotPrimB);

        UIComponentElement modSlotSecA = new UIComponentElement(0.0,
                -0.15,
                0.2,
                0.2,
                3,
                statComponent.getEquippedGun().getSecondaryModifierSlotA() == null
                        ? (statComponent.getEquippedGun().isSecondaryModifierSlotAAvailable()
                        ? MOD_SLOT_AVAILABLE_TEXTURE
                        : MOD_SLOT_BLOCKED_TEXTURE)
                        : statComponent.getEquippedGun().getSecondaryModifierSlotA().getUpgradeIcon());
        modSlotSecA.setUiGroupKey(UIGroupKey.INVENTORY);
        modSlotSecA.setComponent(statComponent.getEquippedGun().getSecondaryModifierSlotA());
        modSlotSecA.setComponentClass(UpgradeComponent.class);
        key = statComponent.getEquippedGun().isSecondaryModifierSlotAAvailable()
                ? "GUN_INVENTORY_MOD_SLOT_SEC_A_"
                : "GUN_INVENTORY_BLOCKED_SLOT_";
        key += RandomStringUtils.randomAlphanumeric(6);
        UIHandler.getInstance().addObject(key, modSlotSecA);

        UIComponentElement modSlotSecB = new UIComponentElement(0.3,
                -0.15,
                0.2,
                0.2,
                3,
                statComponent.getEquippedGun().getSecondaryModifierSlotB() == null
                        ? (statComponent.getEquippedGun().isSecondaryModifierSlotBAvailable()
                        ? MOD_SLOT_AVAILABLE_TEXTURE
                        : MOD_SLOT_BLOCKED_TEXTURE)
                        : statComponent.getEquippedGun().getSecondaryModifierSlotB().getUpgradeIcon());
        modSlotSecB.setUiGroupKey(UIGroupKey.INVENTORY);
        modSlotSecB.setComponent(statComponent.getEquippedGun().getSecondaryModifierSlotB());
        modSlotSecB.setComponentClass(UpgradeComponent.class);
        key = statComponent.getEquippedGun().isSecondaryModifierSlotBAvailable()
                ? "GUN_INVENTORY_MOD_SLOT_SEC_B_"
                : "GUN_INVENTORY_BLOCKED_SLOT_";
        key += RandomStringUtils.randomAlphanumeric(6);
        UIHandler.getInstance().addObject(key, modSlotSecB);
    }

    private static void displayEquipedGun(GunComponent equippedGun) {
        UIText gunTitle = new UIText(equippedGun.getGunName(),
                -0.6 + X_MARGIN,
                0.6 - Y_MARGIN,
                1,
                1,
                1);
        gunTitle.setUiGroupKey(UIGroupKey.INVENTORY);
        gunTitle.setLayer(2);
        gunTitle.setColor(TEXT_COLOR_YELLOW);
        UIHandler.getInstance().addObject("GUN_INVENTORY_" + RandomStringUtils.randomAlphanumeric(6), gunTitle);

        UIElement gunSprite = new UIElement(-0.3,
                -0.1,
                0.6,
                0.6,
                2,
                equippedGun.getGunSprite());
        gunSprite.setUiGroupKey(UIGroupKey.INVENTORY);
        UIHandler.getInstance().addObject("GUN_INVENTORY_" + RandomStringUtils.randomAlphanumeric(6), gunSprite);
    }

    private static void displayUpgradeIcons(StatComponent statComponent, double startX, double startY, double offsetY, double offsetX, String uiElementKeyPrefix, String targetUpgradeCategory, String backdropTextureKey, int itemsPerRow, boolean displayDistinct) {
        UIHandler.getInstance().removeAllObjectsWithPrefix(uiElementKeyPrefix);
        List<UpgradeComponent> upgrades;
        List<UpgradeComponent> upgradesOriginal = statComponent.getUpgrades()
                .stream()
                .filter(upgrade -> upgrade.getUpgradeCategory().equals(targetUpgradeCategory))
                .sorted(UpgradeComponent::compareTo)
                .collect(Collectors.toList());
        if (displayDistinct) {
            upgrades = upgradesOriginal.stream().distinct().collect(Collectors.toList());
        } else {
            upgrades = upgradesOriginal;
        }
        for (int i = 0; i < upgrades.size(); i++) {
            UpgradeComponent upgradeComponent = upgrades.get(i);
            double x = startX + (offsetX * (i % itemsPerRow));
            double y = startY - (offsetY * (Math.floorDiv(i, itemsPerRow)));
            UIComponentElement upgradeIcon = new UIComponentElement(x,
                    y,
                    0.1,
                    0.1,
                    3,
                    upgradeComponent.getUpgradeIcon());
            UIElement upgradeIconBackdrop = new UIElement(x,
                    y,
                    0.1,
                    0.1,
                    2,
                    backdropTextureKey);
            upgradeIcon.setUiGroupKey(UIGroupKey.INVENTORY);
            upgradeIconBackdrop.setUiGroupKey(UIGroupKey.INVENTORY);
            upgradeIcon.setComponent(upgradeComponent);
            upgradeIcon.setComponentClass(UpgradeComponent.class);
            UIHandler.getInstance().addObject(uiElementKeyPrefix + RandomStringUtils.randomAlphanumeric(6), upgradeIcon);
            UIHandler.getInstance().addObject(uiElementKeyPrefix + "BACKDROP_" + RandomStringUtils.randomAlphanumeric(6), upgradeIconBackdrop);
            long upgradeCount = upgradesOriginal.stream().filter(upgrade -> upgrade.equals(upgradeComponent)).count();
            if (upgradeCount > 1 && displayDistinct) {
                UIText upgradeCountText = new UIText("x" + upgradeCount,
                        x + 0.05,
                        y + 0.05,
                        0.1,
                        0.1,
                        0.75);
                upgradeCountText.setColor(TEXT_COLOR_YELLOW);
                upgradeCountText.setLayer(3);
                upgradeCountText.setUiGroupKey(UIGroupKey.STATS);
                UIHandler.getInstance().addObject(uiElementKeyPrefix + "_COUNT_" + RandomStringUtils.randomAlphanumeric(6), upgradeCountText);
            }
        }
    }

    private void updatePlayerHUD() {
        Entity player = EntityHandler.getInstance().getEntityWithComponent(PlayerTag.class);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        decimalFormat.setRoundingMode(RoundingMode.UP);
        if (player != null && player.getComponentOfType(StatComponent.class) != null) {

            StatComponent statComponent = player.getComponentOfType(StatComponent.class);
            GunComponent equipedGunComponent = player.getComponentOfType(StatComponent.class).getEquippedGun();
            this.levelIndicator.setText("Lvl. " + statComponent.getLevel());

            double healthPercent = statComponent.getHealthPercentage();
            this.healthBar.setWidth(DEFAULT_BAR_WIDTH * healthPercent);

            double armorPercent = statComponent.getShieldPercentage();
            this.shieldBar.setWidth(DEFAULT_BAR_WIDTH * armorPercent);

            double xpPercent = statComponent.getXPPercentage();
            this.xpBar.setWidth(DEFAULT_BAR_WIDTH * xpPercent);

            if (statComponent.getEquippedGun() != null) {
                if (statComponent.getEquippedGun().isPrimaryAttack()) {
                    double attackSpeedCooldown = Math.max(Math.min((System.nanoTime() - statComponent.getLastAttackPrimary())
                            / ((equipedGunComponent.getPrimaryBaseAttackSpeed() * statComponent.getAttackSpeedPrimary()) * SECONDS_TO_NANOSECONDS_FACTOR), 1.0), 0);
                    this.primaryAttackCooldownBar.setWidth(DEFAULT_BAR_WIDTH * attackSpeedCooldown);
                    this.primaryAttackCooldownBar.setTextureKey("attack_speed_bar");
                } else {
                    this.primaryAttackCooldownBar.setTextureKey("attack_speed_bar_locked");
                }
                if (statComponent.getEquippedGun().isSecondaryAttack()) {
                    double attackSpeedCooldown = Math.max(Math.min((System.nanoTime() - statComponent.getLastAttackSecondary())
                            / ((equipedGunComponent.getSecondaryBaseAttackSpeed() * statComponent.getAttackSpeedSecondary()) * SECONDS_TO_NANOSECONDS_FACTOR), 1.0), 0);
                    this.secondaryAttackCooldownBar.setWidth(DEFAULT_BAR_WIDTH * attackSpeedCooldown);
                    this.secondaryAttackCooldownBar.setTextureKey("attack_speed_bar");
                } else {
                    this.secondaryAttackCooldownBar.setTextureKey("attack_speed_bar_locked");
                }
            } else {
                this.primaryAttackCooldownBar.setTextureKey("attack_speed_bar_locked");
                this.secondaryAttackCooldownBar.setTextureKey("attack_speed_bar_locked");
            }

            double dashCooldown = Math.max(Math.min((System.nanoTime() - statComponent.getLastDashed())
                    / (statComponent.getDashCooldownSpeed() * SECONDS_TO_NANOSECONDS_FACTOR), 1.0), 0);
            this.dashCooldownBar.setWidth(DEFAULT_BAR_WIDTH * dashCooldown);
        }
    }

    private void updateBossHealthbar() {
        Entity boss = EntityHandler.getInstance().getEntityWithComponent(BossComponent.class);
        if (boss != null) {
            double healthPercent = boss.getComponentOfType(StatComponent.class).getHealthPercentage();
            this.bossHealthBar.setWidth(DEFAULT_BAR_WIDTH * healthPercent);
        } else {
            this.bossHealthBar.setWidth(0);
        }
    }

    private void updateMouseOver() {
        UIHandler.getInstance().removeAllObjectsWithPrefix("TOOLTIP_");
        //for (Entity tooltipEntity : EntityHandler.getInstance().getAllEntitiesWithComponents(TooltipComponent.class)) {
        //    updateTooltipPopup(tooltipEntity);
        //}
        for (Entity item : EntityHandler.getInstance().getAllEntitiesWithComponents(ItemTag.class)) {
            updateItemPopup(item);
        }
        for (UIElement uiElement : UIHandler.getInstance().getAllObjects()
                .stream()
                .filter(uiElement -> UISceneService.getInstance().getVisibleUIGroups().contains(uiElement.getUiGroupKey()))
                .collect(Collectors.toList())) {
            updateUIElementPopup(uiElement);
            updateUIComponentElementSelection(uiElement);
        }
    }

    private void updateUIComponentElementSelection(UIElement uiElement) {
        if (CollisionUtil.checkInside(
                MouseHandler.getInstance().getMousePositionClipSpace(),
                uiElement.getX(),
                uiElement.getY(),
                uiElement.getX() + uiElement.getWidth(),
                uiElement.getY() + uiElement.getHeight())) {
            StatComponent statComponent = EntityHandler.getInstance()
                    .getEntityWithComponent(PlayerTag.class)
                    .getComponentOfType(StatComponent.class);
            if (uiElement instanceof UIComponentElement
                    && !uiElement.getElementKey().contains("GUN_INVENTORY_MOD_SLOT_")
                    && ((UIComponentElement) uiElement).getComponent() != null
                    && ((UIComponentElement) uiElement).getComponentClass().equals(UpgradeComponent.class)
                    && ((UpgradeComponent) ((UIComponentElement) uiElement).getComponent()).getUpgradeCategory().equals("bulletModifier")
                    && this.selectedUpgradeComponent == null
                    && MouseHandler.getInstance().isKeyForActionPressed("mouseButtonPrimary", true)) {
                this.selectedUpgradeComponent = (UpgradeComponent) ((UIComponentElement) uiElement).getComponent();
                statComponent.removeUpgrade(this.selectedUpgradeComponent);
            }
            if (uiElement instanceof UIComponentElement
                    && !uiElement.getElementKey().contains("GUN_INVENTORY_MOD_SLOT_")
                    && ((UIComponentElement) uiElement).getComponent() != null
                    && ((UIComponentElement) uiElement).getComponentClass().equals(GunComponent.class)
                    && this.selectedUpgradeComponent == null
                    && MouseHandler.getInstance().isKeyForActionPressed("mouseButtonPrimary", true)) {
                statComponent.unequipGunUpgrades();
                statComponent.setEquippedGun(((GunComponent) ((UIComponentElement) uiElement).getComponent()));
                EntityHandler.getInstance()
                        .getEntityWithId("GUN")
                        .getComponentOfType(RenderComponent.class)
                        .setTextureKey(statComponent.getEquippedGun().getGunSprite());
            }
            if (uiElement.getElementKey().contains("BULLET_MOD_INVENTORY")
                    && this.selectedUpgradeComponent != null
                    && MouseHandler.getInstance().isKeyForActionPressed("mouseButtonPrimary", true)) {
                statComponent.addUpgrade(this.selectedUpgradeComponent);
                this.selectedUpgradeComponent = null;
            }
            if (this.selectedUpgradeComponent != null
                    && MouseHandler.getInstance().isKeyForActionPressed("mouseButtonSecondary", true)) {
                statComponent.addUpgrade(this.selectedUpgradeComponent);
                this.selectedUpgradeComponent = null;
            }
            if (uiElement.getElementKey().contains("GUN_INVENTORY_MOD_SLOT_")
                    && MouseHandler.getInstance().isKeyForActionPressed("mouseButtonSecondary", true)) {
                if (uiElement.getElementKey().contains("_PRIM_A_")) {
                    UpgradeComponent currentlyEquipped = statComponent.getEquippedGun().getPrimaryModifierSlotA();
                    statComponent.getEquippedGun().setPrimaryModifierSlotA(null);
                    statComponent.addUpgrade(currentlyEquipped);
                } else if (uiElement.getElementKey().contains("_PRIM_B_")) {
                    UpgradeComponent currentlyEquipped = statComponent.getEquippedGun().getPrimaryModifierSlotB();
                    statComponent.getEquippedGun().setPrimaryModifierSlotB(null);
                    statComponent.addUpgrade(currentlyEquipped);
                } else if (uiElement.getElementKey().contains("_SEC_A_")) {
                    UpgradeComponent currentlyEquipped = statComponent.getEquippedGun().getSecondaryModifierSlotA();
                    statComponent.getEquippedGun().setSecondaryModifierSlotA(null);
                    statComponent.addUpgrade(currentlyEquipped);
                } else if (uiElement.getElementKey().contains("_SEC_B_")) {
                    UpgradeComponent currentlyEquipped = statComponent.getEquippedGun().getSecondaryModifierSlotB();
                    statComponent.getEquippedGun().setSecondaryModifierSlotB(null);
                    statComponent.addUpgrade(currentlyEquipped);
                }
            }
            if (uiElement.getElementKey().contains("GUN_INVENTORY_MOD_SLOT_")
                    && MouseHandler.getInstance().isKeyForActionPressed("mouseButtonPrimary", true)) {
                if (uiElement.getElementKey().contains("_PRIM_A_")
                        && (this.selectedUpgradeComponent == null
                        || !Slot.SECONDARY.getKey().equals(this.selectedUpgradeComponent.getUpgradeSlot()))) {
                    UpgradeComponent currentlyEquipped = statComponent.getEquippedGun().getPrimaryModifierSlotA();
                    statComponent.getEquippedGun().setPrimaryModifierSlotA(this.selectedUpgradeComponent);
                    this.selectedUpgradeComponent = currentlyEquipped;
                    createSoundEntity("upgrade");
                } else if (uiElement.getElementKey().contains("_PRIM_B_")
                        && (this.selectedUpgradeComponent == null
                        || !Slot.SECONDARY.getKey().equals(this.selectedUpgradeComponent.getUpgradeSlot()))) {
                    UpgradeComponent currentlyEquipped = statComponent.getEquippedGun().getPrimaryModifierSlotB();
                    statComponent.getEquippedGun().setPrimaryModifierSlotB(this.selectedUpgradeComponent);
                    this.selectedUpgradeComponent = currentlyEquipped;
                    createSoundEntity("upgrade");
                } else if (uiElement.getElementKey().contains("_SEC_A_")) {
                    UpgradeComponent currentlyEquipped = statComponent.getEquippedGun().getSecondaryModifierSlotA();
                    statComponent.getEquippedGun().setSecondaryModifierSlotA(this.selectedUpgradeComponent);
                    this.selectedUpgradeComponent = currentlyEquipped;
                    createSoundEntity("upgrade");
                } else if (uiElement.getElementKey().contains("_SEC_B_")) {
                    UpgradeComponent currentlyEquipped = statComponent.getEquippedGun().getSecondaryModifierSlotB();
                    statComponent.getEquippedGun().setSecondaryModifierSlotB(this.selectedUpgradeComponent);
                    this.selectedUpgradeComponent = currentlyEquipped;
                    createSoundEntity("upgrade");
                }
            }
        }
    }

    private void updateMouseSelection() {
        UIHandler.getInstance().removeAllObjectsWithPrefix("MOUSE_SELECTION_");
        if (this.selectedUpgradeComponent != null) {
            Vector2d mousePositionClipSpace = MouseHandler.getInstance().getMousePositionClipSpace();
            UIElement mouseSelection = new UIElement(mousePositionClipSpace.x() - 0.05,
                    mousePositionClipSpace.y() - 0.05,
                    0.1,
                    0.1,
                    5,
                    this.selectedUpgradeComponent.getUpgradeIcon());
            UIHandler.getInstance().addObject("MOUSE_SELECTION_" + RandomStringUtils.randomAlphanumeric(6), mouseSelection);
        }
    }

    private static void updateTooltipPopup(Entity tooltipEntity) {
        TransformationComponent transformationComponent = tooltipEntity.getComponentOfType(TransformationComponent.class);
        if (CollisionUtil.checkInside(MouseHandler.getInstance().getMousePositionWorldSpace(),
                tooltipEntity.getComponentOfType(CollisionComponent.class).getHitBox(),
                transformationComponent.getPosition())) {
            Vector2d positionClipspace = CoordinateConverter.transformWorldSpaceToClipSpace(transformationComponent.getPosition());
            UIGroup uiGroup = UIUtil.getTooltip(tooltipEntity.getComponentOfType(TooltipComponent.class), positionClipspace);
            UIUtil.addUIGroupToUIHandler(uiGroup, "TOOLTIP_");
        }
    }

    private static void updateItemPopup(Entity item) {
        TransformationComponent transformationComponent = item.getComponentOfType(TransformationComponent.class);
        if (CollisionUtil.checkInside(MouseHandler.getInstance().getMousePositionWorldSpace(),
                item.getComponentOfType(CollisionComponent.class).getHitBox(),
                transformationComponent.getPosition())
                && !UISceneService.getInstance().isInventoryVisible()) {
            UIGroup tooltipUIGroup = null;
            if (item.hasComponentOfType(UpgradeComponent.class)) {
                UpgradeComponent upgradeComponent = item.getComponentOfType(UpgradeComponent.class);
                Vector2d positionClipspace = CoordinateConverter.transformWorldSpaceToClipSpace(transformationComponent.getPosition());
                tooltipUIGroup = UIUtil.getUpgradeComponentTooltip(upgradeComponent, positionClipspace, 6);
            } else if (item.hasComponentOfType(GunComponent.class)) {
                GunComponent gunComponent = item.getComponentOfType(GunComponent.class);
                Vector2d positionClipspace = CoordinateConverter.transformWorldSpaceToClipSpace(transformationComponent.getPosition());
                tooltipUIGroup = UIUtil.getGunComponentTooltip(gunComponent, positionClipspace, 6);
            }
            if (tooltipUIGroup != null) {
                UIUtil.addUIGroupToUIHandler(tooltipUIGroup, "TOOLTIP_");
            }
        }
    }

    private void updateUIElementPopup(UIElement uiElement) {
        if (CollisionUtil.checkInside(
                MouseHandler.getInstance().getMousePositionClipSpace(),
                uiElement.getX(),
                uiElement.getY(),
                uiElement.getX() + uiElement.getWidth(),
                uiElement.getY() + uiElement.getHeight())) {
            if (uiElement instanceof UIComponentElement
                    && ((UIComponentElement) uiElement).getComponent() != null) {
                if (((UIComponentElement) uiElement).getComponentClass().equals(UpgradeComponent.class)) {
                    UIGroup upgradeTooltipGroup = UIUtil.getUpgradeComponentTooltip((UpgradeComponent) ((UIComponentElement) uiElement).getComponent(),
                            new Vector2d(uiElement.getX(), uiElement.getY()), 6);
                    UIUtil.addUIGroupToUIHandler(upgradeTooltipGroup, "TOOLTIP_");
                }
                if (((UIComponentElement) uiElement).getComponentClass().equals(GunComponent.class)) {
                    UIGroup gunComponentTooltip = UIUtil.getGunComponentTooltip((GunComponent) ((UIComponentElement) uiElement).getComponent(),
                            new Vector2d(uiElement.getX(), uiElement.getY()), 6);
                    UIUtil.addUIGroupToUIHandler(gunComponentTooltip, "TOOLTIP_");
                }
            }
        }
    }
}
