package engine.loader.template;

import java.util.Map;

public class RoomTemplate {

    private String templateName;
    private String floorTextureKey;
    private String wallTextureKey;
    private Map<String, Double> roomFloorEntityTemplates;
    private Map<String, Double> roomWallEntityTemplates;
    private Map<String, Double> roomHostileEntityTemplates;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getFloorTextureKey() {
        return floorTextureKey;
    }

    public void setFloorTextureKey(String floorTextureKey) {
        this.floorTextureKey = floorTextureKey;
    }

    public String getWallTextureKey() {
        return wallTextureKey;
    }

    public void setWallTextureKey(String wallTextureKey) {
        this.wallTextureKey = wallTextureKey;
    }

    public Map<String, Double> getRoomFloorEntityTemplates() {
        return roomFloorEntityTemplates;
    }

    public void setRoomFloorEntityTemplates(Map<String, Double> roomFloorEntityTemplates) {
        this.roomFloorEntityTemplates = roomFloorEntityTemplates;
    }

    public Map<String, Double> getRoomWallEntityTemplates() {
        return roomWallEntityTemplates;
    }

    public void setRoomWallEntityTemplates(Map<String, Double> roomWallEntityTemplates) {
        this.roomWallEntityTemplates = roomWallEntityTemplates;
    }

    public Map<String, Double> getRoomHostileEntityTemplates() {
        return roomHostileEntityTemplates;
    }

    public void setRoomHostileEntityTemplates(Map<String, Double> roomHostileEntityTemplates) {
        this.roomHostileEntityTemplates = roomHostileEntityTemplates;
    }
}
