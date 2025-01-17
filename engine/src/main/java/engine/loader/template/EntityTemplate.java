package engine.loader.template;

import java.util.List;

public class EntityTemplate {

    private String templateName;
    private String extendedTemplate;
    private List<ComponentTemplate> components;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getExtendedTemplate() {
        return extendedTemplate;
    }

    public void setExtendedTemplate(String extendedTemplate) {
        this.extendedTemplate = extendedTemplate;
    }

    public List<ComponentTemplate> getComponents() {
        return components;
    }

    public void setComponents(List<ComponentTemplate> components) {
        this.components = components;
    }

    @Override
    public String toString() {
        return "EntityTemplate{" +
                "templateName='" + templateName + '\'' +
                ", components=" + components +
                '}';
    }
}
