package engine.loader;

import engine.entity.ComponentTemplate;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

public class ComponentLoader {
    public static final String RESOURCE_ENTITY_SUBFOLDER = "component/";
    public static final String DEFAULT_ENTITY_FILE_EXTENSION = ".yaml";

    public static ComponentTemplate loadComponent(String fileName) {
        Yaml yaml = new Yaml(new Constructor(ComponentTemplate.class));
        InputStream inputStream = ComponentLoader.class
                .getClassLoader()
                .getResourceAsStream(RESOURCE_ENTITY_SUBFOLDER + fileName + DEFAULT_ENTITY_FILE_EXTENSION);
        return yaml.load(inputStream);
    }
}
