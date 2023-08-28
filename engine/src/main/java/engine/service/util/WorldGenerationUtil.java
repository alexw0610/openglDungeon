package engine.service.util;

import engine.enums.WorldTileType;
import engine.object.generation.World;

public class WorldGenerationUtil {

    public static double getTextureOffsetForOrientation(World world, int x, int y) {
        boolean top = world.getTileType(x, y + 1).equals(WorldTileType.GROUND);
        boolean left = world.getTileType(x - 1, y).equals(WorldTileType.GROUND);
        boolean right = world.getTileType(x + 1, y).equals(WorldTileType.GROUND);
        boolean bottom = world.getTileType(x, y - 1).equals(WorldTileType.GROUND);

        if (top && !left && !right && bottom) {
            return 0.0f; //pillar horizontal
        }
        if (!top && left && right && !bottom) {
            return 1.0f; //pillar vertical
        }
        if (top && left && right && bottom) {
            return 2.0f; //free
        }
        if (!top && left && !right && bottom) {
            return 3.0f;
        }
        if (!top && !left && right && bottom) {
            return 4.0f;
        }
        if (top && left && !right && !bottom) {
            return 5.0f;
        }
        if (top && !left && right && !bottom) {
            return 6.0f;
        }
        if (!top && !left && right && !bottom) {
            return 7.0f;
        }
        if (!top && left && !right && !bottom) {
            return 8.0f;
        }
        if (!top && !left && !right && bottom) {
            return 9.0f;
        }
        if (top && !left && !right && !bottom) {
            return 10.0f;
        }
        if (!top && !left && !right && !bottom) {
            return 11.0f;
        }
        if (!top && left && right && bottom) {
            return 12.0f;
        }
        if (top && !left && right && bottom) {
            return 13.0f;
        }
        if (top && left && !right && bottom) {
            return 14.0f;
        }
        if (top && left && right && !bottom) {
            return 15.0f;
        }

        return 11.0;
    }
}
