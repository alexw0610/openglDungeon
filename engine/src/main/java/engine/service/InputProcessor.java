package engine.service;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import engine.component.*;
import engine.entity.ComponentBuilder;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.EventType;
import engine.handler.*;
import engine.object.ui.UIElement;
import engine.object.ui.UIInventoryElement;
import engine.service.util.CollisionUtil;
import org.joml.Vector2d;

import java.util.List;
import java.util.stream.Collectors;

public class InputProcessor {
    private static final ThreadLocal<InputProcessor> INSTANCE = ThreadLocal.withInitial(InputProcessor::new);

    private InputProcessor() {

    }

    public InputProcessor getInstance() {
        return INSTANCE.get();
    }

    public static void processInput() {
        Entity player = EntityHandler.getInstance().getEntityWithComponent(PlayerTag.class);
        if (player != null) {
            TransformationComponent transformationComponent = player.getComponentOfType(TransformationComponent.class);
            while (!MouseHandler.getInstance().getMouseClickedEventsQueue().isEmpty()) {
                MouseEvent event = MouseHandler.getInstance().getMouseClickedEventsQueue().poll();
                processMouseEvent(event, transformationComponent, player);
            }
            while (!KeyHandler.getInstance().getKeyPressedEventsQueue().isEmpty()) {
                KeyEvent event = KeyHandler.getInstance().getKeyPressedEventsQueue().poll();
                processKeyEvent(event, player);
            }
        }
    }

    private static void processKeyEvent(KeyEvent event, Entity player) {
        if (event != null && KeyHandler.getKeyForAction("openInventory") == event.getKeyCode()) {
            UISceneHandler.getInstance().toggleInventory(player.getEntityId());
        }
    }

    private static void processMouseEvent(MouseEvent event, TransformationComponent transformationComponent, Entity player) {
        if (!processMouseOverUI(event)) {
            processMouseOverWorld(event, transformationComponent, player);
        }
    }

    private static boolean processMouseOverUI(MouseEvent event) {
        if (event != null && event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() == 1) {
            for (UIInventoryElement uiInventoryElement : UISceneHandler.getInstance().getActiveInventories()) {
                for (UIInventoryElement.InventorySlot inventorySlot : uiInventoryElement.getInventorySlots()) {
                    UIElement offsetSlotUIElement = inventorySlot.getItemUIElement().clone();
                    offsetSlotUIElement.setScreenPositionX(offsetSlotUIElement.getScreenPositionX() + uiInventoryElement.getxOffset());
                    if (UIService.getInstance().isMouseOver(MouseHandler.getInstance().getMousePositionClipSpace(), offsetSlotUIElement)) {
                        if (UISceneHandler.getInstance().getPickedItem() == null && inventorySlot.getItemComponent() != null) {
                            UISceneHandler.getInstance().setPickedItem(inventorySlot);
                        } else if (UISceneHandler.getInstance().getPickedItem() != null && inventorySlot.getItemComponent() == null) {
                            UIInventoryElement.InventorySlot pickedItem = UISceneHandler.getInstance().getPickedItem();
                            pickedItem.getInventoryComponent().getItems().remove(pickedItem.getItemComponent());
                            inventorySlot.getInventoryComponent().getItems().add(pickedItem.getItemComponent());
                            if (!pickedItem.getUiInventoryElement().getParentEntityId().equals(uiInventoryElement.getParentEntityId())) {
                                EventHandler.getInstance().addEvent(EventType.INVENTORY_ITEM_MOVED,
                                        pickedItem.getItemComponent(),
                                        pickedItem.getUiInventoryElement().getParentEntityId(),
                                        uiInventoryElement.getParentEntityId());
                            }
                            uiInventoryElement.reload();
                            pickedItem.getUiInventoryElement().reload();
                            UISceneHandler.getInstance().setPickedItem(null);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static void processMouseOverWorld(MouseEvent event, TransformationComponent transformationComponent, Entity player) {
        if (event != null && event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() == 1) {
            AttackComponent attack = (AttackComponent) ComponentBuilder.fromTemplate("slashAttack");
            attack.setTargetComponentConstraint(MobTag.class);
            EntityBuilder.builder()
                    .withComponent(attack)
                    .at(transformationComponent.getPosition().x(), transformationComponent.getPosition().y())
                    .buildAndInstantiate();
        }
        if (event != null && event.getButton() == MouseEvent.BUTTON3 && event.getClickCount() == 1) {
            List<Entity> entityList = EntityHandler.getInstance()
                    .getAllEntitiesWithComponents(CollisionComponent.class, InventoryComponent.class)
                    .stream()
                    .filter(entity -> !entity.hasComponentOfType(StatComponent.class) || entity.getComponentOfType(StatComponent.class)
                            .isDead())
                    .collect(Collectors.toList());
            Vector2d mousePositionWorldSpace = MouseHandler.getInstance().getMousePositionWorldSpace(new Vector2d(event.getX(), event.getY()));
            for (Entity entity : entityList) {
                boolean clickedOn = CollisionUtil.checkInside(mousePositionWorldSpace,
                        entity.getComponentOfType(CollisionComponent.class).getHitBox(),
                        entity.getComponentOfType(TransformationComponent.class).getPosition());
                if (clickedOn) {
                    System.out.println("Requested inventory display of entityId: " + entity.getEntityId());
                    UISceneHandler.getInstance().toggleInventory(entity.getEntityId());
                    UISceneHandler.getInstance().setInventoryActive(player.getEntityId());
                    break;
                }
            }
        }
    }
}
