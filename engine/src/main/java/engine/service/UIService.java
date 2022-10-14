package engine.service;

import engine.component.*;
import engine.entity.Entity;
import engine.enums.Color;
import engine.handler.EntityHandler;
import engine.handler.MouseHandler;
import engine.handler.UISceneHandler;
import engine.object.ui.HealthBar;
import engine.object.ui.UIElement;
import engine.object.ui.UIInventoryElement;
import engine.object.ui.UIText;
import engine.service.util.CollisionUtil;
import engine.service.util.CoordinateConverter;
import org.apache.commons.lang3.StringUtils;
import org.joml.Intersectiond;
import org.joml.Vector2d;

import java.util.*;
import java.util.stream.Collectors;

public class UIService {

    private static final ThreadLocal<UIService> INSTANCE = ThreadLocal.withInitial(UIService::new);

    private final List<UIElement> currentUiElements = new LinkedList<>();
    private final List<UIText> currentUITextElements = new LinkedList<>();

    private UIService() {
    }

    public static UIService getInstance() {
        return INSTANCE.get();
    }

    public void updateUI() {
        if (UISceneHandler.getInstance().getPickedItem() != null) {
            UIElement pickedItem = UISceneHandler.getInstance().getPickedItem().getItemUIElement().clone();
            pickedItem.setScreenPositionX(MouseHandler.getInstance().getMousePositionClipSpace().x());
            pickedItem.setScreenPositionY(MouseHandler.getInstance().getMousePositionClipSpace().y());
            pickedItem.setLayer(10);
            currentUiElements.add(pickedItem);
        }
        for (UIInventoryElement inventoryElement : UISceneHandler.getInstance().getActiveInventories()) {
            currentUiElements.addAll(inventoryElement.getCopyOfElementsToDisplay());
            currentUITextElements.addAll(inventoryElement.getCopyOfTextElementsToDisplay());
        }
        generateMobTagUI();
        processUIMouseOver();
        processEntityMouseOver();
        Map<Integer, List<UIDrawable>> drawOrderMap = generateDrawOrderMap();
        renderItems(drawOrderMap);
        currentUiElements.clear();
        currentUITextElements.clear();
    }

    private void renderItems(Map<Integer, List<UIDrawable>> drawOrderMap) {
        drawOrderMap.forEach((integer, uiDrawables) -> {
            uiDrawables.forEach(drawable -> {
                if (drawable.getUiElement() != null) {
                    RenderService.getInstance().renderUI(drawable.getUiElement());
                } else {
                    RenderService.getInstance().renderUI(drawable.getText());
                }
            });
        });
    }

    /*
     * DrawOrder Guidelines:
     *   Smallest layer will be rendered on top!
     *   0-9: Tooltip & Tooltip background
     *   10-19: General UI (Inventory, ...)
     *   20-29: Healthbar, Nameplates, ...
     */
    private Map<Integer, List<UIDrawable>> generateDrawOrderMap() {
        Map<Integer, List<UIDrawable>> drawOrderMap = new TreeMap<>(Collections.reverseOrder());
        currentUiElements.forEach(uiElement -> {
            if (!drawOrderMap.containsKey(uiElement.getLayer())) {
                drawOrderMap.put(uiElement.getLayer(), new LinkedList<>());
            }
            drawOrderMap.get(uiElement.getLayer()).add(new UIDrawable(uiElement.getLayer(), uiElement));
        });
        currentUITextElements.forEach(uiText -> {
            if (!drawOrderMap.containsKey(uiText.getLayer())) {
                drawOrderMap.put(uiText.getLayer(), new LinkedList<>());
            }
            drawOrderMap.get(uiText.getLayer()).add(new UIDrawable(uiText.getLayer(), uiText));
        });
        return drawOrderMap;
    }

    private void processUIMouseOver() {
        Vector2d currentMousePosition = MouseHandler.getInstance().getMousePositionClipSpace();
        for (UIElement uiElement : currentUiElements) {
            if (isMouseOver(currentMousePosition, uiElement)) {
                if (StringUtils.isNotBlank(uiElement.getTooltip())) {
                    generateUiTooltip(uiElement);
                    break;
                }
            }
        }
    }

    private void processEntityMouseOver() {
        generateLootableTooltip();
    }

    private void generateLootableTooltip() {
        List<Entity> hoveredEntities = EntityHandler.getInstance().getAllEntitiesWithComponents(InventoryComponent.class, CollisionComponent.class, TransformationComponent.class);
        Vector2d mousePositionClipSpace = MouseHandler.getInstance().getMousePositionClipSpace();
        Vector2d mousePositionWorldSpace = MouseHandler.getInstance().getMousePositionWorldSpace();
        hoveredEntities = hoveredEntities.stream()
                .filter(entity -> (!entity.hasComponentOfType(StatComponent.class) || entity.getComponentOfType(StatComponent.class).isDead())
                        && !entity.getComponentOfType(InventoryComponent.class).getItems().isEmpty()
                        && CollisionUtil.checkInside(mousePositionWorldSpace,
                        entity.getComponentOfType(CollisionComponent.class).getHitBox(),
                        entity.getComponentOfType(TransformationComponent.class).getPosition()))
                .collect(Collectors.toList());
        hoveredEntities.forEach(entity -> generateUiTooltip("Loot", mousePositionClipSpace.x(), mousePositionClipSpace.y()));

    }

    public boolean isMouseOver(Vector2d currentMousePosition, UIElement uiElement) {
        return Intersectiond.testPointAar(currentMousePosition.x(), currentMousePosition.y(),
                uiElement.getPosTopLeftX(),
                uiElement.getPosBottomRightY(),
                uiElement.getPosBottomRightX(),
                uiElement.getPosTopLeftY());
    }

    private void generateUiTooltip(UIElement uiElement) {
        generateUiTooltip(uiElement.getTooltip(), uiElement.getScreenPositionX(), (uiElement.getScreenPositionY() - uiElement.getHeight() / 2));
    }

    private void generateUiTooltip(String tooltipText, double screenPositionX, double screenPositionY) {
        UIText tooltip = new UIText(tooltipText).centered().fontSize(0.0007).spacing(1.15).layer(0).fixedSize(true);
        tooltip.setColor(Color.WHITE);
        tooltip.setScreenPosition(new Vector2d(screenPositionX, screenPositionY));
        UIElement tooltipBackground = new UIElement(tooltip.getScreenPosition().x(),
                tooltip.getScreenPosition().y(),
                tooltip.getFontSize() * tooltip.getTotalWidth() * tooltip.getSpacing(),
                0.035);
        tooltipBackground.setColor(Color.PURPLE.value());
        tooltipBackground.setLayer(1);
        tooltipBackground.setFixedSize(true);
        currentUITextElements.add(tooltip);
        currentUiElements.add(tooltipBackground);
    }

    private void generateMobTagUI() {
        List<Entity> entities = EntityHandler.getInstance().getAllEntitiesWithComponents(StatComponent.class, TransformationComponent.class);
        entities.stream()
                .filter(entity -> StringUtils.isNotBlank(entity.getComponentOfType(StatComponent.class).getEntityName()))
                .forEach(this::createdNameplate);
        entities.forEach(this::createHealthbar);
    }

    private void createdNameplate(Entity entity) {
        UIText uiText = new UIText(entity.getComponentOfType(StatComponent.class).getEntityName()).centered().fontSize(0.0035).spacing(1.15).layer(20);
        uiText.setColor(entity.hasComponentOfType(MobTag.class) ? Color.RED : entity.hasComponentOfType(PlayerTag.class) ? Color.YELLOW : Color.GREEN);
        uiText.setScreenPosition(CoordinateConverter.transformWorldSpaceToClipSpace(entity.getComponentOfType(TransformationComponent.class).getPosition().add(0, 0.6)));
        currentUITextElements.add(uiText);
    }

    private void createHealthbar(Entity entity) {
        Vector2d position = CoordinateConverter.transformWorldSpaceToClipSpace(entity.getComponentOfType(TransformationComponent.class).getPosition().add(0, 0.45));
        HealthBar healthBar = new HealthBar(position.x(), position.y(), 0.8, 0.03, entity.getComponentOfType(StatComponent.class).getHealthPercentage());
        healthBar.setFixedSize(false);
        healthBar.setLayer(20);
        currentUiElements.add(healthBar);
    }

    public List<UIElement> getCurrentUiElements() {
        return currentUiElements;
    }

    private static class UIDrawable {
        private int layer;
        private UIElement uiElement;
        private UIText text;

        public UIDrawable(int layer, UIElement uiElement) {
            this.layer = layer;
            this.uiElement = uiElement;
        }


        public UIDrawable(int layer, UIText text) {
            this.layer = layer;
            this.text = text;
        }

        public int getLayer() {
            return layer;
        }

        public void setLayer(int layer) {
            this.layer = layer;
        }

        public UIElement getUiElement() {
            return uiElement;
        }

        public void setUiElement(UIElement uiElement) {
            this.uiElement = uiElement;
        }

        public UIText getText() {
            return text;
        }

        public void setText(UIText text) {
            this.text = text;
        }
    }
}
