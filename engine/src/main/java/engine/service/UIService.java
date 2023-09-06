package engine.service;

import engine.component.BossComponent;
import engine.component.StatComponent;
import engine.component.TooltipComponent;
import engine.component.UpgradeComponent;
import engine.component.base.CollisionComponent;
import engine.component.base.TransformationComponent;
import engine.component.tag.PlayerTag;
import engine.entity.Entity;
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
        initInventory();
    }

    public void updateUI() {
        updateHUD();
        updateStatUpgradeUI();
        updateMouseOver();
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

        UIElement statSummary = new UIElement(-DEFAULT_BAR_WIDTH - 0.65, 0.65, 0.5, 0.29, 1, "statBackground");
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
        UIElement statUpgradeBackground = new UIElement(-DEFAULT_BAR_WIDTH - 0.65, 0.35, 0.5, 0.29, 1, "statBackground");
        statUpgradeBackground.setUiGroupKey(UIGroupKey.STATS);
        UIHandler.getInstance().addObject(statUpgradeBackground);
    }

    private void initInventory() {
        UIElement inventoryBackground = new UIElement(-0.8, -0.7, 1.6, 1.4, 1, "uibg");
        inventoryBackground.setUiGroupKey(UIGroupKey.INVENTORY);
        inventoryBackground.setVisible(false);
        UIHandler.getInstance().addObject(inventoryBackground);
    }

    private void updateStatUpgradeUI() {
        UIHandler.getInstance().removeAllObjectsWithPrefix("STAT_UPGRADE_");
        StatComponent statComponent = EntityHandler.getInstance()
                .getEntityWithComponent(PlayerTag.class)
                .getComponentOfType(StatComponent.class);
        List<UpgradeComponent> upgrades = statComponent.getUpgrades();
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
                upgradeIcon.setUiGroupKey(UIGroupKey.STATS);
                UIHandler.getInstance().addObject("STAT_UPGRADE_" + RandomStringUtils.randomAlphanumeric(6), upgradeCountText);
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
        for (Entity upgradeEntity : EntityHandler.getInstance().getAllEntitiesWithComponents(UpgradeComponent.class)) {
            updateUpgradePopup(upgradeEntity);
        }
        for (UIElement uiElement : UIHandler.getInstance().getAllObjects()) {
            updateUIElementPopup(uiElement);
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

    private static void updateUpgradePopup(Entity upgradeEntity) {
        TransformationComponent transformationComponent = upgradeEntity.getComponentOfType(TransformationComponent.class);
        if (CollisionUtil.checkInside(MouseHandler.getInstance().getMousePositionWorldSpace(),
                upgradeEntity.getComponentOfType(CollisionComponent.class).getHitBox(),
                transformationComponent.getPosition())) {
            UpgradeComponent upgradeComponent = upgradeEntity.getComponentOfType(UpgradeComponent.class);
            Vector2d positionClipspace = CoordinateConverter.transformWorldSpaceToClipSpace(transformationComponent.getPosition());
            UIGroup upgradeTooltipGroup = UIUtil.getUpgradeComponentTooltip(upgradeComponent, positionClipspace, 6);
            UIUtil.addUIGroupToUIHandler(upgradeTooltipGroup, "TOOLTIP_");
        }
    }

    private static void updateUIElementPopup(UIElement uiElement) {
        if (CollisionUtil.checkInside(
                MouseHandler.getInstance().getMousePositionClipSpace(),
                uiElement.getX(),
                uiElement.getY(),
                uiElement.getX() + uiElement.getWidth(),
                uiElement.getY() + uiElement.getHeight())) {
            if (uiElement instanceof UIComponentElement && uiElement.isVisible()) {
                UIGroup upgradeTooltipGroup = UIUtil.getUpgradeComponentTooltip((UpgradeComponent) ((UIComponentElement) uiElement).getComponent(),
                        new Vector2d(uiElement.getX(), uiElement.getY()), 6);
                UIUtil.addUIGroupToUIHandler(upgradeTooltipGroup, "TOOLTIP_");
            }
        }
    }
}
