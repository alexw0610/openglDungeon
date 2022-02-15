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
import engine.handler.NavHandler;
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
    public static double collisionHandled = 0;
    private double gameLogicTime = 0;
    private double renderingTime = 0;
    private double movementLogicTime = 0;
    private double postLogicTime = 0;
    private double preRenderingTime = 0;

    private EntityHandler entityHandler;
    private NavHandler navHandler;

    private boolean started = false;

    public void start(boolean offlineMode, boolean serverMode) {
        INSTANCE.setOfflineMode(offlineMode);
        INSTANCE.setServerMode(serverMode);
        if (!serverMode) {
            setupDisplay();
        } else {
            setupServerLoop();
        }
        while (!started) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupServerLoop() {
        ServerLoop serverLoop = new ServerLoop(this);
        new Thread(serverLoop).start();
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
        InputListener inputListener = new InputListener();
        window.addGLEventListener(new Display(this, inputListener));
        window.addKeyListener(inputListener);
        window.addMouseListener(inputListener);
        window.setTitle(TITLE);
        animator.start();
        window.setVisible(!INSTANCE.isServerMode());
    }

    public void step() {
        double sectionStartTime = System.nanoTime();
        if (!INSTANCE.isServerMode()) {
            RenderService.getInstance().clearCall();
            MeshHandler.getInstance().removeMeshesWithPrefix(LightSourceSystem.LIGHT_POLYGON_KEY_PREFIX);
            MeshHandler.getInstance().removeMeshesWithPrefix(ViewSourceSystem.VIEW_POLYGON_KEY_PREFIX);
        }
        //Game logic
        List<Entity> entities = EntityHandler.getInstance().getAllObjects();
        for (Entity entity : entities) {
            if (StatSystem.isResponsibleFor(entity)) {
                StatSystem.processEntity(entity);
            }
            if (!INSTANCE.isServerMode() && PlayerMovementInputSystem.isResponsibleFor(entity)) {
                PlayerMovementInputSystem.processEntity(entity);
            }
            if (AISystem.isResponsibleFor(entity)) {
                AISystem.processEntity(entity);
            }
            if (!INSTANCE.isServerMode() && CameraSystem.isResponsibleFor(entity)) {
                CameraSystem.processEntity(entity);
            }
            if (!INSTANCE.isServerMode() && AnimationSystem.isResponsibleFor(entity)) {
                AnimationSystem.processEntity(entity);
            }
            if (!INSTANCE.isServerMode() && ParticleSystem.isResponsibleFor(entity)) {
                ParticleSystem.processEntity(entity);
            }
            if (CollisionSystem.isResponsibleFor(entity)) {
                CollisionSystem.processEntity(entity);
            }
            if (!INSTANCE.isServerMode() && BleedingSystem.isResponsibleFor(entity)) {
                BleedingSystem.processEntity(entity);
            }
            if (!INSTANCE.isServerMode() && ZoneChangeSystem.isResponsibleFor(entity)) {
                ZoneChangeSystem.processEntity(entity);
            }
            if (AttackSystem.isResponsibleFor(entity)) {
                AttackSystem.processEntity(entity);
            }
            if (!INSTANCE.isServerMode() && ColorShadeSystem.isResponsibleFor(entity)) {
                ColorShadeSystem.processEntity(entity);
            }
            if (InventorySystem.isResponsibleFor(entity)) {
                InventorySystem.processEntity(entity);
            }
        }
        this.gameLogicTime = System.nanoTime() - sectionStartTime;
        sectionStartTime = System.nanoTime();
        //Movement Logic
        for (Entity entity : entities) {
            if (ProjectileSystem.isResponsibleFor(entity)) {
                ProjectileSystem.processEntity(entity);
            }
            if (PhysicsSystem.isResponsibleFor(entity)) {
                PhysicsSystem.processEntity(entity);
            }
        }
        this.movementLogicTime = System.nanoTime() - sectionStartTime;
        sectionStartTime = System.nanoTime();
        //Post Game Logic
        for (Entity entity : entities) {
            if (DestructionSystem.isResponsibleFor(entity)) {
                DestructionSystem.processEntity(entity);
            }
        }
        this.postLogicTime = System.nanoTime() - sectionStartTime;
        if (!INSTANCE.isServerMode()) {
            sectionStartTime = System.nanoTime();
            Entity camera = EntityHandler.getInstance()
                    .getEntityWithComponent(CameraComponent.class);
            final Vector2d cameraPosition;
            if (camera != null) {
                cameraPosition = camera.getComponentOfType(TransformationComponent.class)
                        .getPosition();
            } else {
                cameraPosition = new Vector2d(0, 0);
            }
            List<Entity> entitiesToRender = EntityHandler.getInstance().getAllEntitiesWithComponents(RenderComponent.class)
                    .parallelStream()
                    .unordered()
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
            this.preRenderingTime = System.nanoTime() - sectionStartTime;
            sectionStartTime = System.nanoTime();
            //Rendering
            for (Entity entity : entitiesToRender) {
                if (RenderSystem.isResponsibleFor(entity)) {
                    RenderSystem.processEntity(entity);
                }
            }
        }
        this.renderingTime = System.nanoTime() - sectionStartTime;
        long stepTime = java.lang.System.nanoTime();
        stepTimeDelta = (stepTime - lastStepTime) / 1000;
        lastStepTime = stepTime;
        RenderService.renderTick += stepTimeDelta;
        fpsCount++;
        //printPerformanceAudit();
        collisionHandled = 0;
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
                            " System:%n" +
                            "  Collisions handled: %s%n" +
                            " Performance: %n" +
                            "  gameLogicTime: %s%n" +
                            "  postLogicTime: %s%n" +
                            "  movementLogicTime: %s%n" +
                            "  preRenderingTime: %s%n" +
                            "  renderingTime: %s%n" +
                            "----------------- %n",
                    stepTimeDelta / 1000,
                    (1000 / (stepTimeDelta / 1000)),
                    fpsCount / 5,
                    EntityHandler.getInstance().getEntityCount(),
                    RenderService.entitiesRendered,
                    RenderService.lightsRendered,
                    RenderService.viewMapsRendered,
                    Engine.collisionHandled,
                    gameLogicTime * 0.000001,
                    postLogicTime * 0.000001,
                    movementLogicTime * 0.000001,
                    preRenderingTime * 0.000001,
                    renderingTime * 0.000001
            );
            lastPerformanceAudit = (System.nanoTime() / 1000000.0);
            fpsCount = 0;
        }
    }

    public void setEntityHandler(EntityHandler entityHandler) {
        this.entityHandler = entityHandler;
    }

    public EntityHandler getEntityHandler() {
        return this.entityHandler;
    }

    public NavHandler getNavHandler() {
        return navHandler;
    }

    public void setNavHandler(NavHandler navHandler) {
        this.navHandler = navHandler;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }
}
