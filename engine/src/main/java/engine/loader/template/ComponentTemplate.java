package engine.loader.template;

import java.util.Map;

public class ComponentTemplate {

    private String templateName;
    private String type;
    private Map<String, Object> arguments;
    private Map<String, Object> modifiers;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }

    public void setArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
    }

    public Map<String, Object> getModifiers() {
        return modifiers;
    }

    public void setModifiers(Map<String, Object> modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public String toString() {
        return "ComponentTemplate{" +
                "type='" + type + '\'' +
                ", arguments=" + arguments +
                ", modifiers=" + modifiers +
                '}';
    }
}
