package engine.handler.template;

import engine.handler.Handler;
import engine.loader.YamlLoader;
import engine.loader.template.ZoneTemplate;

import java.io.File;
import java.net.JarURLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;

public class ZoneTemplateHandler implements Handler<ZoneTemplate> {
    private static final ThreadLocal<ZoneTemplateHandler> INSTANCE = ThreadLocal.withInitial(ZoneTemplateHandler::new);
    public static final String ZONE_TEMPLATE_FOLDER = "zone/";

    private final Map<String, ZoneTemplate> templateMap = new HashMap<>();

    public static ZoneTemplateHandler getInstance() {
        return INSTANCE.get();
    }

    private ZoneTemplateHandler() {
        try {
            Enumeration<JarEntry> entries = ((JarURLConnection) getClass().getClassLoader()
                    .getResource("zone/").toURI().toURL().openConnection())
                    .getJarFile().entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().startsWith(ZONE_TEMPLATE_FOLDER) && entry.getName().endsWith(".yaml")) {
                    String filename = entry.getName().split("/")[1];
                    ZoneTemplate template = YamlLoader.load(ZoneTemplate.class, ZONE_TEMPLATE_FOLDER + filename);
                    addObject(String.valueOf(template.getZoneId()), template);
                }
            }
        } catch (Exception e) {
            File templateDirectory = new File(Thread.currentThread().getContextClassLoader().getResource(ZONE_TEMPLATE_FOLDER).getPath());
            for (File file : templateDirectory.listFiles()) {
                ZoneTemplate template = YamlLoader.load(ZoneTemplate.class, ZONE_TEMPLATE_FOLDER + file.getName());
                addObject(String.valueOf(template.getZoneId()), template);
            }
        }
    }

    @Override
    public ZoneTemplate getObject(String key) {
        return templateMap.get(key);
    }

    @Override
    public List<ZoneTemplate> getAllObjects() {
        return null;
    }

    @Override
    public void addObject(String key, ZoneTemplate object) {
        this.templateMap.put(key, object);
    }

    @Override
    public void addObject(ZoneTemplate object) {
    }

    @Override
    public void removeObject(String key) {
        this.templateMap.remove(key);
    }
}
