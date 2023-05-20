package client;

import engine.Engine;
import engine.component.*;
import engine.entity.EntityBuilder;
import engine.enums.PrimitiveMeshShape;
import engine.enums.ShaderType;
import engine.handler.EntityHandler;

public class Main {

    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.start();

        EntityHandler.setInstance(engine.getEntityHandler());
        RenderComponent renderComponent = new RenderComponent(PrimitiveMeshShape.QUAD.value(), null, ShaderType.DEFAULT.shaderKey, 1.0, 3);
        renderComponent.setAlwaysVisible(true);
        renderComponent.setShadeless(true);
        EntityBuilder.builder()
                .withComponent(new TransformationComponent(0.0, 0.0))
                .withComponent(renderComponent)
                .at(0, 0)
                .buildAndInstantiate();
        EntityBuilder.builder()
                .withComponent(new TransformationComponent(0.0, 0.0))
                .withComponent(new CameraComponent())
                .withComponent(new ViewSourceTag())
                .at(0, 0)
                .buildAndInstantiate();
    }
}
