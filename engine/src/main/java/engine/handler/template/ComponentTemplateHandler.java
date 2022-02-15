package engine.handler.template;

import engine.handler.Handler;
import engine.loader.YamlLoader;
import engine.loader.template.ComponentTemplate;

import java.io.File;
import java.net.JarURLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;

public class ComponentTemplateHandler implements Handler<ComponentTemplate> {
    private static final ThreadLocal<ComponentTemplateHandler> INSTANCE = ThreadLocal.withInitial(ComponentTemplateHandler::new);
    public static final String COMPONENT_TEMPLATE_FOLDER = "component/";
    private final Map<String, ComponentTemplate> templateMap = new HashMap<>();

    public static ComponentTemplateHandler getInstance() {
        return INSTANCE.get();
    }

    private ComponentTemplateHandler() {
        try {
            Enumeration<JarEntry> entries = ((JarURLConnection) getClass().getClassLoader()
                    .getResource("zone/").toURI().toURL().openConnection())
                    .getJarFile().entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().startsWith(COMPONENT_TEMPLATE_FOLDER) && entry.getName().endsWith(".yaml")) {
                    String filename = entry.getName().split("/")[1];
                    addObject(filename.split("\\.")[0], YamlLoader.load(ComponentTemplate.class, COMPONENT_TEMPLATE_FOLDER + filename));
                }
            }
        } catch (Exception e) {
            File templateDirectory = new File(Thread.currentThread().getContextClassLoader().getResource(COMPONENT_TEMPLATE_FOLDER).getPath());
            for (File file : templateDirectory.listFiles()) {
                addObject(file.getName().split("\\.")[0], YamlLoader.load(ComponentTemplate.class, COMPONENT_TEMPLATE_FOLDER + file.getName()));
            }
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
