package engine;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import engine.handler.RenderHandler;
import engine.handler.key.KeyHandler;
import engine.object.Camera;
import engine.service.PhysicsService;
import engine.service.RenderService;

public class Display implements GLEventListener {

    RenderService renderService;
    PhysicsService physicsService = new PhysicsService();
    KeyHandler keyHandler = KeyHandler.KEY_HANDLER;

    public void init(GLAutoDrawable glAutoDrawable) {
        this.renderService = new RenderService();
    }

    public void dispose(GLAutoDrawable glAutoDrawable) {
    }

    public void display(GLAutoDrawable glAutoDrawable) {
        renderService.renderNextFrame();
        keyHandler.processActiveKeys();
        physicsService.doPhysics(RenderHandler.RENDER_HANDLER.getCurrentFrameDeltaMs());
        Camera.CAMERA.lerpToLookAtTarget(RenderHandler.RENDER_HANDLER.getCurrentFrameDeltaMs());

    }

    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {

    }
}
