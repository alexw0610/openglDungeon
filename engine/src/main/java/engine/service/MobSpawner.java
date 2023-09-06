package engine.service;

import engine.component.tag.PlayerTag;
import engine.component.StatComponent;
import engine.component.base.TransformationComponent;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.WorldTileType;
import engine.handler.EntityHandler;
import engine.object.generation.World;
import org.apache.commons.lang3.RandomStringUtils;
import org.joml.Vector2d;

public class MobSpawner {

    private static final int MOB_CAP = 10;
    private static final float MOB_SPAWN_CHANCE = 0.3f;
    public static final String MOB_PREFIX = "MOB_";
    public static boolean shouldSpawnMobs = true;
    public static int difficultyLevel = 0;

    public static void clearMobs() {
        EntityHandler.getInstance().removeObjectsWithPrefix(MOB_PREFIX);
    }

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
                .fromTemplate("boss_alien")
                .at(8, 8)
                .build();
        EntityHandler.getInstance().addObject("MOB_" + RandomStringUtils.randomAlphanumeric(8), boss);
    }

    public static void spawnBossAdd(World world, int maxAdds, int playerLevel) {
        int mobCount = (int) EntityHandler.getInstance()
                .getObjectsWithPrefix("MOB_")
                .stream()
                .filter(mob -> !mob.getComponentOfType(StatComponent.class).isDead())
                .count();
        if (mobCount >= maxAdds) {
            return;
        }
        Entity mob;
        if (Math.random() > 0.40) {
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
        StatComponent mobStats = mob.getComponentOfType(StatComponent.class);
        mobStats.setDropsXP(false);
        mobStats.setLevel(playerLevel);
        mobStats.setMaxHealthPoints(mobStats.getMaxHealthPoints() + ((mobStats.getMaxHealthPoints() * 0.5) * playerLevel));
        mobStats.setAttackSpeedPrimary(mobStats.getAttackSpeedPrimary() - ((mobStats.getAttackSpeedPrimary() * 0.05) * playerLevel));
        int corner = (int) (Math.random() * 4.0);
        switch (corner) {
            case 0:
                mob.getComponentOfType(TransformationComponent.class).setPositionX(2.0);
                mob.getComponentOfType(TransformationComponent.class).setPositionY(2.0);
                break;
            case 1:
                mob.getComponentOfType(TransformationComponent.class).setPositionX(14.0);
                mob.getComponentOfType(TransformationComponent.class).setPositionY(2.0);
                break;
            case 2:
                mob.getComponentOfType(TransformationComponent.class).setPositionX(14.0);
                mob.getComponentOfType(TransformationComponent.class).setPositionY(14.0);
                break;
            case 3:
                mob.getComponentOfType(TransformationComponent.class).setPositionX(2.0);
                mob.getComponentOfType(TransformationComponent.class).setPositionY(14.0);
                break;
        }
        EntityHandler.getInstance().addObject("MOB_" + RandomStringUtils.randomAlphanumeric(8), mob);
    }

    public static void spawnMobs(World world) {
        int mobCount = (int) EntityHandler.getInstance()
                .getObjectsWithPrefix("MOB_")
                .stream()
                .filter(mob -> !mob.getComponentOfType(StatComponent.class).isDead())
                .count();
        Entity player = EntityHandler.getInstance().getEntityWithComponent(PlayerTag.class);
        while (mobCount < getLevelMobCap() && shouldSpawnMobs) {
            int xPosition = (int) (Math.random() * world.worldSizeX);
            int yPosition = (int) (Math.random() * world.worldSizeY);
            int playerLevel = 1;
            if (player != null && player.hasComponentOfType(TransformationComponent.class)) {
                playerLevel = player.getComponentOfType(StatComponent.class).getLevel();
                if (player.getComponentOfType(TransformationComponent.class).getPosition()
                        .distance(new Vector2d(xPosition, yPosition)) < 10
                        && player.getComponentOfType(TransformationComponent.class)
                        .getPosition()
                        .distance(new Vector2d(xPosition, yPosition))
                        > 25) {
                    break;
                }
            }
            boolean spawnPositionFound = false;
            while (!spawnPositionFound) {
                if (world.getTileType(xPosition, yPosition).equals(WorldTileType.GROUND)
                        && WorldGenerator.getCaveSize(xPosition, yPosition, world) > 16
                        && Math.random() < MOB_SPAWN_CHANCE) {
                    Entity mob;
                    if (Math.random() > 0.40) {
                        mob = EntityBuilder
                                .builder()
                                .fromTemplate("mob_alien")
                                .at(xPosition, yPosition)
                                .build();
                    } else {
                        mob = EntityBuilder
                                .builder()
                                .fromTemplate("mob_alien_ranged")
                                .at(xPosition, yPosition)
                                .build();
                    }
                    StatComponent mobStats = mob.getComponentOfType(StatComponent.class);
                    mobStats.setLevel(playerLevel);
                    mobStats.setMaxHealthPoints(mobStats.getMaxHealthPoints() + ((mobStats.getMaxHealthPoints() * 0.5) * playerLevel));
                    mobStats.setAttackSpeedPrimary(mobStats.getAttackSpeedPrimary() - ((mobStats.getAttackSpeedPrimary() * 0.05) * playerLevel));
                    EntityHandler.getInstance().addObject("MOB_" + RandomStringUtils.randomAlphanumeric(8), mob);
                    spawnPositionFound = true;
                    mobCount++;
                } else {
                    xPosition = (xPosition + ((int) (Math.random() * 3))) % world.worldSizeX;
                    yPosition = (yPosition + ((int) (Math.random() * 3))) % world.worldSizeY;
                }
            }
        }
    }

    private static double getLevelMobCap() {
        return MOB_CAP + (Math.round(Math.max(Math.log(difficultyLevel) * 3, 0)));
    }
}
