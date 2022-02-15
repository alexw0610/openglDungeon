package engine.handler.template;

import engine.handler.Handler;
import engine.loader.YamlLoader;
import engine.loader.template.RoomTemplate;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.net.JarURLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.stream.Collectors;

public class RoomTemplateHandler implements Handler<RoomTemplate> {
    private static final ThreadLocal<RoomTemplateHandler> INSTANCE = ThreadLocal.withInitial(RoomTemplateHandler::new);
    public static final String ROOM_TEMPLATE_FOLDER = "room/";

    private final Map<String, RoomTemplate> templateMap = new HashMap<>();

    public static RoomTemplateHandler getInstance() {
        return INSTANCE.get();
    }

    private RoomTemplateHandler() {
        try {
            Enumeration<JarEntry> entries = ((JarURLConnection) getClass().getClassLoader()
                    .getResource("zone/").toURI().toURL().openConnection())
                    .getJarFile().entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().startsWith(ROOM_TEMPLATE_FOLDER) && entry.getName().endsWith(".yaml")) {
                    String filename = entry.getName().split("/")[1];
                    addObject(filename.split("\\.")[0], YamlLoader.load(RoomTemplate.class, ROOM_TEMPLATE_FOLDER + filename));
                }
            }
        } catch (Exception e) {
            File templateDirectory = new File(Thread.currentThread().getContextClassLoader().getResource(ROOM_TEMPLATE_FOLDER).getPath());
            for (File file : templateDirectory.listFiles()) {
                addObject(file.getName().split("\\.")[0], YamlLoader.load(RoomTemplate.class, ROOM_TEMPLATE_FOLDER + file.getName()));
            }
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
        if (!this.templateMap.containsKey(key)) {
            this.templateMap.put(key, object);
        }
    }

    @Override
    public void addObject(RoomTemplate object) {
        synchronized (this.templateMap) {
            String key = RandomStringUtils.randomAlphanumeric(16);
            this.templateMap.put(key, object);
        }
    }

    public void removeObjectsWithPrefix(String prefix) {
        for (String key : this.templateMap.keySet().stream().filter(key -> key.startsWith(prefix)).collect(Collectors.toList())) {
            removeObject(key);
        }
    }

    @Override
    public void removeObject(String key) {
        this.templateMap.remove(key);
    }
}
