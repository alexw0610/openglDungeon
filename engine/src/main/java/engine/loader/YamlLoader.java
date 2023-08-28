package engine.loader;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

public class YamlLoader {
    public static <T> T load(Class<T> type, InputStream inputStream) {
        Yaml yaml = new Yaml(new Constructor(type, new LoaderOptions()));
        return yaml.load(inputStream);
    }
}
