package engine.entity;

import engine.component.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Entity implements Serializable {

    private static final long serialVersionUID = 732199433633596042L;
    private String entityId;

    private final Map<Class<? extends Component>, Component> components = new HashMap<>();

    public void addComponent(Component component) {
        components.put(component.getClass(), component);
    }

    public void addIfNotExistsComponent(Component component) {
        components.putIfAbsent(component.getClass(), component);
    }

    public void removeComponent(Class<? extends Component> component) {
        if (hasComponentOfType(component)) {
            getComponentOfType(component).onRemove();
        }
        components.remove(component);
    }

    public boolean hasComponentOfType(Class<? extends Component> component) {
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

    public void onRemove(){
        this.components.values().forEach(Component::onRemove);
        this.components.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return Objects.equals(entityId, entity.entityId) && Objects.equals(components, entity.components);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityId, components);
    }
}
