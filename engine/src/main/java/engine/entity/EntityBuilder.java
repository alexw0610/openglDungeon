package engine.entity;

import engine.component.Component;

public class EntityBuilder {

    private final Entity entity;

    private EntityBuilder() {
        this.entity = new Entity();
    }

    public static EntityBuilder builder() {
        return new EntityBuilder();
    }

    public EntityBuilder withComponent(Component... component) {
        for (Component comp : component) {
            withComponent(comp);
        }
        return this;
    }

    public EntityBuilder withComponent(Component component) {
        this.entity.addComponent(component);
        return this;
    }

    public Entity build() {
        return this.entity;
    }
}
