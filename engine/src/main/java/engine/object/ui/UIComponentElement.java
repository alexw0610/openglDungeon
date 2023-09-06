package engine.object.ui;

import engine.component.Component;

public class UIComponentElement extends UIElement {

    private Component component;
    private Class<?> componentClass;

    public UIComponentElement(double x, double y, double width, double height, int layer, String texture) {
        super(x, y, width, height, layer, texture);
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public Class<?> getComponentClass() {
        return componentClass;
    }

    public void setComponentClass(Class<?> componentClass) {
        this.componentClass = componentClass;
    }
}
