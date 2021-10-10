package engine.entity;

import engine.component.Component;
import engine.handler.EntityHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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

    public EntityBuilder withTag(Class<? extends Component> tag) {
        Constructor<? extends Component> defaultConstructor;
        try {
            defaultConstructor = tag.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            System.err.println("No default constructor available on class: " + tag.getName());
            return this;
        }
        Component component;
        try {
            component = defaultConstructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            System.err.println("Failed to create instance with default constructor of class: " + tag.getName());
            return this;
        }
        this.entity.addComponent(component);
        return this;
    }

    public EntityBuilder withComponent(Component component) {
        this.entity.addComponent(component);
        return this;
    }

    public Entity buildAndInstantiate() {
        EntityHandler.getInstance().addObject(this.entity);
        return this.entity;
    }

    public Entity build() {
        return this.entity;
    }

    public EntityBuilder fromTemplate(EntityTemplate template) {
        return TemplateEntityAssembler.fromTemplate(template, this);
    }
}
