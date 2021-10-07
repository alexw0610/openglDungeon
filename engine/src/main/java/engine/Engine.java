package engine;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;
import engine.component.RenderComponent;
import engine.entity.Entity;
import engine.handler.EntityHandler;
import engine.handler.MeshHandler;
import engine.service.RenderService;
import engine.system.*;

import java.util.Comparator;
import java.util.stream.Collectors;

import static engine.EngineConstants.*;

public class Engine {

    public static double stepTimeDelta = 0;
    private static double lastStepTime = 0;

    public void start() {
        setupDisplay();
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
        RenderService.getInstance().clearCall();
        MeshHandler.getInstance().removeMeshesWithPrefix(LightSourceSystem.LIGHT_POLYGON_KEY_PREFIX);
        //Game logic
        for (Entity entity : EntityHandler.getInstance().getAllObjects()) {
            if (PlayerMovementInputSystem.isResponsibleFor(entity)) {
                PlayerMovementInputSystem.processEntity(entity);
            }
            if (ProjectileSystem.isResponsibleFor(entity)) {
                ProjectileSystem.processEntity(entity);
            }
            if (PhysicsSystem.isResponsibleFor(entity)) {
                PhysicsSystem.processEntity(entity);
            }
            if (CameraSystem.isResponsibleFor(entity)) {
                CameraSystem.processEntity(entity);
            }
            if (AnimationSystem.isResponsibleFor(entity)) {
                AnimationSystem.processEntity(entity);
            }
            if (ParticleSystem.isResponsibleFor(entity)) {
                ParticleSystem.processEntity(entity);
            }
            if (CollisionSystem.isResponsibleFor(entity)) {
                CollisionSystem.processEntity(entity);
            }
            if (DestructionSystem.isResponsibleFor(entity)) {
                DestructionSystem.processEntity(entity);
            }
        }
        //Pre Rendering
        for (Entity entity : EntityHandler.getInstance().getAllObjects()) {
            if (ViewSourceSystem.isResponsibleFor(entity)) {
                ViewSourceSystem.processEntity(entity);
            }
            if (LightSourceSystem.isResponsibleFor(entity)) {
                LightSourceSystem.processEntity(entity);
            }
        }
        //Rendering
        for (Entity entity : EntityHandler.getInstance().getAllEntitiesWithComponents(RenderComponent.class).stream()
                .sorted(Comparator.comparingInt(e -> e.getComponentOfType(RenderComponent.class).getLayer()))
                .collect(Collectors.toList())) {
            if (RenderSystem.isResponsibleFor(entity)) {
                RenderSystem.processEntity(entity);
            }
        }
        long stepTime = java.lang.System.nanoTime();
        stepTimeDelta = (stepTime - lastStepTime) / 1000;
        lastStepTime = stepTime;
        RenderService.renderTick += stepTimeDelta;
    }

}
