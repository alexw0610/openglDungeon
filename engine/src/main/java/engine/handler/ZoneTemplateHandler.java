package engine.handler;

import engine.loader.YamlLoader;
import engine.loader.template.ZoneTemplate;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZoneTemplateHandler implements Handler<ZoneTemplate> {
    private static final ZoneTemplateHandler INSTANCE = new ZoneTemplateHandler();
    public static final String ZONE_TEMPLATE_FOLDER = "zone/";

    private final Map<String, ZoneTemplate> templateMap = new HashMap<>();

    public static ZoneTemplateHandler getInstance() {
        return INSTANCE;
    }

    private ZoneTemplateHandler() {
        File templateDirectory = new File(Thread.currentThread().getContextClassLoader().getResource(ZONE_TEMPLATE_FOLDER).getPath());
        for (File file : templateDirectory.listFiles()) {
            addObject(file.getName().split("\\.")[0], YamlLoader.load(ZoneTemplate.class, ZONE_TEMPLATE_FOLDER + file.getName()));
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
