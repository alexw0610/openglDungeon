package engine.handler.template;

import engine.handler.Handler;
import engine.loader.YamlLoader;
import engine.loader.template.ComponentTemplate;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentTemplateHandler implements Handler<ComponentTemplate> {
    private static final ThreadLocal<ComponentTemplateHandler> INSTANCE = ThreadLocal.withInitial(ComponentTemplateHandler::new);
    public static final String COMPONENT_TEMPLATE_FOLDER = "./component/";
    private final Map<String, ComponentTemplate> templateMap = new HashMap<>();
    private final Map<Integer, String> itemTypeIdMap = new HashMap<>();

    public static ComponentTemplateHandler getInstance() {
        return INSTANCE.get();
    }

    private ComponentTemplateHandler() {
        try {
            for (Path path : Files.newDirectoryStream(Paths.get(COMPONENT_TEMPLATE_FOLDER))) {
                File file = path.toFile();
                ComponentTemplate componentTemplate = YamlLoader.load(ComponentTemplate.class, Files.newInputStream(path));
                addObject(componentTemplate.getTemplateName(), componentTemplate);
                System.out.println("Loaded componentTemplate " + file.getName().split("\\.")[0] + " from external path " + path);
                if (componentTemplate.getType().equals("ItemComponent")) {
                    this.itemTypeIdMap.put((Integer) componentTemplate.getArguments().get("itemTypeId"), componentTemplate.getTemplateName());
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load componentTemplates from external Path. Error: " + e.getMessage());
        }
    }

    @Override
    public ComponentTemplate getObject(String key) {
        return templateMap.get(key);
    }

    @Override
    public List<ComponentTemplate> getAllObjects() {
        return new ArrayList<>(this.templateMap.values());
    }

    @Override
    public void addObject(String key, ComponentTemplate object) {
        this.templateMap.put(key, object);
    }

    @Override
    public void addObject(ComponentTemplate object) {
    }

    @Override
    public void removeObject(String key) {
        this.templateMap.remove(key);
    }

    public String getNameForItemTypeId(int itemTypeId) {
        return this.itemTypeIdMap.get(itemTypeId);
    }
}
