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
import engine.handler.EntityHandler;
import engine.object.generation.World;
import org.apache.commons.lang3.RandomStringUtils;
import org.joml.Vector2i;
import org.joml.Vector3d;

import java.util.LinkedList;
import java.util.List;

import static engine.EntityKeyConstants.CLUTTER_ENTITY_PREFIX;
import static engine.service.util.WorldGenerationUtil.getTextureOffsetForOrientation;

public class WorldGenerator {

    private static final float INITIAL_FILL_CHANCE = 0.42f;
    private static final int GENERATION_STEPS = 3;
    private static final int UPPER_DEATH_LIMIT = 8;
    private static final int LOWER_DEATH_LIMIT = 2;
    private static final int COME_ALIVE_VALUE = 5;
    private static final int maxClutterNodes = 12;
    private static final int maxClutterSize = 4;
    private static final double clutterNodeFillChance = 0.5;
    private static final int maxZones = 2;
    private static final int maxZoneSize = 6;
    private static final double zoneFillChance = 0.6;

    public static World generateLevel() {
        int[][] generationMap = new int[48][48];
        initializeMap(generationMap);
        for (int i = 0; i < GENERATION_STEPS; i++) {
            doGenerationStep(generationMap);
        }
        World world = new World(48, 48);
        generateTiles(world, generationMap);
        generateTileEntities(world, "floor", "rock");
        generateEnvironmentClutter(world);
        generateZone(world);
        EntityHandler.getInstance().setWorld(world);
        return world;
    }

    private static void generateZone(World world) {
        int zones = 0;
        while (zones < maxZones) {
            int xNodePosition = (int) (Math.random() * world.worldSizeX);
            int yNodePosition = (int) (Math.random() * world.worldSizeY);
            if (world.isWalkable(xNodePosition, yNodePosition) && nextToWall(world, xNodePosition, yNodePosition)
                    && getCaveSize(xNodePosition, yNodePosition, world) > 20) {
                for (int x = xNodePosition - maxZoneSize / 2; x < xNodePosition + maxZoneSize / 2; x++) {
                    for (int y = yNodePosition - maxZoneSize / 2; y < yNodePosition + maxZoneSize / 2; y++) {
                        if (Math.random() < zoneFillChance) {
                            if (world.getTileType(x, y).equals(WorldTileType.GROUND)) {
                                world.getTile(x, y).getEntity().getComponentOfType(RenderComponent.class).setTextureKey("floor_alien");
                            }
                        }
                        if (world.getTileType(x, y).equals(WorldTileType.ROCK)) {
                            world.getTile(x, y).getEntity().getComponentOfType(RenderComponent.class).setTextureKey("rock_alien");
                        }
                        if (Math.random() < 0.1 && world.isWalkable(x, y)) {
                            EntityBuilder.builder().fromTemplate("alien_egg").at(x, y)
                                    .buildAndInstantiate(CLUTTER_ENTITY_PREFIX + RandomStringUtils.randomAlphanumeric(8));
                        }
                    }
                }
                zones++;
            }
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
        generateEnvironmentClutter(world);
        return world;
    }

    private static void generateEnvironmentClutter(World world) {
        int clutterNodes = 0;
        while (clutterNodes < maxClutterNodes) {
            int xNodePosition = (int) (Math.random() * world.worldSizeX);
            int yNodePosition = (int) (Math.random() * world.worldSizeY);
            if (world.isWalkable(xNodePosition, yNodePosition)
                    && getCaveSize(xNodePosition, yNodePosition, world) > 20) {
                generateClutterNode(world, xNodePosition, yNodePosition);
                clutterNodes++;
            }
        }

    }

    private static void generateClutterNode(World world, int xNodePosition, int yNodePosition) {
        String clutterTexture = pickRandomClutterType();
        if (clutterTexture.equals("fire_grass")) {
            Entity lightSource = EntityBuilder.builder().fromTemplate("light").at(xNodePosition, yNodePosition).build();
            lightSource.getComponentOfType(LightSourceComponent.class)
                    .setLightColor(new Vector3d(159.0 / 255.0, 97.0 / 255.0, 82.0 / 255.0));
            EntityHandler.getInstance().addObject(CLUTTER_ENTITY_PREFIX + RandomStringUtils.randomAlphanumeric(8), lightSource);
        }
        if (clutterTexture.equals("cave_plant")) {
            Entity lightSource = EntityBuilder.builder().fromTemplate("light").at(xNodePosition, yNodePosition).build();
            lightSource.getComponentOfType(LightSourceComponent.class)
                    .setLightColor(new Vector3d(58.0 / 255.0, 123.0 / 255.0, 118.0 / 255.0));
            EntityHandler.getInstance().addObject(CLUTTER_ENTITY_PREFIX + RandomStringUtils.randomAlphanumeric(8), lightSource);
        }
        for (int x = xNodePosition - maxClutterSize / 2; x < xNodePosition + maxClutterSize / 2; x++) {
            for (int y = yNodePosition - maxClutterSize / 2; y < yNodePosition + maxClutterSize / 2; y++) {
                if (Math.random() < clutterNodeFillChance && world.isWalkable(x, y)) {
                    Entity clutter = EntityBuilder.builder().fromTemplate("clutter")
                            .at(x + ((Math.random() - 0.5) * 0.5), y + ((Math.random() - 0.5) * 0.5))
                            .build();
                    clutter.getComponentOfType(RenderComponent.class).setTextureKey(clutterTexture);
                    EntityHandler.getInstance()
                            .addObject(CLUTTER_ENTITY_PREFIX + RandomStringUtils.randomAlphanumeric(8), clutter);
                }
            }
        }
    }

    private static String pickRandomClutterType() {
        double random = Math.random();
        return random < 0.33 ? "rubble" : random < 0.66 ? "cave_plant" : "fire_grass";
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
