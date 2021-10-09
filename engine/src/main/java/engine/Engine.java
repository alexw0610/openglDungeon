package engine;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;
import engine.component.CameraComponent;
import engine.component.RenderComponent;
import engine.component.TransformationComponent;
import engine.entity.Entity;
import engine.handler.EntityHandler;
import engine.handler.MeshHandler;
import engine.service.RenderService;
import engine.system.*;
import org.joml.Vector2d;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static engine.EngineConstants.*;

public class Engine {

    public static double stepTimeDelta = 0;
    private static double lastStepTime = 0;
    private double lastPerformanceAudit = 0;
    private double fpsCount = 0;

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
        List<Entity> entities = EntityHandler.getInstance().getAllObjects();
        for (Entity entity : entities) {
            if (PlayerMovementInputSystem.isResponsibleFor(entity)) {
                PlayerMovementInputSystem.processEntity(entity);
            }
            if (AISystem.isResponsibleFor(entity)) {
                AISystem.processEntity(entity);
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
        }
        //Movement Logic
        for (Entity entity : entities) {
            if (ProjectileSystem.isResponsibleFor(entity)) {
                ProjectileSystem.processEntity(entity);
            }
            if (PhysicsSystem.isResponsibleFor(entity)) {
                PhysicsSystem.processEntity(entity);
            }
        }
        //Post Game Logic
        for (Entity entity : entities) {
            if (DestructionSystem.isResponsibleFor(entity)) {
                DestructionSystem.processEntity(entity);
            }
        }
        Vector2d cameraPosition = EntityHandler.getInstance().getEntityWithComponent(CameraComponent.class).getComponentOfType(TransformationComponent.class).getPosition();
        List<Entity> entitiesToRender = EntityHandler.getInstance().getAllEntitiesWithComponents(RenderComponent.class).stream()
                .filter(e -> e.getComponentOfType(TransformationComponent.class).getPosition().distance(cameraPosition) < EngineConstants.RENDER_DISTANCE)
                .sorted(Comparator.comparingInt(e -> e.getComponentOfType(RenderComponent.class).getLayer()))
                .collect(Collectors.toList());
        //Pre Rendering
        for (Entity entity : entitiesToRender) {
            if (ViewSourceSystem.isResponsibleFor(entity)) {
                ViewSourceSystem.processEntity(entity);
            }
            if (LightSourceSystem.isResponsibleFor(entity)) {
                LightSourceSystem.processEntity(entity);
            }
        }
        //Rendering
        for (Entity entity : entitiesToRender) {
            if (RenderSystem.isResponsibleFor(entity)) {
                RenderSystem.processEntity(entity);
            }
        }
        long stepTime = java.lang.System.nanoTime();
        stepTimeDelta = (stepTime - lastStepTime) / 1000;
        lastStepTime = stepTime;
        RenderService.renderTick += stepTimeDelta;
        fpsCount++;
        printPerformanceAudit();
    }

    private void printPerformanceAudit() {
        if ((System.nanoTime() / 1000000.0) - lastPerformanceAudit > 5000) {
            System.out.println("Performance Audit:");
            System.out.printf(" Frame delta (ms): %s%n" +
                            " Fps (calc): %s%n" +
                            " Fps (cnt): %s%n" +
                            " Entities: %s%n" +
                            "  Entities rendered: %s%n" +
                            "  Lights rendered: %s%n" +
                            "  View maps rendered: %s%n" +
                            "----------------- %n",
                    stepTimeDelta / 1000,
                    (1000 / (stepTimeDelta / 1000)),
                    fpsCount / 5,
                    EntityHandler.getInstance().getEntityCount(),
                    RenderService.entitiesRendered,
                    RenderService.lightsRendered,
                    RenderService.viewMapsRendered
            );
            lastPerformanceAudit = (System.nanoTime() / 1000000.0);
            fpsCount = 0;
        }
    }
}
