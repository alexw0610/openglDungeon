package engine.service;

import engine.component.*;
import engine.component.base.CollisionComponent;
import engine.component.base.TransformationComponent;
import engine.component.tag.PlayerTag;
import engine.entity.Entity;
import engine.enums.UIGroupKey;
import engine.handler.EntityHandler;
import engine.handler.MouseHandler;
import engine.handler.UIHandler;
import engine.object.ui.UIElement;
import engine.object.ui.UIText;
import engine.service.util.CollisionUtil;
import engine.service.util.CoordinateConverter;
import org.joml.Vector2d;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

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
        loadUIScene();
    }

    public void loadUIScene() {
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

        initStatUI();
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

    public void updateUI() {
        updateHUD();
        updateMouseOver();
    }

    private void updateHUD() {
        updatePlayerHUD();
        updateBossHealthbar();
    }

    private void updatePlayerHUD() {
        Entity player = EntityHandler.getInstance().getEntityWithComponent(PlayerTag.class);
        Entity gun = EntityHandler.getInstance().getEntityWithComponent(GunComponent.class);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        decimalFormat.setRoundingMode(RoundingMode.UP);
        if (player != null && player.getComponentOfType(StatComponent.class) != null) {

            this.levelIndicator.setText("Lvl. " + player.getComponentOfType(StatComponent.class).getLevel());

            double healthPercent = player.getComponentOfType(StatComponent.class).getHealthPercentage();
            this.healthbar.setWidth(DEFAULT_BAR_WIDTH * healthPercent);

            double armorPercent = player.getComponentOfType(StatComponent.class).getArmorPercentage();
            this.armorbar.setWidth(DEFAULT_BAR_WIDTH * armorPercent);

            double xpPercent = player.getComponentOfType(StatComponent.class).getXPPercentage();
            this.xpbar.setWidth(DEFAULT_BAR_WIDTH * xpPercent);

            if (gun != null) {
                double attackSpeedCooldown = Math.max(Math.min((System.nanoTime() - gun.getComponentOfType(GunComponent.class).getLastShotTime())
                        / (player.getComponentOfType(StatComponent.class).getAttackSpeed() * SECONDS_TO_NANOSECONDS_FACTOR), 1.0), 0);
                this.attackSpeedBar.setWidth(DEFAULT_BAR_WIDTH * attackSpeedCooldown);
            } else {
                this.attackSpeedBar.setWidth(0);
            }
            this.healthStat.setText(decimalFormat.format(player.getComponentOfType(StatComponent.class).getMaxHealthPoints()));
            this.armorStat.setText(decimalFormat.format(player.getComponentOfType(StatComponent.class).getMaxArmor()));
            this.attackSpeedStat.setText(decimalFormat.format(player.getComponentOfType(StatComponent.class).getAttackSpeed()));
            this.moveSpeedStat.setText(decimalFormat.format(player.getComponentOfType(StatComponent.class).getMovementSpeed()));
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
        UIHandler.getInstance().removeTextObject("TOOLTIP_TITLE");
        UIHandler.getInstance().removeTextObject("TOOLTIP_RARITY");
        UIHandler.getInstance().removeTextObject("TOOLTIP_TEXT");
        UIHandler.getInstance().removeObject("TOOLTIP_BACKGROUND");
        for (Entity tooltipEntity : EntityHandler.getInstance().getAllEntitiesWithComponents(TooltipComponent.class)) {
            updateTooltipPopup(tooltipEntity);
        }
        for (Entity upgradeEntity : EntityHandler.getInstance().getAllEntitiesWithComponents(UpgradeComponent.class)) {
            updateUpgradePopup(upgradeEntity);
        }
    }

    private static void updateTooltipPopup(Entity tooltipEntity) {
        TransformationComponent transformationComponent = tooltipEntity.getComponentOfType(TransformationComponent.class);
        if (CollisionUtil.checkInside(MouseHandler.getInstance().getMousePositionWorldSpace(),
                tooltipEntity.getComponentOfType(CollisionComponent.class).getHitBox(),
                transformationComponent.getPosition())) {
            Vector2d positionClipspace = CoordinateConverter.transformWorldSpaceToClipSpace(transformationComponent.getPosition());
            UIText tooltipText = new UIText(tooltipEntity.getComponentOfType(TooltipComponent.class).getTooltip(),
                    positionClipspace.x(),
                    positionClipspace.y(),
                    0.4,
                    0.1,
                    0.65);
            tooltipText.setColor(TEXT_COLOR_YELLOW);
            tooltipText.setLayer(2);
            UIElement tooltipBackground = new UIElement(positionClipspace.x(),
                    positionClipspace.y(),
                    tooltipText.getMaxReachedWidth(),
                    tooltipText.getMaxReachedHeight(),
                    1,
                    "tooltipBox");
            UIHandler.getInstance().addObject("TOOLTIP_TEXT", tooltipText);
            UIHandler.getInstance().addObject("TOOLTIP_BACKGROUND", tooltipBackground);
        }
    }

    private static void updateUpgradePopup(Entity upgradeEntity) {
        TransformationComponent transformationComponent = upgradeEntity.getComponentOfType(TransformationComponent.class);
        if (CollisionUtil.checkInside(MouseHandler.getInstance().getMousePositionWorldSpace(),
                upgradeEntity.getComponentOfType(CollisionComponent.class).getHitBox(),
                transformationComponent.getPosition())) {
            UpgradeComponent upgradeComponent = upgradeEntity.getComponentOfType(UpgradeComponent.class);
            Vector2d positionClipspace = CoordinateConverter.transformWorldSpaceToClipSpace(transformationComponent.getPosition());
            UIText upgradeTitle = new UIText(upgradeComponent.getUpgradeTitle(),
                    positionClipspace.x(),
                    positionClipspace.y(),
                    0.4,
                    0.1,
                    0.75);
            upgradeTitle.setColor(TEXT_COLOR_YELLOW);
            upgradeTitle.setLayer(2);

            UIText upgradeRarity = new UIText(upgradeComponent.getUpgradeRarity(),
                    positionClipspace.x(),
                    positionClipspace.y() + upgradeTitle.getMaxReachedHeight(),
                    0.4,
                    0.1,
                    0.65);

            switch (upgradeComponent.getUpgradeRarity()) {
                case "Common":
                    upgradeRarity.setColor(RARITY_COLOR_COMMON);
                    break;
                case "Rare":
                    upgradeRarity.setColor(RARITY_COLOR_RARE);
                    break;
                case "Epic":
                    upgradeRarity.setColor(RARITY_COLOR_EPIC);
                    break;
            }

            upgradeRarity.setLayer(2);

            UIText upgradeTooltip = new UIText(upgradeComponent.getToolTip(),
                    positionClipspace.x(),
                    positionClipspace.y() + upgradeTitle.getMaxReachedHeight() + upgradeRarity.getMaxReachedHeight(),
                    0.4,
                    0.1,
                    0.65);
            upgradeTooltip.setColor(TEXT_COLOR_WHITE);
            upgradeTooltip.setLayer(2);

            UIElement tooltipBackground = new UIElement(positionClipspace.x() - 0.025,
                    positionClipspace.y() + 0.025,
                    Math.max(upgradeTitle.getMaxReachedWidth(), upgradeTooltip.getMaxReachedWidth()) + 0.05,
                    upgradeTitle.getMaxReachedHeight() + upgradeRarity.getMaxReachedHeight() + upgradeTooltip.getMaxReachedHeight() - 0.05,
                    1,
                    "statBackground");
            UIHandler.getInstance().addObject("TOOLTIP_TITLE", upgradeTitle);
            UIHandler.getInstance().addObject("TOOLTIP_RARITY", upgradeRarity);
            UIHandler.getInstance().addObject("TOOLTIP_TEXT", upgradeTooltip);
            UIHandler.getInstance().addObject("TOOLTIP_BACKGROUND", tooltipBackground);
        }
    }
}
