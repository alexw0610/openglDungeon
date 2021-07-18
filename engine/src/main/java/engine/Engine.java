package engine;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;
import engine.enumeration.PrimitiveMeshShape;
import engine.enumeration.ShaderType;
import engine.handler.RenderHandler;
import engine.handler.SceneHandler;
import engine.object.Camera;
import engine.object.GameObject;
import engine.object.Player;

public class Engine {

    private static final String TITLE = "Dungeon";
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 960;
    private static final int FPS = 60;

    public void start() {
        setupDisplay();
    }

    private static void setupDisplay() {
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLWindow window = GLWindow.create(caps);

        final FPSAnimator animator = new FPSAnimator(window, FPS, true);

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDestroyNotify(WindowEvent arg0) {
                new Thread() {
                    @Override
                    public void run() {
                        if (animator.isStarted())
                            animator.stop();
                        System.exit(0);
                    }
                }.start();
            }
        });

        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        window.addGLEventListener(new Display());
        InputListener inputListener = new InputListener();
        window.addKeyListener(inputListener);
        window.addMouseListener(inputListener);
        window.setTitle(TITLE);
        window.setVisible(true);
        animator.start();
    }

}
