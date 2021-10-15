package engine.loader;

import engine.loader.template.DungeonTemplate;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

public class DungeonLoader {
    public static final String RESOURCE_ENTITY_SUBFOLDER = "dungeon/";
    public static final String DEFAULT_ENTITY_FILE_EXTENSION = ".yaml";

    public static DungeonTemplate loadDungeon(String fileName) {
        Yaml yaml = new Yaml(new Constructor(DungeonTemplate.class));
        InputStream inputStream = DungeonLoader.class
                .getClassLoader()
                .getResourceAsStream(RESOURCE_ENTITY_SUBFOLDER + fileName + DEFAULT_ENTITY_FILE_EXTENSION);
        return yaml.load(inputStream);
    }
}
