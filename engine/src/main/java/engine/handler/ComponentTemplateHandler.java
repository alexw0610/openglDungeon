package engine.handler;

import engine.entity.ComponentTemplate;
import engine.loader.ComponentLoader;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentTemplateHandler implements Handler<ComponentTemplate> {
    private static final ComponentTemplateHandler INSTANCE = new ComponentTemplateHandler();
    private final Map<String, ComponentTemplate> templateMap = new HashMap<>();

    public static ComponentTemplateHandler getInstance() {
        return INSTANCE;
    }

    private ComponentTemplateHandler() {
        File templateDirectory = new File(Thread.currentThread().getContextClassLoader().getResource(ComponentLoader.RESOURCE_ENTITY_SUBFOLDER).getPath());
        for (File file : templateDirectory.listFiles()) {
            addObject(file.getName().split("\\.")[0], ComponentLoader.loadComponent(file.getName().split("\\.")[0]));
        }
    }

    @Override
    public ComponentTemplate getObject(String key) {
        return templateMap.get(key);
    }

    @Override
    public List<ComponentTemplate> getAllObjects() {
        return null;
    }

    @Override
    public void addObject(String key, ComponentTemplate object) {
        this.templateMap.put(key, object);
    }

    @Override
    public void addObject(ComponentTemplate object) {
    }

    @Override
    public void removeObject(String key) {
        this.templateMap.remove(key);
    }
}
