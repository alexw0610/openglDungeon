package engine.service.util;

import engine.component.GunComponent;
import engine.component.TooltipComponent;
import engine.component.UpgradeComponent;
import engine.enums.UIGroupKey;
import engine.handler.UIHandler;
import engine.object.ui.UIElement;
import engine.object.ui.UIGroup;
import engine.object.ui.UIText;
import org.apache.commons.lang3.RandomStringUtils;
import org.joml.Vector2d;
import org.joml.Vector3d;

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
                "uibg");
        UIGroup uiGroup = new UIGroup();
        uiGroup.addUiElements(tooltipBackground);
        uiGroup.addUiTexts(upgradeTitle, upgradeRarity, upgradeTooltip);
        return uiGroup;
    }

    public static UIGroup getGunComponentTooltip(GunComponent gunComponent, Vector2d position, int layer) {
        UIText upgradeTitle = new UIText(gunComponent.getGunName(),
                position.x(),
                position.y(),
                0.4,
                0.1,
                0.75);
        upgradeTitle.setColor(TEXT_COLOR_YELLOW);
        upgradeTitle.setLayer(layer);

        UIText type = new UIText("Gun",
                position.x(),
                position.y() + upgradeTitle.getMaxReachedHeight(),
                0.4,
                0.1,
                0.65);
        type.setColor(RARITY_COLOR_EPIC);
        type.setLayer(layer);

        UIText primaryAttack = new UIText("Primary Attack",
                position.x(),
                position.y() + upgradeTitle.getMaxReachedHeight() + type.getMaxReachedHeight(),
                0.4,
                0.1,
                0.65);
        primaryAttack.setColor(gunComponent.isPrimaryAttack() ? TEXT_COLOR_GREEN : TEXT_COLOR_GRAY);
        primaryAttack.setLayer(layer);

        UIText secondaryAttack = new UIText("Secondary Attack",
                position.x(),
                position.y() + upgradeTitle.getMaxReachedHeight() + type.getMaxReachedHeight() + primaryAttack.getMaxReachedHeight(),
                0.4,
                0.1,
                0.65);
        secondaryAttack.setColor(gunComponent.isSecondaryAttack() ? TEXT_COLOR_GREEN : TEXT_COLOR_GRAY);
        secondaryAttack.setLayer(layer);

        UIText modifierSlots = new UIText("Modifier Slots:",
                position.x(),
                position.y() + upgradeTitle.getMaxReachedHeight() + type.getMaxReachedHeight() + primaryAttack.getMaxReachedHeight() + secondaryAttack.getMaxReachedHeight(),
                0.4,
                0.1,
                0.70);
        modifierSlots.setColor(TEXT_COLOR_YELLOW);
        modifierSlots.setLayer(layer);

        int modifiersSlotsPrimary = gunComponent.isPrimaryModifierSlotAAvailable() ? gunComponent.isPrimaryModifierSlotBAvailable() ? 2 : 1 : gunComponent.isPrimaryModifierSlotBAvailable() ? 1 : 0;
        UIText modSlotPrimary = new UIText("Primary: " + modifiersSlotsPrimary,
                position.x(),
                position.y() + upgradeTitle.getMaxReachedHeight() + type.getMaxReachedHeight() + primaryAttack.getMaxReachedHeight() + secondaryAttack.getMaxReachedHeight() + modifierSlots.getMaxReachedHeight(),
                0.4,
                0.1,
                0.65);
        modSlotPrimary.setColor(modifiersSlotsPrimary > 0 ? TEXT_COLOR_GREEN : TEXT_COLOR_GRAY);
        modSlotPrimary.setLayer(layer);

        int modifiersSlotsSecondary = gunComponent.isSecondaryModifierSlotAAvailable() ? gunComponent.isSecondaryModifierSlotBAvailable() ? 2 : 1 : gunComponent.isSecondaryModifierSlotBAvailable() ? 1 : 0;
        UIText modSlotSecondary = new UIText("Secondary: " + modifiersSlotsSecondary,
                position.x(),
                position.y() + upgradeTitle.getMaxReachedHeight() + type.getMaxReachedHeight() + primaryAttack.getMaxReachedHeight() + secondaryAttack.getMaxReachedHeight() + modifierSlots.getMaxReachedHeight() + modSlotPrimary.getMaxReachedHeight(),
                0.4,
                0.1,
                0.65);
        modSlotSecondary.setColor(modifiersSlotsSecondary > 0 ? TEXT_COLOR_GREEN : TEXT_COLOR_GRAY);
        modSlotSecondary.setLayer(layer);

        UIElement tooltipBackground = new UIElement(position.x() - 0.025,
                position.y() + 0.025,
                Math.max(upgradeTitle.getMaxReachedWidth(), primaryAttack.getMaxReachedWidth()) + 0.05,
                upgradeTitle.getMaxReachedHeight()
                        + type.getMaxReachedHeight()
                        + primaryAttack.getMaxReachedHeight()
                        + secondaryAttack.getMaxReachedHeight()
                        + modifierSlots.getMaxReachedHeight()
                        + modSlotPrimary.getMaxReachedHeight()
                        + modSlotSecondary.getMaxReachedHeight()
                        - 0.05,
                layer - 1,
                "uibg");
        UIGroup uiGroup = new UIGroup();
        uiGroup.addUiElements(tooltipBackground);
        uiGroup.addUiTexts(upgradeTitle, type, primaryAttack, secondaryAttack, modifierSlots, modSlotPrimary, modSlotSecondary);
        return uiGroup;
    }

    public static void instantiateUITextTitleValuePair(String title, double scale, double xTitle, double y, Vector3d textColorTitle, String value, double xValue, Vector3d textColorValue, UIGroupKey uiGroupKey, String uiElementKeyPrefix) {
        instantiateUIText(title, scale, xTitle, y, textColorTitle, uiGroupKey, uiElementKeyPrefix);
        instantiateUIText(value, scale, xValue, y, textColorValue, uiGroupKey, uiElementKeyPrefix);
    }

    public static void instantiateUIText(String text, double scale, double x, double y, Vector3d textColor, UIGroupKey uiGroupKey, String uiElementKeyPrefix) {
        UIText uiText = new UIText(text, x, y, 1, 1, scale);
        uiText.setUiGroupKey(uiGroupKey);
        uiText.setLayer(2);
        uiText.setColor(textColor);
        UIHandler.getInstance().addObject(uiElementKeyPrefix + RandomStringUtils.randomAlphanumeric(6), uiText);
    }
}
