package engine.entity;

import engine.component.Component;
import engine.handler.template.ComponentTemplateHandler;
import engine.loader.template.ComponentTemplate;

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
