package engine;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

public class Display implements GLEventListener {
    private final Engine engine;

    public Display(Engine engine) {
        this.engine = engine;
    }

    public void init(GLAutoDrawable glAutoDrawable) {
        engine.init();
    }

    public void dispose(GLAutoDrawable glAutoDrawable) {
    }

    public void display(GLAutoDrawable glAutoDrawable) {
        engine.step();
    }

    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {
    }
}
