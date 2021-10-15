package engine.handler;

import engine.loader.RoomLoader;
import engine.loader.template.RoomTemplate;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomTemplateHandler implements Handler<RoomTemplate> {
    private static final RoomTemplateHandler INSTANCE = new RoomTemplateHandler();
    private final Map<String, RoomTemplate> templateMap = new HashMap<>();

    public static RoomTemplateHandler getInstance() {
        return INSTANCE;
    }

    private RoomTemplateHandler() {
        File templateDirectory = new File(Thread.currentThread().getContextClassLoader().getResource(RoomLoader.RESOURCE_ENTITY_SUBFOLDER).getPath());
        for (File file : templateDirectory.listFiles()) {
            addObject(file.getName().split("\\.")[0], RoomLoader.loadRoom(file.getName().split("\\.")[0]));
        }
    }

    @Override
    public RoomTemplate getObject(String key) {
        return templateMap.get(key);
    }

    @Override
    public List<RoomTemplate> getAllObjects() {
        return null;
    }

    @Override
    public void addObject(String key, RoomTemplate object) {
        this.templateMap.put(key, object);
    }

    @Override
    public void addObject(RoomTemplate object) {
    }

    @Override
    public void removeObject(String key) {
        this.templateMap.remove(key);
    }
}
