package engine.handler;

import com.jogamp.newt.event.KeyEvent;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class KeyHandler {
    private static final ThreadLocal<KeyHandler> INSTANCE = ThreadLocal.withInitial(KeyHandler::new);
    private static Map<String, Short> actionToKeyMap;
    private static final byte[] keyMap = new byte[255];
    private final Queue<KeyEvent> keyPressedEventQueue = new ArrayDeque<>();

    private KeyHandler() {
        actionToKeyMap = new HashMap<>();
        actionToKeyMap.put("movePlayerUp", KeyEvent.VK_W);
        actionToKeyMap.put("movePlayerDown", KeyEvent.VK_S);
        actionToKeyMap.put("movePlayerRight", KeyEvent.VK_D);
        actionToKeyMap.put("movePlayerLeft", KeyEvent.VK_A);

        actionToKeyMap.put("dash", KeyEvent.VK_Q);

        actionToKeyMap.put("moveCameraUp", KeyEvent.VK_UP);
        actionToKeyMap.put("moveCameraDown", KeyEvent.VK_DOWN);
        actionToKeyMap.put("moveCameraRight", KeyEvent.VK_RIGHT);
        actionToKeyMap.put("moveCameraLeft", KeyEvent.VK_LEFT);

        actionToKeyMap.put("zoomCameraIn", KeyEvent.VK_PAGE_DOWN);
        actionToKeyMap.put("zoomCameraOut", KeyEvent.VK_PAGE_UP);

        actionToKeyMap.put("openInventory", KeyEvent.VK_I);
        actionToKeyMap.put("placeBomb", KeyEvent.VK_E);
    }

    public static KeyHandler getInstance() {
        return INSTANCE.get();
    }

    public static void setInstance(KeyHandler keyHandler) {
        INSTANCE.set(keyHandler);
    }

    public boolean isKeyForActionPressed(String action) {
        return actionToKeyMap.get(action) != null
                && keyMap[actionToKeyMap.get(action)] == 1;
    }

    public boolean isKeyForActionPressed(String action, boolean consume) {
        boolean pressed = actionToKeyMap.get(action) != null
                && keyMap[actionToKeyMap.get(action)] == 1;
        if(pressed && consume){
            setKeyReleased(actionToKeyMap.get(action));
        }
        return pressed;
    }

    public void setKeyPressed(KeyEvent keyEvent) {
        if (!keyEvent.isAutoRepeat()) {
            keyMap[keyEvent.getKeyCode()] = 1;
        }
    }

    public void setKeyReleased(KeyEvent keyEvent) {
        if (!keyEvent.isAutoRepeat()) {
            keyMap[keyEvent.getKeyCode()] = 0;
        }
    }

    public void setKeyReleased(short keyCode) {
        keyMap[keyCode] = 0;
    }

    public void addKeyPressedEvent(KeyEvent keyEvent) {
        this.keyPressedEventQueue.add(keyEvent);
    }

    public Queue<KeyEvent> getKeyPressedEventsQueue() {
        return this.keyPressedEventQueue;
    }

    public static short getKeyForAction(String action) {
        return actionToKeyMap.get(action);
    }
}
