package engine.loader;

import engine.loader.template.RoomTemplate;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

public class RoomLoader {
    public static final String RESOURCE_ENTITY_SUBFOLDER = "room/";
    public static final String DEFAULT_ENTITY_FILE_EXTENSION = ".yaml";

    public static RoomTemplate loadRoom(String fileName) {
        Yaml yaml = new Yaml(new Constructor(RoomTemplate.class));
        InputStream inputStream = RoomLoader.class
                .getClassLoader()
                .getResourceAsStream(RESOURCE_ENTITY_SUBFOLDER + fileName + DEFAULT_ENTITY_FILE_EXTENSION);
        return yaml.load(inputStream);
    }
}
