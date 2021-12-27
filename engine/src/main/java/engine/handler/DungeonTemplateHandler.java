package engine.handler;

import engine.loader.YamlLoader;
import engine.loader.template.DungeonTemplate;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DungeonTemplateHandler implements Handler<DungeonTemplate> {
    private static final DungeonTemplateHandler INSTANCE = new DungeonTemplateHandler();
    public static final String DUNGEON_TEMPLATE_FOLDER = "dungeon/";
    private final Map<String, DungeonTemplate> templateMap = new HashMap<>();

    public static DungeonTemplateHandler getInstance() {
        return INSTANCE;
    }

    private DungeonTemplateHandler() {
        File templateDirectory = new File(Thread.currentThread().getContextClassLoader().getResource(DUNGEON_TEMPLATE_FOLDER).getPath());
        for (File file : templateDirectory.listFiles()) {
            addObject(file.getName().split("\\.")[0], YamlLoader.load(DungeonTemplate.class, DUNGEON_TEMPLATE_FOLDER + file.getName()));
        }
    }

    @Override
    public DungeonTemplate getObject(String key) {
        return templateMap.get(key);
    }

    @Override
    public List<DungeonTemplate> getAllObjects() {
        return null;
    }

    @Override
    public void addObject(String key, DungeonTemplate object) {
        this.templateMap.put(key, object);
    }

    @Override
    public void addObject(DungeonTemplate object) {
    }

    @Override
    public void removeObject(String key) {
        this.templateMap.remove(key);
    }
}
