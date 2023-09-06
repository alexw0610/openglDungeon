package engine.service.util;

import engine.component.TooltipComponent;
import engine.component.UpgradeComponent;
import engine.handler.UIHandler;
import engine.object.ui.UIElement;
import engine.object.ui.UIGroup;
import engine.object.ui.UIText;
import org.apache.commons.lang3.RandomStringUtils;
import org.joml.Vector2d;

import static engine.EngineConstants.*;

public class UIUtil {

    public static void addUIGroupToUIHandler(UIGroup uiGroup, String keyPrefix) {
        for (UIText text : uiGroup.getUiTextList()) {
            UIHandler.getInstance().addObject(keyPrefix + RandomStringUtils.randomAlphanumeric(6), text);
        }
        for (UIElement uiElement : uiGroup.getUiElementList()) {
            UIHandler.getInstance().addObject(keyPrefix + RandomStringUtils.randomAlphanumeric(6), uiElement);
        }
    }

    public static UIGroup getTooltip(TooltipComponent tooltipComponent, Vector2d position) {
        UIText tooltipText = new UIText(tooltipComponent.getTooltip(),
                position.x(),
                position.y(),
                0.4,
                0.1,
                0.65);
        tooltipText.setColor(TEXT_COLOR_YELLOW);
        tooltipText.setLayer(2);
        UIElement tooltipBackground = new UIElement(position.x(),
                position.y(),
                tooltipText.getMaxReachedWidth(),
                tooltipText.getMaxReachedHeight(),
                1,
                "tooltipBox");
        UIGroup uiGroup = new UIGroup();
        uiGroup.addUiTexts(tooltipText);
        uiGroup.addUiElements(tooltipBackground);
        return uiGroup;
    }

    public static UIGroup getUpgradeComponentTooltip(UpgradeComponent upgradeComponent, Vector2d position, int layer) {
        UIText upgradeTitle = new UIText(upgradeComponent.getUpgradeTitle(),
                position.x(),
                position.y(),
                0.4,
                0.1,
                0.75);
        upgradeTitle.setColor(TEXT_COLOR_YELLOW);
        upgradeTitle.setLayer(layer);

        UIText upgradeRarity = new UIText(upgradeComponent.getUpgradeRarity(),
                position.x(),
                position.y() + upgradeTitle.getMaxReachedHeight(),
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

        upgradeRarity.setLayer(layer);

        UIText upgradeTooltip = new UIText(upgradeComponent.getToolTip(),
                position.x(),
                position.y() + upgradeTitle.getMaxReachedHeight() + upgradeRarity.getMaxReachedHeight(),
                0.4,
                0.1,
                0.65);
        upgradeTooltip.setColor(TEXT_COLOR_WHITE);
        upgradeTooltip.setLayer(layer);

        UIElement tooltipBackground = new UIElement(position.x() - 0.025,
                position.y() + 0.025,
                Math.max(upgradeTitle.getMaxReachedWidth(), upgradeTooltip.getMaxReachedWidth()) + 0.05,
                upgradeTitle.getMaxReachedHeight() + upgradeRarity.getMaxReachedHeight() + upgradeTooltip.getMaxReachedHeight() - 0.05,
                layer - 1,
                "statBackground");
        UIGroup uiGroup = new UIGroup();
        uiGroup.addUiElements(tooltipBackground);
        uiGroup.addUiTexts(upgradeTitle, upgradeRarity, upgradeTooltip);
        return uiGroup;
    }
}
