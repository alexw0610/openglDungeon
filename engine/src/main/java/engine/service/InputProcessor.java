package engine.service;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import engine.component.TransformationComponent;
import engine.entity.Entity;

public class InputProcessor {
    private static final ThreadLocal<InputProcessor> INSTANCE = ThreadLocal.withInitial(InputProcessor::new);

    private InputProcessor() {

    }

    public InputProcessor getInstance() {
        return INSTANCE.get();
    }

    public static void processInput() {

    }

    private static void processKeyEvent(KeyEvent event, Entity player) {

    }

    private static void processMouseEvent(MouseEvent event, TransformationComponent transformationComponent, Entity player) {

    }

}
