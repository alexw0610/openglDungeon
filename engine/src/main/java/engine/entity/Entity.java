package engine.entity;

import engine.component.Component;

import java.util.ArrayList;
import java.util.List;

public class Entity {

    private final List<Component> components = new ArrayList<>();

    public void addComponent(Component component) {
        if (!hasComponentOfType(component.getClass())) {
            this.components.add(component);
        }
    }

/*
    public void removeComponent(Class component) {
        for (Component comp : this.components) {
            if (component.isInstance(comp)) {
                return true;
            }
        }
    }
    */

    public boolean hasComponentOfType(Class component) {
        for (Component comp : this.components) {
            if (component.isInstance(comp)) {
                return true;
            }
        }
        return false;
    }

    public <T> T getComponentOfType(Class component) {
        for (Component comp : this.components) {
            if (component.isInstance(comp)) {
                return (T) comp;
            }
        }
        return null;
    }
}
