package engine.handler.key;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import engine.EngineConstants;
import engine.handler.RenderHandler;
import engine.handler.SceneHandler;
import engine.object.Camera;

public class KeyHandler extends KeyFunctions {
    public static final KeyHandler KEY_HANDLER = new KeyHandler();
    private final RenderHandler renderHandler = RenderHandler.RENDER_HANDLER;
    private final SceneHandler sceneHandler = SceneHandler.getInstance();

    private KeyHandler() {
        super();
    }

    private final byte[] keyMap = new byte[255];
    private final byte primaryMouseButton = 0;
    private final byte secondaryMouseButton = 0;
    private byte mouseWheelRotation = 0;


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

    public void setMouseWheelRotation(MouseEvent mouseEvent) {
        mouseWheelRotation = mouseEvent.getRotation()[1] < 0 ? (byte) -1 : (byte) 1;
    }

    private boolean isKeyPressed(int keyCode) {
        return keyMap[keyCode] == 1;
    }

    public void processActiveKeys() {
        for (short i = 0; i < 255; i++) {
            if (isKeyPressed(i)) {
                KeyFunction keyFunction = getFunctionForKey(i);
                if (keyFunction != null) {
                    keyFunction.run(renderHandler.getCurrentFrameDelta() * EngineConstants.FRAME_DELTA_FACTOR, sceneHandler.getPlayer(), Camera.CAMERA);
                }
            }
        }

    }

}
