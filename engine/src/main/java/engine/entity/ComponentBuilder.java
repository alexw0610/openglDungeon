package engine.entity;

import engine.component.Component;
import engine.handler.ComponentTemplateHandler;

public class ComponentBuilder {
    public static Component fromTemplate(String template) {
        ComponentTemplate componentTemplate = ComponentTemplateHandler.getInstance().getObject(template);
        Component component = null;
        if (componentTemplate != null) {
            component = TemplateAssembler.assembleComponent(componentTemplate);
        }
        return component;
    }
}
