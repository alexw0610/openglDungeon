package engine.entity;

import engine.component.Component;

import java.util.HashMap;
import java.util.Map;

public class Entity {

    private String entityId;

    private final Map<Class, Component> components = new HashMap<>();

    public void addComponent(Component component) {
        components.put(component.getClass(), component);
    }

    public void removeComponent(Class component) {
        components.remove(component);
    }

    public boolean hasComponentOfType(Class component) {
        return components.containsKey(component);
    }

    public <T> T getComponentOfType(Class<T> component) {
        return (T) components.get(component);
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

}
