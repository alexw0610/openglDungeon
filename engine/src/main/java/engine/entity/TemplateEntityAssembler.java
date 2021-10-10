package engine.entity;

import engine.component.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class TemplateEntityAssembler {

    static EntityBuilder fromTemplate(EntityTemplate template, EntityBuilder builder) {
        for (ComponentTemplate componentTemplate : template.getComponents()) {
            Class<Component> componentClass = null;
            try {
                componentClass = (Class<Component>) Class.forName("engine.component." + componentTemplate.getType());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            Constructor constructor = null;
            try {
                if (componentTemplate.getArguments() != null && !componentTemplate.getArguments().isEmpty()) {
                    List<Class> constructorArguments = componentTemplate.getArguments().values().stream().map(Object::getClass).collect(Collectors.toList());
                    Class[] arguments = new Class[constructorArguments.size()];
                    constructorArguments.toArray(arguments);
                    constructor = componentClass.getConstructor(arguments);
                } else {
                    constructor = componentClass.getConstructor();
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            Component component = null;
            try {
                if (componentTemplate.getArguments() != null && !componentTemplate.getArguments().isEmpty()) {
                    List<Object> constructorArguments = new ArrayList<>(componentTemplate.getArguments().values());
                    Object[] arguments = new Object[constructorArguments.size()];
                    constructorArguments.toArray(arguments);
                    component = componentClass.cast(constructor.newInstance(arguments));
                } else {
                    component = componentClass.cast(constructor.newInstance());
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            builder.withComponent(component);
        }
        return builder;
    }
}
