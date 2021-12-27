package engine.loader;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

public class YamlLoader {
    public static <T> T load(Class<T> type, String fileName) {
        Yaml yaml = new Yaml(new Constructor(type));
        InputStream inputStream = YamlLoader.class
                .getClassLoader()
                .getResourceAsStream(fileName);
        return yaml.load(inputStream);
    }
}
