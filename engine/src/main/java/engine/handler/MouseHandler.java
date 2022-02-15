package engine.handler;

import com.jogamp.newt.event.MouseEvent;
import engine.EngineConstants;
import engine.service.RenderService;
import org.joml.Vector2d;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class MouseHandler {
    private static final ThreadLocal<MouseHandler> INSTANCE = ThreadLocal.withInitial(MouseHandler::new);
    private static Map<String, Short> actionToMouseMap;
    private final Queue<MouseEvent> mouseClickedEventsQueue = new ArrayDeque<>();
    private double mousePositionX;
    private double mousePositionY;
    private static final byte[] keyMap = new byte[255];

    private MouseHandler() {
        actionToMouseMap = new HashMap<>();
        actionToMouseMap.put("mouseButtonPrimary", MouseEvent.BUTTON1);
        actionToMouseMap.put("mouseButtonSecondary", MouseEvent.BUTTON2);
    }

    public static MouseHandler getInstance() {
        return INSTANCE.get();
    }

    public static void setInstance(MouseHandler mouseHandler) {
        INSTANCE.set(mouseHandler);
    }

    public boolean isKeyForActionPressed(String action) {
        return actionToMouseMap.get(action) != null
                && keyMap[actionToMouseMap.get(action)] == 1;
    }

    public Vector2d getMousePositionScreenSpace() {
        return new Vector2d(this.mousePositionX, this.mousePositionY);
    }

    public Vector2d getMousePositionClipSpace() {
        return new Vector2d(0.5 - (this.mousePositionX / (EngineConstants.WINDOW_WIDTH)), 0.5 - (this.mousePositionY / (EngineConstants.WINDOW_HEIGHT)));
    }

    public Vector2d getMousePositionWorldSpace() {
        Vector2d clipSpaceMouse = getMousePositionClipSpace().mul(2).mul(1, EngineConstants.WINDOW_HEIGHT / EngineConstants.WINDOW_WIDTH).div(RenderService.cameraPosZ);
        return new Vector2d(RenderService.cameraPosX - clipSpaceMouse.x(), RenderService.cameraPosY + clipSpaceMouse.y());
    }

    public void updateMouseEvent(MouseEvent mouseEvent) {
        this.mousePositionX = mouseEvent.getX();
        this.mousePositionY = mouseEvent.getY();
    }

    public void setKeyPressed(MouseEvent mouseEvent) {
        if (!mouseEvent.isAutoRepeat()) {
            keyMap[mouseEvent.getButton()] = 1;
        }
    }

    public void setKeyReleased(MouseEvent mouseEvent) {
        if (!mouseEvent.isAutoRepeat()) {
            keyMap[mouseEvent.getButton()] = 0;
        }
    }

    public Queue<MouseEvent> getMouseClickedEventsQueue() {
        return this.mouseClickedEventsQueue;
    }

    public void setKeyClicked(MouseEvent mouseEvent) {
        mouseClickedEventsQueue.add(mouseEvent);
    }
}
