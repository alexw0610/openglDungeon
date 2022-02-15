package engine.service;

import engine.entity.EntityBuilder;
import engine.handler.NavHandler;
import engine.handler.template.RoomTemplateHandler;
import engine.handler.template.ZoneTemplateHandler;
import engine.loader.TextureLoader;
import engine.loader.template.EntityInstanceTemplate;
import engine.loader.template.RoomTemplate;
import engine.loader.template.ZoneTemplate;
import engine.object.TileMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.joml.Random;
import org.joml.Vector2d;

import java.awt.image.BufferedImage;

public class ZoneGenerator {

    private static final String DUNGEON_ENTITY_PREFIX = "DUNGEON_ENTITY_";

    public static Vector2d generate(String zoneTemplateKey) {
        ZoneTemplate template = ZoneTemplateHandler.getInstance().getObject(zoneTemplateKey);
        BufferedImage zoneIdTexture = TextureLoader.loadTextureBuffer(template.getZoneIdTexture());
        TileMap tileMap = new TileMap(zoneIdTexture.getWidth(), template.getZoneId());
        int[] temp = new int[zoneIdTexture.getWidth() * zoneIdTexture.getHeight()];
        zoneIdTexture.getRGB(0, 0, zoneIdTexture.getWidth(), zoneIdTexture.getHeight(), temp, 0, zoneIdTexture.getWidth());
        RoomTemplateHandler.getInstance().removeObjectsWithPrefix(DUNGEON_ENTITY_PREFIX);
        for (int y = 0; y < zoneIdTexture.getHeight(); ++y) {
            for (int x = 0; x < zoneIdTexture.getWidth(); ++x) {
                int pixel = temp[y + x * zoneIdTexture.getWidth()];
                int id = (((pixel >> 16) & 0xFF));
                if (id != 0) {
                    RoomTemplate tempTemplate = new RoomTemplate();
                    tempTemplate.setFloorTextureKey(template.getZoneFloorIdMappings().get(id));
                    tempTemplate.setWallTextureKey(template.getZoneWallIdMappings().get(id));
                    String roomTemplateKey = DUNGEON_ENTITY_PREFIX + id;
                    RoomTemplateHandler.getInstance().addObject(roomTemplateKey, tempTemplate);
                    tileMap.setTile(y, zoneIdTexture.getWidth() - x, roomTemplateKey);
                }
            }
        }
        tileMap.initMap(new Random(template.getZoneId()));
        for (EntityInstanceTemplate entity : template.getEntities()) {
            EntityBuilder.builder().fromTemplate(entity.getTemplateName())
                    .at(entity.getX(), entity.getY())
                    .buildAndInstantiate(DUNGEON_ENTITY_PREFIX + RandomStringUtils.randomAlphanumeric(8));
        }
        NavHandler.getInstance().setNavMap(tileMap.getNavMap());
        return new Vector2d(template.getSpawnPointX(), template.getSpawnPointY());
    }
}
