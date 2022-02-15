package engine.handler.template;

import engine.handler.Handler;
import engine.loader.YamlLoader;
import engine.loader.template.EntityTemplate;

import java.io.File;
import java.net.JarURLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;

public class EntityTemplateHandler implements Handler<EntityTemplate> {
    private static final ThreadLocal<EntityTemplateHandler> INSTANCE = ThreadLocal.withInitial(EntityTemplateHandler::new);
    public static final String ENTITY_TEMPLATE_FOLDER = "entity/";
    private final Map<String, EntityTemplate> templateMap = new HashMap<>();

    public static EntityTemplateHandler getInstance() {
        return INSTANCE.get();
    }

    private EntityTemplateHandler() {
        try {
            Enumeration<JarEntry> entries = ((JarURLConnection) getClass().getClassLoader()
                    .getResource("zone/").toURI().toURL().openConnection())
                    .getJarFile().entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().startsWith(ENTITY_TEMPLATE_FOLDER) && entry.getName().endsWith(".yaml")) {
                    String filename = entry.getName().split("/")[1];
                    addObject(filename.split("\\.")[0], YamlLoader.load(EntityTemplate.class, ENTITY_TEMPLATE_FOLDER + filename));
                }
            }
        } catch (Exception e) {
            File templateDirectory = new File(Thread.currentThread().getContextClassLoader().getResource(ENTITY_TEMPLATE_FOLDER).getPath());
            for (File file : templateDirectory.listFiles()) {
                addObject(file.getName().split("\\.")[0], YamlLoader.load(EntityTemplate.class, ENTITY_TEMPLATE_FOLDER + file.getName()));
            }
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
