package engine.service;

import engine.EntityKeyConstants;
import engine.component.StatComponent;
import engine.component.base.CollisionComponent;
import engine.component.base.LightSourceComponent;
import engine.component.base.RenderComponent;
import engine.component.base.TransformationComponent;
import engine.component.tag.ShadowCastTag;
import engine.component.tag.ViewBlockingTag;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.WorldTileType;
import engine.enums.ZoneType;
import engine.handler.EntityHandler;
import engine.object.generation.World;
import org.apache.commons.lang3.RandomStringUtils;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3d;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static engine.EntityKeyConstants.CLUTTER_ENTITY_PREFIX;
import static engine.service.util.WorldGenerationUtil.getTextureOffsetForOrientation;

public class WorldGenerator {

    private static final float INITIAL_FILL_CHANCE = 0.42f;
    private static final int GENERATION_STEPS = 3;
    private static final int UPPER_DEATH_LIMIT = 8;
    private static final int LOWER_DEATH_LIMIT = 2;
    private static final int COME_ALIVE_VALUE = 5;
    private static final int maxZones = 3;
    private static final int maxZoneSize = 6;

    public static World generateLevel(int level) {
        int[][] generationMap = new int[48][48];
        initializeMap(generationMap);
        for (int i = 0; i < GENERATION_STEPS; i++) {
            doGenerationStep(generationMap);
        }
        World world = new World(48, 48);
        generateTiles(world, generationMap);
        generateTileEntities(world, "floor", "rock");
        generateZones(world, level);
        EntityHandler.getInstance().setWorld(world);
        return world;
    }

    private static void generateZones(World world, int level) {
        int zones = 0;
        List<ZoneType> zoneTypes = Arrays.stream(ZoneType.values())
                .filter(zoneType -> zoneType.value() <= level)
                .collect(Collectors.toList());
        while (zones < maxZones) {
            int xNodePosition = (int) (Math.random() * world.worldSizeX);
            int yNodePosition = (int) (Math.random() * world.worldSizeY);
            int zoneIndex = (int) Math.abs(Math.random() * zoneTypes.size());
            ZoneType targetZoneType = zoneTypes.get(zoneIndex);
            if (world.isWalkable(xNodePosition, yNodePosition)
                    && nextToWall(world, xNodePosition, yNodePosition)
                    && getCaveSize(xNodePosition, yNodePosition, world) > 20) {
                for (int x = xNodePosition - maxZoneSize / 2; x < xNodePosition + maxZoneSize / 2; x++) {
                    for (int y = yNodePosition - maxZoneSize / 2; y < yNodePosition + maxZoneSize / 2; y++) {
                        if (targetZoneType.equals(ZoneType.RUBBLE_ZONE)) {
                            spawnEntity(world, x, y, "rubble", 0.5, false);
                        }
                        if (targetZoneType.equals(ZoneType.HIVE_ZONE)) {
                            spawnLightSource(xNodePosition, x, yNodePosition, y, 77.0, 169.0, 62.0);
                            spawnEntity(world, x, y, "alien_egg", 0.1, false);
                            changeTexture(world, WorldTileType.GROUND, x, y, 0.6, "floor_alien");
                            changeTexture(world, WorldTileType.ROCK, x, y, 1.0, "rock_alien");
                        }
                        if (targetZoneType.equals(ZoneType.STRANGLE_ZONE)) {
                            spawnLightSource(xNodePosition, x, yNodePosition, y, 58.0, 123.0, 118.0);
                            spawnEntity(world, x, y, "strangle_vine", 0.1, false);
                            spawnEntity(world, x, y, "cave_plant", 0.6, true);
                        }
                        if (targetZoneType.equals(ZoneType.FIRE_ZONE)) {
                            spawnLightSource(xNodePosition, x, yNodePosition, y, 159.0, 97.0, 82.0);
                            spawnEntity(world, x, y, "fire_grass", 0.6, true);
                        }
                    }
                }
                zones++;
            }
        }

    }

    private static void spawnLightSource(int xNodePosition, int x, int yNodePosition, int y, double r, double g, double b) {
        if (xNodePosition == x && yNodePosition == y) {
            Entity lightSource = EntityBuilder.builder().fromTemplate("light").at(xNodePosition, yNodePosition).build();
            lightSource.getComponentOfType(LightSourceComponent.class)
                    .setLightColor(new Vector3d(r / 255.0, g / 255.0, b / 255.0));
            EntityHandler.getInstance().addObject(CLUTTER_ENTITY_PREFIX + RandomStringUtils.randomAlphanumeric(8), lightSource);
        }
    }

    private static void spawnEntity(World world, int x, int y, String entityTemplateKey, double chance, boolean offsetPosition) {
        if (Math.random() <= chance && world.isWalkable(x, y)) {
            Entity entity = EntityBuilder.builder().fromTemplate(entityTemplateKey).at(x, y)
                    .buildAndInstantiate(CLUTTER_ENTITY_PREFIX + RandomStringUtils.randomAlphanumeric(8));
            if (offsetPosition) {
                Vector2d position = entity.getComponentOfType(TransformationComponent.class).getPosition();
                entity.getComponentOfType(TransformationComponent.class).setPositionX(position.x() + (Math.random() - 0.5));
                entity.getComponentOfType(TransformationComponent.class).setPositionY(position.y() + (Math.random() - 0.5));
            }
        }
    }

    private static void changeTexture(World world, WorldTileType targetWorldTileType, int x, int y, double changeChance, String textureKey) {
        if (Math.random() <= changeChance && world.getTileType(x, y).equals(targetWorldTileType)) {
            world.getTile(x, y).getEntity().getComponentOfType(RenderComponent.class).setTextureKey(textureKey);
        }
    }

    private static boolean nextToWall(World world, int xNodePosition, int yNodePosition) {
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                if (!world.isWalkable(xNodePosition + x, yNodePosition + y)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static World generateSafeRoom() {
        int[][] generationMap = new int[10][16];
        World world = new World(10, 16);
        generateTiles(world, generationMap);
        generateTileEntities(world, "floor", "wall_space");
        return world;
    }

    public static World generateBossRoom() {
        int[][] generationMap = new int[32][32];
        initializeMap(generationMap);
        for (int i = 0; i < GENERATION_STEPS; i++) {
            doGenerationStep(generationMap);
        }
        clearCenter(generationMap);
        World world = new World(32, 32);
        generateTiles(world, generationMap);
        generateTileEntities(world, "floor", "rock");
        return world;
    }

    private static void clearCenter(int[][] generationMap) {
        for (int x = 0; x < 24; x++) {
            for (int y = 0; y < 24; y++) {
                if (new Vector2i(x, y).distance(new Vector2i(16, 16)) < 8) {
                    generationMap[x][y] = 0;
                }
                if (new Vector2i(x, y).distance(new Vector2i(16, 0)) < 8) {
                    generationMap[x][y] = 0;
                }
            }
        }
    }

    public static void setPlayerSpawnPosition(Entity entity, World world) {
        for (int x = world.worldSizeX / 2; x < world.worldSizeY; x++) {
            for (int y = world.worldSizeX / 2; y < world.worldSizeY; y++) {
                if (world.getTile(x, y).getWorldTileType().equals(WorldTileType.GROUND) && getCaveSize(x, y, world) > 16 && Math.random() > 0.8f) {
                    entity.getComponentOfType(TransformationComponent.class).setPositionX(x);
                    entity.getComponentOfType(TransformationComponent.class).setPositionY(y);
                    return;
                }
            }
        }
    }

    private static void doGenerationStep(int[][] generationMap) {
        int[][] valueMap = new int[generationMap.length][generationMap[0].length];
        for (int x = 0; x < generationMap.length; x++) {
            for (int y = 0; y < generationMap[0].length; y++) {
                valueMap[x][y] = getNeighbourCount(x, y, generationMap);
            }
        }
        for (int x = 0; x < generationMap.length; x++) {
            for (int y = 0; y < generationMap[0].length; y++) {
                if (generationMap[x][y] == 1) {
                    if (valueMap[x][y] >= UPPER_DEATH_LIMIT || valueMap[x][y] <= LOWER_DEATH_LIMIT) {
                        generationMap[x][y] = 0;
                    }
                } else {
                    if (valueMap[x][y] == COME_ALIVE_VALUE) {
                        generationMap[x][y] = 1;
                    }
                }
            }
        }
    }

    public static int getCaveSize(int x, int y, World world) {
        return getCaveSize(x, y, world, new LinkedList<>(), 0);
    }

    private static int getCaveSize(int x, int y, World world, List<Vector2i> visitedNodes, int openSpaces) {
        visitedNodes.add(new Vector2i(x, y));
        if (visitedNodes.size() > 32) {
            return openSpaces;
        }
        if (!world.isWalkable(x, y)) {
            return 0;
        }
        if (world.isWalkable(x + 1, y) && !visitedNodes.contains(new Vector2i(x + 1, y))) {
            openSpaces += getCaveSize(x + 1, y, world, visitedNodes, openSpaces);
        }
        if (world.isWalkable(x - 1, y) && !visitedNodes.contains(new Vector2i(x - 1, y))) {
            openSpaces += getCaveSize(x - 1, y, world, visitedNodes, openSpaces);
        }
        if (world.isWalkable(x, y + 1) && !visitedNodes.contains(new Vector2i(x, y + 1))) {
            openSpaces += getCaveSize(x, y + 1, world, visitedNodes, openSpaces);
        }
        if (world.isWalkable(x, y - 1) && !visitedNodes.contains(new Vector2i(x, y - 1))) {
            openSpaces += getCaveSize(x, y - 1, world, visitedNodes, openSpaces);
        }
        openSpaces += 1;
        return openSpaces;
    }

    private static int getNeighbourCount(int x, int y, int[][] generationMap) {
        int count = 0;
        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            if (x + xOffset < 0 || x + xOffset >= generationMap.length) {
                break;
            }
            for (int yOffset = -1; yOffset <= 1; yOffset++) {
                if (y + yOffset < 0 || y + yOffset >= generationMap[0].length) {
                    break;
                }
                if (xOffset == 0 && yOffset == 0) {
                    break;
                }
                count += generationMap[x + xOffset][y + yOffset];
            }
        }
        return count;
    }

    private static void initializeMap(int[][] generationMap) {
        for (int x = 0; x < generationMap.length; x++) {
            for (int y = 0; y < generationMap[0].length; y++) {
                if (Math.random() < INITIAL_FILL_CHANCE) {
                    generationMap[x][y] = 1;
                } else {
                    generationMap[x][y] = 0;
                }
            }
        }
    }

    private static void generateTileEntities(World world, String floorTextureKey, String wallTextureKey) {
        for (int x = 0; x < world.worldSizeX; x++) {
            for (int y = 0; y < world.worldSizeY; y++) {
                Entity entity = null;
                if (outOfBoundNeighbor(x, y, world)) {
                    entity = EntityBuilder.builder().fromTemplate("worldTile").at(x, y).build();
                    entity.getComponentOfType(RenderComponent.class).setTextureKey(wallTextureKey);
                    entity.getComponentOfType(RenderComponent.class).setTextureOffSetX(getTextureOffsetForOrientation(world, x, y));
                    entity.getComponentOfType(StatComponent.class).setMaxHealthPoints(Double.MAX_VALUE);
                } else if (world.getTileType(x, y).equals(WorldTileType.GROUND)) {
                    entity = EntityBuilder.builder().fromTemplate("worldGround").at(x, y).build();
                    entity.getComponentOfType(RenderComponent.class).setTextureKey(floorTextureKey);
                } else {
                    entity = EntityBuilder.builder().fromTemplate("worldTile").at(x, y).build();
                    entity.getComponentOfType(RenderComponent.class).setTextureKey(wallTextureKey);
                    double textureOffsetForOrientation = getTextureOffsetForOrientation(world, x, y);
                    entity.getComponentOfType(RenderComponent.class).setTextureOffSetX(textureOffsetForOrientation);
                    if (textureOffsetForOrientation == 11) {
                        entity.removeComponent(ViewBlockingTag.class);
                        entity.removeComponent(ShadowCastTag.class);
                        entity.removeComponent(CollisionComponent.class);
                    }
                }
                world.getTile(x, y).setEntity(entity);
                EntityHandler.getInstance().addObject(EntityKeyConstants.WORLD_TILE_PREFIX + RandomStringUtils.randomAlphanumeric(8), entity);
            }
        }
    }

    private static boolean outOfBoundNeighbor(int x, int y, World world) {
        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int yOffset = -1; yOffset <= 1; yOffset++) {
                if (world.getTileType(x + xOffset, y + yOffset).equals(WorldTileType.OUT_OF_BOUNDS)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void generateTiles(World world, int[][] generationMap) {
        for (int x = 0; x < world.worldSizeX; x++) {
            for (int y = 0; y < world.worldSizeY; y++) {
                if (x == 0 || y == 0 || x == world.worldSizeX - 1 || y == world.worldSizeY - 1) {
                    world.addTile(x, y, WorldTileType.ROCK);
                } else if (generationMap[x][y] == 1) {
                    world.addTile(x, y, WorldTileType.ROCK);
                } else {
                    world.addTile(x, y, WorldTileType.GROUND);
                }
            }
        }
    }
}
