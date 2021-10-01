package engine;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;
import engine.component.RenderComponent;
import engine.component.TransformationComponent;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.PrimitiveMeshShape;
import engine.enums.ShaderType;
import engine.enums.TextureKey;
import engine.object.Mesh;
import engine.system.RenderSystem;
import engine.system.System;
import engine.system.TransformationSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static engine.EngineConstants.*;

public class Engine {
    private final List<Entity> entities = new ArrayList<>();
    private final List<System> systems = new ArrayList<>();

    public void start() {
        setupDisplay();
    }

    public void init() {
        systems.add(new RenderSystem());
        systems.add(new TransformationSystem());
        entities.add(EntityBuilder.builder()
                .withComponent(new RenderComponent(new Mesh(PrimitiveMeshShape.TRIANGLE), TextureKey.DEFAULT, ShaderType.DEFAULT))
                .withComponent(new TransformationComponent())
                .build());
        entities.add(EntityBuilder.builder()
                .withComponent(new RenderComponent(new Mesh(PrimitiveMeshShape.TRIANGLE), TextureKey.DEFAULT, ShaderType.DEFAULT))
                .build());
        entities.add(EntityBuilder.builder()
                .withComponent(new TransformationComponent())
                .build());
    }

    private void setupDisplay() {
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLWindow window = GLWindow.create(caps);

        FPSAnimator animator = new FPSAnimator(window, FPS, true);

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDestroyNotify(WindowEvent arg0) {
                new Thread() {
                    @Override
                    public void run() {
                        if (animator.isStarted())
                            animator.stop();
                        java.lang.System.exit(0);
                    }
                }.start();
            }
        });

        window.setSize((int) WINDOW_WIDTH, (int) WINDOW_HEIGHT);
        window.addGLEventListener(new Display(this));
        InputListener inputListener = new InputListener();
        window.addKeyListener(inputListener);
        window.addMouseListener(inputListener);
        window.setTitle(TITLE);
        animator.start();
        window.setVisible(true);
    }

    public void step() {
        for (System system : this.systems) {
            system.processEntities(this.entities.stream().filter(system::isResponsibleFor).collect(Collectors.toList()));
        }
        //renderService.renderNextFrame();
        //keyHandler.processActiveKeys();
        //physicsService.doPhysics(RenderHandler.RENDER_HANDLER.getCurrentFrameDeltaMs());
        //Camera.CAMERA.lerpToLookAtTarget(RenderHandler.RENDER_HANDLER.getCurrentFrameDeltaMs());
    }

}
