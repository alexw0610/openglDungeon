package engine;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;
import engine.component.*;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.HitBoxType;
import engine.enums.PrimitiveMeshShape;
import engine.enums.ShaderType;
import engine.enums.TextureKey;
import engine.handler.EntityHandler;
import engine.object.HitBox;
import engine.service.RenderService;
import engine.system.System;
import engine.system.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static engine.EngineConstants.*;

public class Engine {

    private static final List<System> systems = new ArrayList<>();
    public static double stepTimeDelta = 0;
    private static double lastStepTime = 0;

    public void start() {
        setupDisplay();
    }

    public void init() {
        systems.addAll(Arrays.asList(
                new RenderSystem(),
                new TransformationSystem(),
                new PlayerMovementInputSystem(),
                new CameraSystem()));

        EntityHandler.getInstance().addObject(EntityBuilder.builder()
                .withComponent(new RenderComponent(PrimitiveMeshShape.TRIANGLE, TextureKey.DEFAULT, ShaderType.DEFAULT, 1, 0))
                .withComponent(new TransformationComponent())
                .withComponent(new PhysicsComponent())
                .withComponent(new PlayerComponent())
                .withComponent(new CameraTargetComponent())
                .withComponent(new CollisionComponent(new HitBox(HitBoxType.CIRCLE, 0.5)))
                .build());

        EntityHandler.getInstance().addObject(EntityBuilder.builder()
                .withComponent(new RenderComponent(PrimitiveMeshShape.QUAD, TextureKey.STONE_CLEAN_SUNSET_WALL, ShaderType.DEFAULT, 1, -1))
                .withComponent(new TransformationComponent(2, 0))
                .withComponent(new CollisionComponent(new HitBox(HitBoxType.AABB, 0.5)))
                .build());

        EntityHandler.getInstance().addObject(EntityBuilder.builder()
                .withComponent(new RenderComponent(PrimitiveMeshShape.QUAD, TextureKey.STONE_FLOOR_PLAIN_PURPLE, ShaderType.DEFAULT, 1, -1))
                .withComponent(new TransformationComponent(0, 0))
                .withComponent(new SurfaceComponent(new HitBox(HitBoxType.AABB, 4)))
                .build());

        EntityHandler.getInstance().addObject(EntityBuilder.builder()
                .withComponent(new TransformationComponent())
                .withComponent(new CameraComponent())
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
        RenderService.clearCall(0);
        for (Entity entity : EntityHandler.getInstance().getAllObjects()) {
            systems.stream().filter(system -> system.isResponsibleFor(entity)).forEach(system -> system.processEntity(entity));
        }
        long stepTime = java.lang.System.nanoTime();
        stepTimeDelta = (stepTime - lastStepTime) / 1000;
        lastStepTime = stepTime;
        //renderService.renderNextFrame();
        //keyHandler.processActiveKeys();
        //physicsService.doPhysics(RenderHandler.RENDER_HANDLER.getCurrentFrameDeltaMs());
        //Camera.CAMERA.lerpToLookAtTarget(RenderHandler.RENDER_HANDLER.getCurrentFrameDeltaMs());
    }

}
