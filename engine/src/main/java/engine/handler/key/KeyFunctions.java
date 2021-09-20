package engine.handler.key;

import com.jogamp.newt.event.KeyEvent;

import java.util.HashMap;
import java.util.Map;

public abstract class KeyFunctions {

    private final Map<Short, KeyFunction> functionMap = new HashMap<>();

    public KeyFunctions() {
        KeyFunction movePlayerUp = ((frameDelta, player, camera) -> player.addMomentumY(frameDelta));
        KeyFunction movePlayerDown = ((frameDelta, player, camera) -> player.addMomentumY(-frameDelta));
        KeyFunction movePlayerRight = ((frameDelta, player, camera) -> player.addMomentumX(frameDelta));
        KeyFunction movePlayerLeft = ((frameDelta, player, camera) -> player.addMomentumX(-frameDelta));

        KeyFunction moveCameraUp = ((frameDelta, player, camera) -> camera.moveUp(frameDelta));
        KeyFunction moveCameraDown = ((frameDelta, player, camera) -> camera.moveDown(frameDelta));
        KeyFunction moveCameraLeft = ((frameDelta, player, camera) -> camera.moveLeft(frameDelta));
        KeyFunction moveCameraRight = ((frameDelta, player, camera) -> camera.moveRight(frameDelta));

        KeyFunction zoomCameraOut = ((frameDelta, player, camera) -> camera.zoomOut(frameDelta));
        KeyFunction zoomCameraIn = ((frameDelta, player, camera) -> camera.zoomIn(frameDelta));

        functionMap.put(KeyEvent.VK_W, movePlayerUp);
        functionMap.put(KeyEvent.VK_S, movePlayerDown);
        functionMap.put(KeyEvent.VK_A, movePlayerLeft);
        functionMap.put(KeyEvent.VK_D, movePlayerRight);

        functionMap.put(KeyEvent.VK_UP, moveCameraUp);
        functionMap.put(KeyEvent.VK_DOWN, moveCameraDown);
        functionMap.put(KeyEvent.VK_LEFT, moveCameraLeft);
        functionMap.put(KeyEvent.VK_RIGHT, moveCameraRight);

        functionMap.put(KeyEvent.VK_PAGE_UP, zoomCameraOut);
        functionMap.put(KeyEvent.VK_PAGE_DOWN, zoomCameraIn);
    }

    KeyFunction getFunctionForKey(short keyCode) {
        return functionMap.get(keyCode);
    }
}
