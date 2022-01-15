package engine.loader.template;

import java.util.List;
import java.util.Map;

public class ZoneTemplate {

    private String templateName;
    private String zoneIdTexture;
    private int zoneId;
    private Map<Integer, String> zoneFloorIdMappings;
    private Map<Integer, String> zoneWallIdMappings;
    private List<EntityInstanceTemplate> entities;
    private int spawnPointX;
    private int spawnPointY;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getZoneIdTexture() {
        return zoneIdTexture;
    }

    public void setZoneIdTexture(String zoneIdTexture) {
        this.zoneIdTexture = zoneIdTexture;
    }

    public int getZoneId() {
        return zoneId;
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }

    public Map<Integer, String> getZoneFloorIdMappings() {
        return zoneFloorIdMappings;
    }

    public void setZoneFloorIdMappings(Map<Integer, String> zoneFloorIdMappings) {
        this.zoneFloorIdMappings = zoneFloorIdMappings;
    }

    public Map<Integer, String> getZoneWallIdMappings() {
        return zoneWallIdMappings;
    }

    public void setZoneWallIdMappings(Map<Integer, String> zoneWallIdMappings) {
        this.zoneWallIdMappings = zoneWallIdMappings;
    }

    public int getSpawnPointX() {
        return spawnPointX;
    }

    public void setSpawnPointX(int spawnPointX) {
        this.spawnPointX = spawnPointX;
    }

    public int getSpawnPointY() {
        return spawnPointY;
    }

    public void setSpawnPointY(int spawnPointY) {
        this.spawnPointY = spawnPointY;
    }

    public List<EntityInstanceTemplate> getEntities() {
        return entities;
    }

    public void setEntities(List<EntityInstanceTemplate> entities) {
        this.entities = entities;
    }
}
