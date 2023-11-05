package engine.service;

import engine.EntityKeyConstants;
import engine.component.StatComponent;
import engine.component.base.TransformationComponent;
import engine.component.tag.PlayerTag;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.WorldTileType;
import engine.handler.EntityHandler;
import engine.object.generation.World;
import org.apache.commons.lang3.RandomStringUtils;
import org.joml.Vector2d;

public class MobSpawner {

    private static final int MOB_CAP = 15;
    private static final float MOB_SPAWN_CHANCE = 0.4f;
    private static final int BOSS_FIGHT_MOB_CAP = 5;
    public static boolean shouldSpawnMobs = true;
    public static boolean isBossFight = false;
    public static int difficultyLevel = 0;

    public static void setDifficultyLevel(int level) {
        difficultyLevel = level;
    }

    public static void toggleMobSpawning(boolean toggle) {
        EntityHandler.getInstance().removeObjectsWithPrefix("MOB_");
        shouldSpawnMobs = toggle;
    }

    public static void spawnBoss() {
        Entity boss = EntityBuilder
                .builder()
                .fromTemplate("mob_alien_boss")
                .at(16.5, 16.5)
                .build();
        Entity player = EntityHandler.getInstance().getEntityWithComponent(PlayerTag.class);
        int playerLevel = player.getComponentOfType(StatComponent.class).getLevel();
        adjustMobStats(playerLevel, boss);
        EntityHandler.getInstance().addObject(EntityKeyConstants.MOB_PREFIX + RandomStringUtils.randomAlphanumeric(8), boss);
    }

    public static void spawnMobs(World world) {
        removeOutOfRangeMobs();
        int mobCount = getCurrentAliveMobCount();
        Entity player = EntityHandler.getInstance().getEntityWithComponent(PlayerTag.class);
        while (mobCount < getLevelMobCap() && shouldSpawnMobs) {
            int xPosition = (int) (Math.random() * world.worldSizeX);
            int yPosition = (int) (Math.random() * world.worldSizeY);
            int playerLevel = player.getComponentOfType(StatComponent.class).getLevel();
            if (checkSpawnPointInvalidRange(player, xPosition, yPosition)) {
                break;
            }
            boolean spawnPositionFound = false;
            while (!spawnPositionFound) {
                if (validSpawnPosition(world, xPosition, yPosition)) {
                    Entity mob;
                    mob = getMobType(playerLevel);
                    mob.getComponentOfType(TransformationComponent.class).setPositionX(xPosition);
                    mob.getComponentOfType(TransformationComponent.class).setPositionY(yPosition);
                    adjustMobStats(playerLevel, mob);
                    EntityHandler.getInstance()
                            .addObject(EntityKeyConstants.MOB_PREFIX + RandomStringUtils.randomAlphanumeric(8), mob);
                    spawnPositionFound = true;
                    mobCount++;
                } else {
                    xPosition = (xPosition + ((int) (Math.random() * 3))) % world.worldSizeX;
                    yPosition = (yPosition + ((int) (Math.random() * 3))) % world.worldSizeY;
                }
            }
        }
    }

    private static void adjustMobStats(int playerLevel, Entity mob) {
        StatComponent mobStats = mob.getComponentOfType(StatComponent.class);
        mobStats.setDropsXP(!isBossFight);
        mobStats.setLevel(playerLevel);
        mobStats.setMaxHealthPoints(mobStats.getMaxHealthPoints() + ((mobStats.getMaxHealthPoints() * 0.2) * playerLevel));
        mobStats.setBaseDamagePrimary(mobStats.getBaseDamagePrimary() + ((mobStats.getBaseDamagePrimary() * 0.1) * playerLevel));
        mobStats.setAttackSpeedPrimary(mobStats.getAttackSpeedPrimary() - ((mobStats.getAttackSpeedPrimary() * 0.02) * playerLevel));
        mobStats.setBulletSpeedPrimary(mobStats.getBulletSpeedPrimary() + ((mobStats.getBulletSpeedPrimary() * 0.05) * playerLevel));
        if (playerLevel > 10 && Math.random() < 0.2) {
            mobStats.setMaxShield(mobStats.getMaxHealthPoints() * 0.1);
        }
    }

    private static Entity getMobType(int playerLevel) {
        Entity mob;
        if (Math.random() > 0.40 || playerLevel <= 5) {
            mob = EntityBuilder
                    .builder()
                    .fromTemplate("mob_alien")
                    .build();
        } else {
            mob = EntityBuilder
                    .builder()
                    .fromTemplate("mob_alien_ranged")
                    .build();
        }
        return mob;
    }

    private static boolean validSpawnPosition(World world, int xPosition, int yPosition) {
        return world.getTileType(xPosition, yPosition).equals(WorldTileType.GROUND)
                && WorldGenerator.getCaveSize(xPosition, yPosition, world) > 40
                && Math.random() < MOB_SPAWN_CHANCE;
    }

    private static boolean checkSpawnPointInvalidRange(Entity player, int xPosition, int yPosition) {
        return player.getComponentOfType(TransformationComponent.class).getPosition()
                .distance(new Vector2d(xPosition, yPosition)) < 5
                || player.getComponentOfType(TransformationComponent.class).getPosition()
                .distance(new Vector2d(xPosition, yPosition)) > 30;
    }

    private static int getCurrentAliveMobCount() {
        return (int) EntityHandler.getInstance()
                .getObjectsWithPrefix(EntityKeyConstants.MOB_PREFIX)
                .stream()
                .filter(mob -> !mob.getComponentOfType(StatComponent.class).isDead())
                .count();
    }

    private static void removeOutOfRangeMobs() {
        Vector2d playerPosition = EntityHandler.getInstance().getEntityWithComponent(PlayerTag.class).getComponentOfType(TransformationComponent.class).getPosition();
        EntityHandler.getInstance()
                .getObjectsWithPrefix(EntityKeyConstants.MOB_PREFIX)
                .stream()
                .filter(mob -> mob.getComponentOfType(TransformationComponent.class)
                        .getPosition()
                        .distance(playerPosition) > 40)
                .forEach(mob -> EntityHandler.getInstance().removeObject(mob.getEntityId()));
    }

    public static void toggleBossFight(boolean toggle) {
        isBossFight = toggle;
    }

    private static double getLevelMobCap() {
        if (isBossFight) {
            return BOSS_FIGHT_MOB_CAP;
        } else {
            return MOB_CAP + (2 * difficultyLevel);
        }
    }
}
