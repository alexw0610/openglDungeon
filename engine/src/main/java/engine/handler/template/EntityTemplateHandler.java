package engine.handler.template;

import engine.handler.Handler;
import engine.loader.YamlLoader;
import engine.loader.template.EntityTemplate;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityTemplateHandler implements Handler<EntityTemplate> {
    private static final ThreadLocal<EntityTemplateHandler> INSTANCE = ThreadLocal.withInitial(EntityTemplateHandler::new);
    public static final String ENTITY_TEMPLATE_FOLDER = "./entity/";
    private final Map<String, EntityTemplate> templateMap = new HashMap<>();

    public static EntityTemplateHandler getInstance() {
        return INSTANCE.get();
    }

    private EntityTemplateHandler() {
        try {
            for (Path path : Files.newDirectoryStream(Paths.get(ENTITY_TEMPLATE_FOLDER))) {
                File file = new File(path.toUri());
                addObject(file.getName().split("\\.")[0], YamlLoader.load(EntityTemplate.class, Files.newInputStream(path)));
                System.out.println("Loaded entityTemplate " + file.getName().split("\\.")[0] + " from external path " + path);
            }
        } catch (Exception e) {
            System.err.println("Failed to load entityTemplates from external Path. Error: " + e.getMessage());
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
