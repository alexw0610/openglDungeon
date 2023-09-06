package engine;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.openal.util.ALut;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;
import engine.component.base.CameraComponent;
import engine.component.base.LightSourceComponent;
import engine.component.base.RenderComponent;
import engine.component.base.TransformationComponent;
import engine.component.tag.ViewSourceTag;
import engine.entity.Entity;
import engine.handler.EntityHandler;
import engine.handler.MeshHandler;
import engine.handler.UIHandler;
import engine.object.ui.UIElement;
import engine.object.ui.UIText;
import engine.service.InputProcessor;
import engine.service.MobSpawner;
import engine.service.RenderService;
import engine.service.UIService;
import engine.system.*;
import engine.system.base.*;
import org.joml.Vector2d;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static engine.EngineConstants.*;

public class Engine {

    public static double stepTimeDelta = 0;
    private static double lastStepTime = 0;
    private EntityHandler entityHandler;
    private UIHandler uiHandler;
    private boolean started = false;
    private boolean paused = true;
    private boolean alInit = false;

    public void start() {
        setupDisplay();
        while (!started) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupDisplay() {
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLWindow window = GLWindow.create(caps);
        FPSAnimator animator = new FPSAnimator(window, FPS, true);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDestroyNotify(WindowEvent arg0) {
                new Thread(() -> {
                    if (animator.isStarted()) animator.stop();
                    ALut.alutExit();
                    System.exit(0);
                }).start();
            }
        });
        window.setSize((int) WINDOW_WIDTH, (int) WINDOW_HEIGHT);
        window.setFullscreen(FULLSCREEN);
        InputListener inputListener = new InputListener();
        window.addGLEventListener(new Display(this, inputListener));
        window.addKeyListener(inputListener);
        window.addMouseListener(inputListener);
        window.setTitle(TITLE);
        animator.start();
        window.setVisible(true);
    }

    public void step() {
        if (!alInit) {
            ALut.alutInit();
            alInit = true;
        }
        while (paused) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        RenderService.getInstance().clearCall();
        clearEphemeralMeshes();
        processEntities();
        MobSpawner.spawnMobs(EntityHandler.getInstance().getWorld());
        renderEntities();
        if (System.nanoTime() - lastStepTime < 0.1 * SECONDS_TO_NANOSECONDS_FACTOR){
            UIService.getInstance().updateUI();
        }
        renderUI();
        InputProcessor.processInput();

        long stepTime = java.lang.System.nanoTime();
        stepTimeDelta = (stepTime - lastStepTime);
        lastStepTime = stepTime;
        RenderService.renderTick += stepTimeDelta;
    }

    private static void clearEphemeralMeshes() {
        MeshHandler.getInstance().removeMeshesWithPrefix(LightSourceSystem.LIGHT_POLYGON_KEY_PREFIX);
        MeshHandler.getInstance().removeMeshesWithPrefix(ViewSourceSystem.VIEW_POLYGON_KEY_PREFIX);
    }

    private static void processEntities() {
        //Game logic
        List<Entity> entities = EntityHandler.getInstance().getAllObjects();
        for (Entity entity : entities) {
            if (AttackSystem.isResponsibleFor(entity)) {
                AttackSystem.processEntity(entity);
            }
            if (CameraSystem.isResponsibleFor(entity)) {
                CameraSystem.processEntity(entity);
            }
            if (AnimationSystem.isResponsibleFor(entity)) {
                AnimationSystem.processEntity(entity);
            }
            if (CollisionSystem.isResponsibleFor(entity)) {
                CollisionSystem.processEntity(entity);
            }
        }

        //Movement Logic
        for (Entity entity : entities) {
            if (PhysicsSystem.isResponsibleFor(entity)) {
                PhysicsSystem.processEntity(entity);
            }
            if (AISystem.isResponsibleFor(entity)) {
                AISystem.processEntity(entity);
            }
        }

        //Post Game Logic
        for (Entity entity : entities) {
            if (PlayerMovementInputSystem.isResponsibleFor(entity)) {
                PlayerMovementInputSystem.processEntity(entity);
            }
            if (StatSystem.isResponsibleFor(entity)) {
                StatSystem.processEntity(entity);
            }
            if (BombSystem.isResponsibleFor(entity)) {
                BombSystem.processEntity(entity);
            }
            if (ProjectileSystem.isResponsibleFor(entity)) {
                ProjectileSystem.processEntity(entity);
            }
            if (PointToMouseSystem.isResponsibleFor(entity)) {
                PointToMouseSystem.processEntity(entity);
            }
            if (AudioSystem.isResponsibleFor(entity)) {
                AudioSystem.processEntity(entity);
            }
            if (PlayerSystem.isResponsibleFor(entity)) {
                PlayerSystem.processEntity(entity);
            }
            if (BossSystem.isResponsibleFor(entity)) {
                BossSystem.processEntity(entity);
            }
            if (DashSystem.isResponsibleFor(entity)) {
                DashSystem.processEntity(entity);
            }
            if (KnockbackSystem.isResponsibleFor(entity)) {
                KnockbackSystem.processEntity(entity);
            }
            if (StunSystem.isResponsibleFor(entity)) {
                StunSystem.processEntity(entity);
            }
            if (DamageTextSystem.isResponsibleFor(entity)) {
                DamageTextSystem.processEntity(entity);
            }
        }
    }

    private static void renderEntities() {
        Entity camera = EntityHandler.getInstance().getEntityWithComponent(CameraComponent.class);
        final Vector2d cameraPosition;
        if (camera != null) {
            cameraPosition = camera.getComponentOfType(TransformationComponent.class).getPosition();
        } else {
            cameraPosition = new Vector2d(0, 0);
        }
        List<Entity> entitiesToRender = EntityHandler.getInstance()
                .getAllEntitiesWithComponents(RenderComponent.class)
                .parallelStream()
                .unordered()
                .filter(e -> e.getComponentOfType(TransformationComponent.class)
                        .getPosition()
                        .distance(cameraPosition) < EngineConstants.RENDER_DISTANCE
                        && !e.getComponentOfType(RenderComponent.class)
                        .getTextureKey()
                        .isEmpty())
                .sorted(Comparator.comparingInt(e -> e.getComponentOfType(RenderComponent.class).getLayer()))
                .collect(Collectors.toList());
        for (Entity entity : EntityHandler.getInstance().getAllEntitiesWithComponents(ViewSourceTag.class)) {
            if (ViewSourceSystem.isResponsibleFor(entity)) {
                ViewSourceSystem.processEntity(entity);
            }
        }
        for (Entity entity : EntityHandler.getInstance().getAllEntitiesWithComponents(LightSourceComponent.class)) {
            if (LightSourceSystem.isResponsibleFor(entity)) {
                LightSourceSystem.processEntity(entity);
            }
        }
        for (Entity entity : entitiesToRender) {
            if (RenderSystem.isResponsibleFor(entity)) {
                RenderSystem.processEntity(entity);
            }
        }
    }

    private static void renderUI() {
        Collection<UIElement> uiElementsToRender = UIHandler.getInstance()
                .getAllObjects()
                .stream()
                .filter(UIElement::isVisible).collect(Collectors.toList());
        uiElementsToRender.addAll(UIHandler.getInstance().getAllTextObjects().stream()
                .filter(UIText::isVisible)
                .map(UIText::getCharacters)
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));
        uiElementsToRender = uiElementsToRender.stream()
                .sorted(Comparator.comparingInt(UIElement::getLayer))
                .collect(Collectors.toList());
        for (UIElement uiElement : uiElementsToRender) {
            RenderService.getInstance().renderUI(uiElement);
        }
    }

    public void setEntityHandler(EntityHandler entityHandler) {
        this.entityHandler = entityHandler;
    }

    public void setUiHandler(UIHandler uiHandler) {
        this.uiHandler = uiHandler;
    }

    public EntityHandler getEntityHandler() {
        return this.entityHandler;
    }

    public UIHandler getUIHandler() {
        return this.uiHandler;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
