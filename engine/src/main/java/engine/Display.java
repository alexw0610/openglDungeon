package engine;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import engine.handler.*;
import engine.service.RenderService;

public class Display implements GLEventListener {
    private final Engine engine;
    private final InputListener inputListener;

    public Display(Engine engine, InputListener inputListener) {
        this.engine = engine;
        this.inputListener = inputListener;
    }

    public void init(GLAutoDrawable glAutoDrawable) {
        this.engine.setEntityHandler(EntityHandler.getInstance());
        this.engine.setUiHandler(UIHandler.getInstance());
        this.inputListener.setKeyHandler(KeyHandler.getInstance());
        this.inputListener.setMouseHandler(MouseHandler.getInstance());
        this.engine.setStarted(true);
    }

    public void dispose(GLAutoDrawable glAutoDrawable) {
    }

    public void display(GLAutoDrawable glAutoDrawable) {
        this.engine.step();
    }

    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {
        EngineConstants.setWindowWidth(i2);
        EngineConstants.setWindowHeight(i3);
        RenderService.reloadRenderService();
    }
}
