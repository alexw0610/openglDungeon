package engine.component;

import engine.handler.UIHandler;
import engine.object.ui.UIElement;

public class HealthBarComponent implements Component {
    private static final long serialVersionUID = 5303611554538247988L;

    private UIElement healthBarElement;

    public HealthBarComponent() {
    }

    public UIElement getHealthBarElement() {
        return healthBarElement;
    }

    public void setHealthBarElement(UIElement healthBarElement) {
        this.healthBarElement = healthBarElement;
    }

    public void onRemove() {
        UIHandler.getInstance().removeObject(healthBarElement.getElementKey());
    }
}
