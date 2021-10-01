package engine.handler;

import com.jogamp.newt.event.KeyEvent;

import java.util.HashMap;
import java.util.Map;

public class KeyHandler {
    private static final KeyHandler INSTANCE = new KeyHandler();
    private static Map<String, Short> actionToKeyMap;
    private static final byte[] keyMap = new byte[255];

    private KeyHandler() {
        actionToKeyMap = new HashMap<>();
        actionToKeyMap.put("movePlayerUp", KeyEvent.VK_W);
        actionToKeyMap.put("movePlayerDown", KeyEvent.VK_S);
        actionToKeyMap.put("movePlayerRight", KeyEvent.VK_D);
        actionToKeyMap.put("movePlayerLeft", KeyEvent.VK_A);
    }

    public static KeyHandler getInstance() {
        return INSTANCE;
    }

    public boolean isKeyForActionPressed(String action) {
        return actionToKeyMap.get(action) != null
                && keyMap[actionToKeyMap.get(action)] == 1;
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
}
