package engine;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import engine.component.RenderComponent;
import engine.component.TransformationComponent;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.PrimitiveMeshShape;
import engine.enums.ShaderType;
import engine.enums.TextureKey;
import engine.handler.key.KeyHandler;
import engine.object.Mesh;
import engine.service.PhysicsService;
import engine.service.RenderService;
import engine.system.RenderSystem;
import engine.system.TransformationSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Display implements GLEventListener {

    RenderService renderService;
    RenderSystem renderSystem;
    TransformationSystem transformationSystem;
    PhysicsService physicsService = new PhysicsService();
    KeyHandler keyHandler = KeyHandler.KEY_HANDLER;
    List<Entity> entities = new ArrayList<>();

    public void init(GLAutoDrawable glAutoDrawable) {
        this.renderSystem = new RenderSystem();
        this.transformationSystem = new TransformationSystem();
        //this.renderService = new RenderService();
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

    public void dispose(GLAutoDrawable glAutoDrawable) {
    }

    public void display(GLAutoDrawable glAutoDrawable) {
        transformationSystem.processEntities(this.entities.stream().filter(transformationSystem::isResponsibleFor).collect(Collectors.toList()));
        //renderService.renderNextFrame();
        //keyHandler.processActiveKeys();
        //physicsService.doPhysics(RenderHandler.RENDER_HANDLER.getCurrentFrameDeltaMs());
        //Camera.CAMERA.lerpToLookAtTarget(RenderHandler.RENDER_HANDLER.getCurrentFrameDeltaMs());
        renderSystem.processEntities(this.entities.stream().filter(renderSystem::isResponsibleFor).collect(Collectors.toList()));

    }

    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {

    }
}
