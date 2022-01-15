package engine.loader.template;

import java.util.List;
import java.util.Map;

public class DungeonTemplate {

    private String templateName;
    private Map<String, Double> dungeonRoomTemplates;
    private List<EntityInstanceTemplate> globalEntities;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Map<String, Double> getDungeonRoomTemplates() {
        return dungeonRoomTemplates;
    }

    public void setDungeonRoomTemplates(Map<String, Double> dungeonRoomTemplates) {
        this.dungeonRoomTemplates = dungeonRoomTemplates;
    }

    public List<EntityInstanceTemplate> getGlobalEntities() {
        return globalEntities;
    }

    public void setGlobalEntities(List<EntityInstanceTemplate> globalEntities) {
        this.globalEntities = globalEntities;
    }
}
