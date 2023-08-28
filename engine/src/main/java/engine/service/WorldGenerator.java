package engine.service;

import engine.component.StatComponent;
import engine.component.base.CollisionComponent;
import engine.component.base.RenderComponent;
import engine.component.base.TransformationComponent;
import engine.component.tag.ShadowCastTag;
import engine.component.tag.ViewBlockingTag;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.WorldTileType;
import engine.handler.EntityHandler;
import engine.handler.UIHandler;
import engine.object.generation.World;
import org.apache.commons.lang3.RandomStringUtils;
import org.joml.Vector2i;

import java.util.LinkedList;
import java.util.List;

import static engine.service.util.WorldGenerationUtil.getTextureOffsetForOrientation;

public class WorldGenerator {

    private static final float INITIAL_FILL_CHANCE = 0.45f;
    private static final int GENERATION_STEPS = 3;
    private static final int UPPER_DEATH_LIMIT = 8;
    private static final int LOWER_DEATH_LIMIT = 2;
    private static final int COME_ALIVE_VALUE = 5;
    public static final String WORLD_TILE_PREFIX = "WORLD_TILE_";


    public static World generateLevel() {
        int[][] generationMap = new int[48][48];
        initializeMap(generationMap);
        for (int i = 0; i < GENERATION_STEPS; i++) {
            doGenerationStep(generationMap);
        }
        World world = new World(48, 48);
        generateTiles(world, generationMap);
        generateTileEntities(world);
        EntityHandler.getInstance().setWorld(world);
        return world;
    }

    public static World generateSafeRoom() {
        int[][] generationMap = new int[10][16];
        World world = new World(10, 16);
        generateTiles(world, generationMap);
        generateTileEntities(world);
        return world;
    }

    public static World generateBossRoom() {
        int[][] generationMap = new int[16][16];
        World world = new World(16, 16);
        generateTiles(world, generationMap);
        generateTileEntities(world);
        return world;
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

    public static void clearWorld() {
        EntityHandler.getInstance().removeAllObjectsWithoutPrefix("PLAYER", "GUN", "CAMERA");
        UIHandler.getInstance().removeTextObjectsWithPrefix("DT_");
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

    private static void generateTileEntities(World world) {
        for (int x = 0; x < world.worldSizeX; x++) {
            for (int y = 0; y < world.worldSizeY; y++) {
                Entity entity = null;
                if (outOfBoundNeighbor(x, y, world)) {
                    entity = EntityBuilder.builder().fromTemplate("worldTile").at(x, y).build();
                    entity.getComponentOfType(RenderComponent.class).setTextureKey("rock");
                    entity.getComponentOfType(RenderComponent.class).setTextureOffSetX(getTextureOffsetForOrientation(world, x, y));
                    entity.getComponentOfType(StatComponent.class).setMaxHealthPoints(Double.MAX_VALUE);
                } else if (world.getTileType(x, y).equals(WorldTileType.GROUND)) {
                    entity = EntityBuilder.builder().fromTemplate("worldGround").at(x, y).build();
                    entity.getComponentOfType(RenderComponent.class).setTextureKey("floor");
                } else {
                    entity = EntityBuilder.builder().fromTemplate("worldTile").at(x, y).build();
                    entity.getComponentOfType(RenderComponent.class).setTextureKey("rock");
                    double textureOffsetForOrientation = getTextureOffsetForOrientation(world, x, y);
                    entity.getComponentOfType(RenderComponent.class).setTextureOffSetX(textureOffsetForOrientation);
                    if (textureOffsetForOrientation == 11) {
                        entity.removeComponent(ViewBlockingTag.class);
                        entity.removeComponent(ShadowCastTag.class);
                        entity.removeComponent(CollisionComponent.class);
                    }
                }
                world.getTile(x, y).setEntity(entity);
                EntityHandler.getInstance().addObject(WORLD_TILE_PREFIX + RandomStringUtils.randomAlphanumeric(8), entity);
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
