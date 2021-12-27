package engine.handler;

import engine.loader.YamlLoader;
import engine.loader.template.RoomTemplate;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomTemplateHandler implements Handler<RoomTemplate> {
    private static final RoomTemplateHandler INSTANCE = new RoomTemplateHandler();
    public static final String ROOM_TEMPLATE_FOLDER = "room/";

    private final Map<String, RoomTemplate> templateMap = new HashMap<>();

    public static RoomTemplateHandler getInstance() {
        return INSTANCE;
    }

    private RoomTemplateHandler() {
        File templateDirectory = new File(Thread.currentThread().getContextClassLoader().getResource(ROOM_TEMPLATE_FOLDER).getPath());
        for (File file : templateDirectory.listFiles()) {
            addObject(file.getName().split("\\.")[0], YamlLoader.load(RoomTemplate.class, ROOM_TEMPLATE_FOLDER + file.getName()));
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
