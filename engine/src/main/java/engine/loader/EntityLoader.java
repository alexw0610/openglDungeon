package engine.loader;

import engine.entity.EntityTemplate;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

public class EntityLoader {
    public static final String RESOURCE_ENTITY_SUBFOLDER = "entity/";
    public static final String DEFAULT_ENTITY_FILE_EXTENSION = ".yaml";

    public static EntityTemplate loadEntity(String fileName) {
        Yaml yaml = new Yaml(new Constructor(EntityTemplate.class));
        InputStream inputStream = EntityLoader.class
                .getClassLoader()
                .getResourceAsStream(RESOURCE_ENTITY_SUBFOLDER + fileName + DEFAULT_ENTITY_FILE_EXTENSION);
        EntityTemplate template = yaml.load(inputStream);
        System.out.println(template);
        return template;
    }
}
