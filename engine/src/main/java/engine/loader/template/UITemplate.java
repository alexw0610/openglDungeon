package engine.loader.template;

import engine.object.ui.UIElement;

import java.util.List;

public class UITemplate {

    private String templateName;
    private List<UIElement> entities;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public List<UIElement> getEntities() {
        return entities;
    }

    public void setEntities(List<UIElement> entities) {
        this.entities = entities;
    }
}
