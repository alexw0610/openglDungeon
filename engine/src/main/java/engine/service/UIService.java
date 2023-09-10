package engine.service;

import engine.component.*;
import engine.component.base.CollisionComponent;
import engine.component.base.TransformationComponent;
import engine.component.tag.ItemTag;
import engine.component.tag.PlayerTag;
import engine.entity.Entity;
import engine.enums.UIGroupKey;
import engine.handler.EntityHandler;
import engine.handler.MouseHandler;
import engine.handler.UIHandler;
import engine.handler.UIStateHandler;
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

public class UIService {

    private static final double DEFAULT_BAR_WIDTH = 7.11 * 0.1;
    private static final double STAT_UI_FONT_SCALE = 0.70;
    private static final String MOD_SLOT_AVAILABLE_TEXTURE = "modifier_slot_available";
    private static final String MOD_SLOT_BLOCKED_TEXTURE = "modifier_slot_blocked";
    private static final String UI_BACKGROUND_TEXTURE = "uibg";
    private static UIService INSTANCE;

    private UIElement healthbar;
    private UIElement armorbar;
    private UIElement xpbar;
    private UIElement attackSpeedBar;
    private UIElement bossHealthBar;

    private UIText healthStat;
    private UIText armorStat;
    private UIText attackSpeedStat;
    private UIText moveSpeedStat;
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
    }

    public void updateUI() {
        updateHUD();
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
        UIElement healthbarBorder = new UIElement(-DEFAULT_BAR_WIDTH - 0.1, 0.85, DEFAULT_BAR_WIDTH, 0.1, 3, "healthBarBorder");
        healthbarBorder.setUiGroupKey(UIGroupKey.HUD);
        UIHandler.getInstance().addObject(healthbarBorder);

        this.healthbar = new UIElement(-DEFAULT_BAR_WIDTH - 0.1, 0.85, DEFAULT_BAR_WIDTH, 0.1, 1, "healthbar");
        this.healthbar.setUiGroupKey(UIGroupKey.HUD);
        UIHandler.getInstance().addObject(healthbar);

        this.armorbar = new UIElement(-DEFAULT_BAR_WIDTH - 0.1, 0.85, DEFAULT_BAR_WIDTH, 0.1, 2, "armorbar");
        this.armorbar.setUiGroupKey(UIGroupKey.HUD);
        UIHandler.getInstance().addObject(armorbar);

        this.levelIndicator = new UIText("Lvl. 1", -0.06, 0.95, 1, 1, 0.85);
        this.levelIndicator.setUiGroupKey(UIGroupKey.HUD);
        UIHandler.getInstance().addObject(levelIndicator);

        UIElement xpBarBorder = new UIElement(0.1, 0.85, DEFAULT_BAR_WIDTH, 0.1, 2, "xpBarBorder");
        xpBarBorder.setUiGroupKey(UIGroupKey.HUD);
        UIHandler.getInstance().addObject(xpBarBorder);

        this.xpbar = new UIElement(0.1, 0.85, DEFAULT_BAR_WIDTH, 0.1, 1, "xpbar");
        this.xpbar.setUiGroupKey(UIGroupKey.HUD);
        UIHandler.getInstance().addObject(xpbar);

        this.attackSpeedBar = new UIElement(-DEFAULT_BAR_WIDTH / 2.0, 0.75, DEFAULT_BAR_WIDTH, 0.05, 1, "attack_speed_bar");
        this.attackSpeedBar.setUiGroupKey(UIGroupKey.HUD);
        UIHandler.getInstance().addObject(attackSpeedBar);

        this.bossHealthBar = new UIElement(-DEFAULT_BAR_WIDTH / 2.0, -0.90, DEFAULT_BAR_WIDTH, 0.1, 1, "healthbar");
        this.bossHealthBar.setUiGroupKey(UIGroupKey.HUD);
        UIHandler.getInstance().addObject(bossHealthBar);

    }

    private void initStatUI() {
        double rowDistance = 0.06;
        double xBaseOffset = 0.62;
        double yBaseOffset = 0.91;

        UIElement statSummary = new UIElement(-DEFAULT_BAR_WIDTH - 0.65, 0.65, 0.5, 0.29, 1, UI_BACKGROUND_TEXTURE);
        statSummary.setUiGroupKey(UIGroupKey.STATS);
        UIHandler.getInstance().addObject(statSummary);

        UIText health = new UIText("Health:", -DEFAULT_BAR_WIDTH - xBaseOffset, yBaseOffset, 0.6, 0.1, STAT_UI_FONT_SCALE);
        health.setLayer(2);
        health.setUiGroupKey(UIGroupKey.STATS);
        UIHandler.getInstance().addObject(health);

        this.healthStat = new UIText("0", -DEFAULT_BAR_WIDTH - xBaseOffset + 0.155, yBaseOffset, 0.6, 0.1, STAT_UI_FONT_SCALE);
        this.healthStat.setLayer(2);
        this.healthStat.setColor(STAT_VALUE_COLOR);
        this.healthStat.setUiGroupKey(UIGroupKey.STATS);
        UIHandler.getInstance().addObject(this.healthStat);

        UIText armor = new UIText("Armor:", -DEFAULT_BAR_WIDTH - xBaseOffset, yBaseOffset - rowDistance, 0.6, 0.1, STAT_UI_FONT_SCALE);
        armor.setLayer(2);
        armor.setUiGroupKey(UIGroupKey.STATS);
        UIHandler.getInstance().addObject(armor);

        this.armorStat = new UIText("0", -DEFAULT_BAR_WIDTH - xBaseOffset + 0.155, yBaseOffset - rowDistance, 0.6, 0.1, STAT_UI_FONT_SCALE);
        this.armorStat.setLayer(2);
        this.armorStat.setColor(STAT_VALUE_COLOR);
        this.armorStat.setUiGroupKey(UIGroupKey.STATS);
        UIHandler.getInstance().addObject(this.armorStat);

        UIText attackSpeed = new UIText("Attack speed:", -DEFAULT_BAR_WIDTH - xBaseOffset, yBaseOffset - rowDistance * 2, 0.6, 0.1, STAT_UI_FONT_SCALE);
        attackSpeed.setLayer(2);
        attackSpeed.setUiGroupKey(UIGroupKey.STATS);
        UIHandler.getInstance().addObject(attackSpeed);

        this.attackSpeedStat = new UIText("0", -DEFAULT_BAR_WIDTH - xBaseOffset + 0.3, yBaseOffset - rowDistance * 2, 0.6, 0.1, STAT_UI_FONT_SCALE);
        this.attackSpeedStat.setLayer(2);
        this.attackSpeedStat.setColor(STAT_VALUE_COLOR);
        this.attackSpeedStat.setUiGroupKey(UIGroupKey.STATS);
        UIHandler.getInstance().addObject(this.attackSpeedStat);

        UIText moveSpeed = new UIText("Move speed:", -DEFAULT_BAR_WIDTH - xBaseOffset, yBaseOffset - rowDistance * 3, 0.6, 0.1, STAT_UI_FONT_SCALE);
        moveSpeed.setLayer(2);
        moveSpeed.setUiGroupKey(UIGroupKey.STATS);
        UIHandler.getInstance().addObject(moveSpeed);

        this.moveSpeedStat = new UIText("0", -DEFAULT_BAR_WIDTH - xBaseOffset + 0.3, yBaseOffset - rowDistance * 3, 0.6, 0.1, STAT_UI_FONT_SCALE);
        this.moveSpeedStat.setLayer(2);
        this.moveSpeedStat.setColor(STAT_VALUE_COLOR);
        this.moveSpeedStat.setUiGroupKey(UIGroupKey.STATS);
        UIHandler.getInstance().addObject(this.moveSpeedStat);

    }

    private void initStatUpgradeUI() {
        UIElement statUpgradeBackground = new UIElement(-DEFAULT_BAR_WIDTH - 0.65, 0.28, 0.5, 0.35, 1, UI_BACKGROUND_TEXTURE);
        statUpgradeBackground.setUiGroupKey(UIGroupKey.STATS);
        UIHandler.getInstance().addObject(statUpgradeBackground);
    }

    private void initGunUpgradeUI() {
        UIElement gunBackground = new UIElement(-0.6, -0.2, 1.2, 0.8, 1, UI_BACKGROUND_TEXTURE);
        gunBackground.setUiGroupKey(UIGroupKey.INVENTORY);
        UIHandler.getInstance().addObject(gunBackground);

        UIElement gunStatBackground = new UIElement(0.625, -0.2, 0.55, 0.8, 1, UI_BACKGROUND_TEXTURE);
        gunStatBackground.setUiGroupKey(UIGroupKey.INVENTORY);
        UIHandler.getInstance().addObject(gunStatBackground);

        UIElement bulletModifierBackground = new UIElement(-0.6, -0.5, 1.2, 0.275, 1, UI_BACKGROUND_TEXTURE);
        bulletModifierBackground.setUiGroupKey(UIGroupKey.INVENTORY);
        UIHandler.getInstance().addObject("BULLET_MOD_INVENTORY", bulletModifierBackground);
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
                    && ((UpgradeComponent) ((UIComponentElement) uiElement).getComponent()).getUpgradeCategory().equals("bulletModifier")
                    && uiElement.isVisible()
                    && this.selectedUpgradeComponent == null
                    && MouseHandler.getInstance().isKeyForActionPressed("mouseButtonPrimary", true)) {
                this.selectedUpgradeComponent = (UpgradeComponent) ((UIComponentElement) uiElement).getComponent();
                statComponent.removeUpgrade(this.selectedUpgradeComponent);
            }
            if (uiElement.getElementKey().contains("BULLET_MOD_INVENTORY")
                    && uiElement.isVisible()
                    && this.selectedUpgradeComponent != null
                    && MouseHandler.getInstance().isKeyForActionPressed("mouseButtonPrimary", true)) {
                statComponent.addUpgrade(this.selectedUpgradeComponent);
                this.selectedUpgradeComponent = null;
            }
            if (uiElement.getElementKey().contains("GUN_INVENTORY_MOD_SLOT_")
                    && uiElement.isVisible()
                    && MouseHandler.getInstance().isKeyForActionPressed("mouseButtonPrimary", true)) {
                if (uiElement.getElementKey().contains("_PRIM_A_")) {
                    UpgradeComponent currentlyEquipped = statComponent.getEquipedGun().getPrimaryModifierSlotA();
                    statComponent.getEquipedGun().setPrimaryModifierSlotA(this.selectedUpgradeComponent);
                    this.selectedUpgradeComponent = currentlyEquipped;
                } else if (uiElement.getElementKey().contains("_PRIM_B_")) {
                    UpgradeComponent currentlyEquipped = statComponent.getEquipedGun().getPrimaryModifierSlotB();
                    statComponent.getEquipedGun().setPrimaryModifierSlotB(this.selectedUpgradeComponent);
                    this.selectedUpgradeComponent = currentlyEquipped;
                } else if (uiElement.getElementKey().contains("_SEC_A_")) {
                    UpgradeComponent currentlyEquipped = statComponent.getEquipedGun().getSecondaryModifierSlotA();
                    statComponent.getEquipedGun().setSecondaryModifierSlotA(this.selectedUpgradeComponent);
                    this.selectedUpgradeComponent = currentlyEquipped;
                } else if (uiElement.getElementKey().contains("_SEC_B_")) {
                    UpgradeComponent currentlyEquipped = statComponent.getEquipedGun().getSecondaryModifierSlotB();
                    statComponent.getEquipedGun().setSecondaryModifierSlotB(this.selectedUpgradeComponent);
                    this.selectedUpgradeComponent = currentlyEquipped;
                }
            }
        }
    }

    private static void instantiateUITextTitleValuePair(String title, double scale, double xTitle, double y, Vector3d textColorTitle, String value, double xValue, Vector3d textColorValue) {
        instantiateUIText(title, scale, xTitle, y, textColorTitle);
        instantiateUIText(value, scale, xValue, y, textColorValue);
    }

    private static void instantiateUIText(String text, double scale, double x, double y, Vector3d textColor) {
        UIText uiText = new UIText(text,
                x,
                y,
                1,
                1,
                scale);
        uiText.setUiGroupKey(UIGroupKey.INVENTORY);
        uiText.setLayer(2);
        uiText.setColor(textColor);
        UIHandler.getInstance().addObject("GUN_STATS_" + RandomStringUtils.randomAlphanumeric(6), uiText);
    }

    private void updateStatUpgradeUI() {
        UIHandler.getInstance().removeAllObjectsWithPrefix("STAT_UPGRADE_");
        StatComponent statComponent = EntityHandler.getInstance()
                .getEntityWithComponent(PlayerTag.class)
                .getComponentOfType(StatComponent.class);
        List<UpgradeComponent> upgrades = statComponent.getUpgrades();
        updateStatModifierInventory(upgrades);
    }

    private static void updateStatModifierInventory(List<UpgradeComponent> upgrades) {
        List<UpgradeComponent> upgradesDistinct = upgrades
                .stream()
                .filter(upgrade -> upgrade.getUpgradeCategory().equals("statModifier"))
                .distinct()
                .sorted(UpgradeComponent::compareTo)
                .collect(Collectors.toList());
        double offsetX = 0.095;
        double offsetY = 0.1;
        for (int i = 0; i < upgradesDistinct.size(); i++) {
            UpgradeComponent upgradeComponent = upgradesDistinct.get(i);
            UIComponentElement upgradeIcon = new UIComponentElement((-DEFAULT_BAR_WIDTH - 0.64) + (offsetX * (i % 5)),
                    0.51 - (offsetY * (Math.floorDiv(i, 5))),
                    0.1,
                    0.1,
                    2,
                    upgradeComponent.getUpgradeIcon());
            upgradeIcon.setUiGroupKey(UIGroupKey.STATS);
            upgradeIcon.setComponent(upgradeComponent);
            upgradeIcon.setComponentClass(UpgradeComponent.class);
            UIHandler.getInstance().addObject("STAT_UPGRADE_" + RandomStringUtils.randomAlphanumeric(6), upgradeIcon);
            long upgradeCount = upgrades.stream().filter(upgrade -> upgrade.equals(upgradeComponent)).count();
            if (upgradeCount > 1) {
                UIText upgradeCountText = new UIText("x" + upgradeCount, (-DEFAULT_BAR_WIDTH - 0.59) + (offsetX * (i % 5)),
                        0.55 - (offsetY * (Math.floorDiv(i, 5))),
                        0.1,
                        0.1,
                        0.75);
                upgradeCountText.setColor(TEXT_COLOR_YELLOW);
                upgradeCountText.setLayer(3);
                upgradeCountText.setUiGroupKey(UIGroupKey.STATS);
                UIHandler.getInstance().addObject("STAT_UPGRADE_" + RandomStringUtils.randomAlphanumeric(6), upgradeCountText);
            }
        }
    }

    private void updateGunUpgradeUI() {
        StatComponent statComponent = EntityHandler.getInstance()
                .getEntityWithComponent(PlayerTag.class)
                .getComponentOfType(StatComponent.class);
        updateGunDisplayInventory(statComponent);
        updateBulletModifierInventory(statComponent);
        updateGunStatsUI(statComponent);
    }

    private static void updateGunStatsUI(StatComponent statComponent) {
        UIHandler.getInstance().removeAllObjectsWithPrefix("GUN_STATS_");
        GunComponent equippedGun = statComponent.getEquipedGun();
        if (equippedGun != null) {
            instantiateUIText("Stats:", 1, 0.65, 0.58, TEXT_COLOR_YELLOW);
            double yOffset = 0.085;
            instantiateUIText("Primary:", 0.9, 0.65, 0.58 - yOffset * 1, TEXT_COLOR_WHITE);
            instantiateUITextTitleValuePair("Attack Damage:", 0.7, 0.65, 0.58 - yOffset * 2, TEXT_COLOR_WHITE, String.valueOf(equippedGun.getPrimaryBaseDamage()), 1.0, TEXT_COLOR_YELLOW);
            instantiateUITextTitleValuePair("Attack Speed:", 0.7, 0.65, 0.58 - yOffset * 3, TEXT_COLOR_WHITE, String.valueOf(equippedGun.getPrimaryBaseAttackSpeed()), 1.0, TEXT_COLOR_YELLOW);
            instantiateUITextTitleValuePair("Bullet Speed:", 0.7, 0.65, 0.58 - yOffset * 4, TEXT_COLOR_WHITE, String.valueOf(equippedGun.getPrimaryBulletSpeed()), 1.0, TEXT_COLOR_YELLOW);
            Vector3d secondaryStatColor = TEXT_COLOR_YELLOW;
            if (!equippedGun.isSecondaryAttack()) {
                secondaryStatColor = TEXT_COLOR_GRAY;
            }
            instantiateUIText("Secondary:", 0.9, 0.65, 0.58 - yOffset * 5, TEXT_COLOR_WHITE);
            instantiateUITextTitleValuePair("Attack Damage:", 0.7, 0.65, 0.58 - yOffset * 6, TEXT_COLOR_WHITE, String.valueOf(equippedGun.getSecondaryBaseDamage()), 1.0, secondaryStatColor);
            instantiateUITextTitleValuePair("Attack Speed:", 0.7, 0.65, 0.58 - yOffset * 7, TEXT_COLOR_WHITE, String.valueOf(equippedGun.getSecondaryBaseAttackSpeed()), 1.0, secondaryStatColor);
            instantiateUITextTitleValuePair("Bullet Speed:", 0.7, 0.65, 0.58 - yOffset * 8, TEXT_COLOR_WHITE, String.valueOf(equippedGun.getSecondaryBulletSpeed()), 1.0, secondaryStatColor);
        }
    }

    private static void updateGunDisplayInventory(StatComponent statComponent) {
        UIHandler.getInstance().removeAllObjectsWithPrefix("GUN_INVENTORY_");
        GunComponent equippedGun = statComponent.getEquipedGun();
        if (equippedGun != null) {
            UIText gunTitle = new UIText(equippedGun.getGunName(),
                    -0.58,
                    0.58,
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

            UIComponentElement modSlotPrimA = new UIComponentElement(0.0,
                    0.3,
                    0.2,
                    0.2,
                    3,
                    statComponent.getEquipedGun().getPrimaryModifierSlotA() == null
                            ? (statComponent.getEquipedGun().isPrimaryModifierSlotAAvailable()
                            ? MOD_SLOT_AVAILABLE_TEXTURE
                            : MOD_SLOT_BLOCKED_TEXTURE)
                            : statComponent.getEquipedGun().getPrimaryModifierSlotA().getUpgradeIcon());
            modSlotPrimA.setUiGroupKey(UIGroupKey.INVENTORY);
            modSlotPrimA.setComponent(statComponent.getEquipedGun().getPrimaryModifierSlotA());
            String key = statComponent.getEquipedGun().isPrimaryModifierSlotAAvailable()
                    ? "GUN_INVENTORY_MOD_SLOT_PRIM_A_"
                    : "GUN_INVENTORY_BLOCKED_SLOT_";
            key += RandomStringUtils.randomAlphanumeric(6);
            UIHandler.getInstance().addObject(key, modSlotPrimA);

            UIComponentElement modSlotPrimB = new UIComponentElement(0.3,
                    0.3,
                    0.2,
                    0.2,
                    3,
                    statComponent.getEquipedGun().getPrimaryModifierSlotB() == null
                            ? (statComponent.getEquipedGun().isPrimaryModifierSlotBAvailable()
                            ? MOD_SLOT_AVAILABLE_TEXTURE
                            : MOD_SLOT_BLOCKED_TEXTURE)
                            : statComponent.getEquipedGun().getPrimaryModifierSlotB().getUpgradeIcon());
            modSlotPrimB.setUiGroupKey(UIGroupKey.INVENTORY);
            modSlotPrimB.setComponent(statComponent.getEquipedGun().getPrimaryModifierSlotB());
            key = statComponent.getEquipedGun().isPrimaryModifierSlotBAvailable()
                    ? "GUN_INVENTORY_MOD_SLOT_PRIM_B_"
                    : "GUN_INVENTORY_BLOCKED_SLOT_";
            key += RandomStringUtils.randomAlphanumeric(6);
            UIHandler.getInstance().addObject(key, modSlotPrimB);

            UIComponentElement modSlotSecA = new UIComponentElement(0.0,
                    -0.15,
                    0.2,
                    0.2,
                    3,
                    statComponent.getEquipedGun().getSecondaryModifierSlotA() == null
                            ? (statComponent.getEquipedGun().isSecondaryModifierSlotAAvailable()
                            ? MOD_SLOT_AVAILABLE_TEXTURE
                            : MOD_SLOT_BLOCKED_TEXTURE)
                            : statComponent.getEquipedGun().getSecondaryModifierSlotA().getUpgradeIcon());
            modSlotSecA.setUiGroupKey(UIGroupKey.INVENTORY);
            modSlotSecA.setComponent(statComponent.getEquipedGun().getSecondaryModifierSlotA());
            key = statComponent.getEquipedGun().isSecondaryModifierSlotAAvailable()
                    ? "GUN_INVENTORY_MOD_SLOT_SEC_A_"
                    : "GUN_INVENTORY_BLOCKED_SLOT_";
            key += RandomStringUtils.randomAlphanumeric(6);
            UIHandler.getInstance().addObject(key, modSlotSecA);

            UIComponentElement modSlotSecB = new UIComponentElement(0.3,
                    -0.15,
                    0.2,
                    0.2,
                    3,
                    statComponent.getEquipedGun().getSecondaryModifierSlotB() == null
                            ? (statComponent.getEquipedGun().isSecondaryModifierSlotBAvailable()
                            ? MOD_SLOT_AVAILABLE_TEXTURE
                            : MOD_SLOT_BLOCKED_TEXTURE)
                            : statComponent.getEquipedGun().getSecondaryModifierSlotB().getUpgradeIcon());
            modSlotSecB.setUiGroupKey(UIGroupKey.INVENTORY);
            modSlotSecB.setComponent(statComponent.getEquipedGun().getSecondaryModifierSlotB());
            key = statComponent.getEquipedGun().isSecondaryModifierSlotBAvailable()
                    ? "GUN_INVENTORY_MOD_SLOT_SEC_B_"
                    : "GUN_INVENTORY_BLOCKED_SLOT_";
            key += RandomStringUtils.randomAlphanumeric(6);
            UIHandler.getInstance().addObject(key, modSlotSecB);
        }
    }

    private static void updateBulletModifierInventory(StatComponent statComponent) {
        UIHandler.getInstance().removeAllObjectsWithPrefix("GUN_UPGRADE_");
        List<UpgradeComponent> upgrades = statComponent.getUpgrades()
                .stream()
                .filter(upgrade -> upgrade.getUpgradeCategory().equals("bulletModifier"))
                .sorted(UpgradeComponent::compareTo)
                .collect(Collectors.toList());
        double offsetX = 0.095;
        double offsetY = 0.1;
        for (int i = 0; i < upgrades.size(); i++) {
            UpgradeComponent upgradeComponent = upgrades.get(i);
            int itemsPerRow = 8;
            UIComponentElement upgradeIcon = new UIComponentElement((-0.58) + (offsetX * (i % itemsPerRow)),
                    -0.35 - (offsetY * (Math.floorDiv(i, itemsPerRow))),
                    0.1,
                    0.1,
                    2,
                    upgradeComponent.getUpgradeIcon());
            upgradeIcon.setUiGroupKey(UIGroupKey.INVENTORY);
            upgradeIcon.setComponent(upgradeComponent);
            upgradeIcon.setComponentClass(UpgradeComponent.class);
            UIHandler.getInstance().addObject("GUN_UPGRADE_" + RandomStringUtils.randomAlphanumeric(6), upgradeIcon);
        }
    }

    private void updatePlayerHUD() {
        Entity player = EntityHandler.getInstance().getEntityWithComponent(PlayerTag.class);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        decimalFormat.setRoundingMode(RoundingMode.UP);
        if (player != null && player.getComponentOfType(StatComponent.class) != null) {

            StatComponent statComponent = player.getComponentOfType(StatComponent.class);
            this.levelIndicator.setText("Lvl. " + statComponent.getLevel());

            double healthPercent = statComponent.getHealthPercentage();
            this.healthbar.setWidth(DEFAULT_BAR_WIDTH * healthPercent);

            double armorPercent = statComponent.getArmorPercentage();
            this.armorbar.setWidth(DEFAULT_BAR_WIDTH * armorPercent);

            double xpPercent = statComponent.getXPPercentage();
            this.xpbar.setWidth(DEFAULT_BAR_WIDTH * xpPercent);

            if (statComponent.getEquipedGun() != null) {
                double attackSpeedCooldown = Math.max(Math.min((System.nanoTime() - statComponent.getLastShotPrimary())
                        / (statComponent.getAttackSpeedPrimary() * SECONDS_TO_NANOSECONDS_FACTOR), 1.0), 0);
                this.attackSpeedBar.setWidth(DEFAULT_BAR_WIDTH * attackSpeedCooldown);
            } else {
                this.attackSpeedBar.setWidth(0);
            }
            this.healthStat.setText(decimalFormat.format(statComponent.getMaxHealthPoints()));
            this.armorStat.setText(decimalFormat.format(statComponent.getMaxArmor()));
            this.attackSpeedStat.setText(decimalFormat.format(statComponent.getAttackSpeedPrimary()));
            this.moveSpeedStat.setText(decimalFormat.format(statComponent.getMovementSpeed()));
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
        for (Entity tooltipEntity : EntityHandler.getInstance().getAllEntitiesWithComponents(TooltipComponent.class)) {
            updateTooltipPopup(tooltipEntity);
        }
        for (Entity item : EntityHandler.getInstance().getAllEntitiesWithComponents(ItemTag.class)) {
            updateItemPopup(item);
        }
        for (UIElement uiElement : UIHandler.getInstance().getAllObjects()
                .stream()
                .filter(uiElement -> UIStateHandler.getInstance().getVisibleUIGroups().contains(uiElement.getUiGroupKey()))
                .collect(Collectors.toList())) {
            updateUIElementPopup(uiElement);
            updateUIComponentElementSelection(uiElement);
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
                transformationComponent.getPosition())) {
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
                    && uiElement.isVisible()
                    && ((UIComponentElement) uiElement).getComponent() != null) {
                UIGroup upgradeTooltipGroup = UIUtil.getUpgradeComponentTooltip((UpgradeComponent) ((UIComponentElement) uiElement).getComponent(),
                        new Vector2d(uiElement.getX(), uiElement.getY()), 6);
                UIUtil.addUIGroupToUIHandler(upgradeTooltipGroup, "TOOLTIP_");
            }
        }
    }
}
