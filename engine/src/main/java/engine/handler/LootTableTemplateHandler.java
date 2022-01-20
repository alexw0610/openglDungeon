package engine.handler;

import engine.loader.YamlLoader;
import engine.loader.template.LootTableTemplate;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LootTableTemplateHandler implements Handler<LootTableTemplate> {
    private static final LootTableTemplateHandler INSTANCE = new LootTableTemplateHandler();
    public static final String LOOT_TABLE_TEMPLATE_FOLDER = "lootTable/";

    private final Map<String, LootTableTemplate> templateMap = new HashMap<>();

    public static LootTableTemplateHandler getInstance() {
        return INSTANCE;
    }

    private LootTableTemplateHandler() {
        File templateDirectory = new File(Thread.currentThread().getContextClassLoader().getResource(LOOT_TABLE_TEMPLATE_FOLDER).getPath());
        for (File file : templateDirectory.listFiles()) {
            addObject(file.getName().split("\\.")[0], YamlLoader.load(LootTableTemplate.class, LOOT_TABLE_TEMPLATE_FOLDER + file.getName()));
        }
    }

    @Override
    public LootTableTemplate getObject(String key) {
        return templateMap.get(key);
    }

    @Override
    public List<LootTableTemplate> getAllObjects() {
        return null;
    }

    @Override
    public void addObject(String key, LootTableTemplate object) {
        if (!this.templateMap.containsKey(key)) {
            this.templateMap.put(key, object);
        }
    }

    @Override
    public void addObject(LootTableTemplate object) {
        synchronized (this.templateMap) {
            String key = RandomStringUtils.randomAlphanumeric(16);
            this.templateMap.put(key, object);
        }
    }

    @Override
    public void removeObject(String key) {
        this.templateMap.remove(key);
    }
}
