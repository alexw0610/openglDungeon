package engine.handler;

import engine.loader.EntityLoader;
import engine.loader.template.EntityTemplate;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityTemplateHandler implements Handler<EntityTemplate> {
    private static final EntityTemplateHandler INSTANCE = new EntityTemplateHandler();
    private final Map<String, EntityTemplate> templateMap = new HashMap<>();

    public static EntityTemplateHandler getInstance() {
        return INSTANCE;
    }

    private EntityTemplateHandler() {
        File templateDirectory = new File(Thread.currentThread().getContextClassLoader().getResource(EntityLoader.RESOURCE_ENTITY_SUBFOLDER).getPath());
        for (File file : templateDirectory.listFiles()) {
            addObject(file.getName().split("\\.")[0], EntityLoader.loadEntity(file.getName().split("\\.")[0]));
        }
    }

    @Override
    public EntityTemplate getObject(String key) {
        return templateMap.get(key);
    }

    @Override
    public List<EntityTemplate> getAllObjects() {
        return null;
    }

    @Override
    public void addObject(String key, EntityTemplate object) {
        this.templateMap.put(key, object);
    }

    @Override
    public void addObject(EntityTemplate object) {
    }

    @Override
    public void removeObject(String key) {
        this.templateMap.remove(key);
    }
}
