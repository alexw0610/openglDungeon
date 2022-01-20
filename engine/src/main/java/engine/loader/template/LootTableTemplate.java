package engine.loader.template;

import java.util.Map;

public class LootTableTemplate {

    private String templateName;
    private Map<String, Double> lootMap;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Map<String, Double> getLootMap() {
        return lootMap;
    }

    public void setLootMap(Map<String, Double> loot) {
        this.lootMap = loot;
    }
}
