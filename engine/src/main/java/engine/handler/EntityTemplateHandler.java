package engine.handler;

import engine.loader.YamlLoader;
import engine.loader.template.EntityTemplate;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityTemplateHandler implements Handler<EntityTemplate> {
    private static final EntityTemplateHandler INSTANCE = new EntityTemplateHandler();
    public static final String ENTITY_TEMPLATE_FOLDER = "entity/";
    private final Map<String, EntityTemplate> templateMap = new HashMap<>();

    public static EntityTemplateHandler getInstance() {
        return INSTANCE;
    }

    private EntityTemplateHandler() {
        File templateDirectory = new File(Thread.currentThread().getContextClassLoader().getResource(ENTITY_TEMPLATE_FOLDER).getPath());
        for (File file : templateDirectory.listFiles()) {
            addObject(file.getName().split("\\.")[0], YamlLoader.load(EntityTemplate.class, ENTITY_TEMPLATE_FOLDER + file.getName()));
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
