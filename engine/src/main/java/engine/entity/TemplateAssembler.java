package engine.entity;

import engine.component.Component;
import engine.handler.EntityTemplateHandler;
import engine.loader.template.ComponentTemplate;
import engine.loader.template.EntityTemplate;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class TemplateAssembler {

    static EntityBuilder assembleEntity(EntityTemplate template, EntityBuilder builder) {
        if (StringUtils.isNotBlank(template.getExtendedTemplate())) {
            assembleEntity(EntityTemplateHandler.getInstance().getObject(template.getExtendedTemplate()), builder);
        }
        for (ComponentTemplate componentTemplate : template.getComponents()) {
            builder.withComponent(assembleComponent(componentTemplate));
        }
        return builder;
    }

    static Component assembleComponent(ComponentTemplate componentTemplate) {
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
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        if (componentTemplate.getModifiers() != null && !componentTemplate.getModifiers().isEmpty()) {
            for (Map.Entry<String, Object> entry : componentTemplate.getModifiers().entrySet()) {
                try {
                    Method method = componentClass.getMethod("set" + StringUtils.capitalize(entry.getKey()), entry.getValue().getClass());
                    method.invoke(component, entry.getValue());
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return component;
    }
}
