package engine.scene;

import engine.enumeration.PrimitiveMeshShape;
import engine.enumeration.ShaderType;
import engine.object.GameObject;
import engine.object.Hitbox;
import engine.object.enums.HitboxType;

public class SceneGenerator {

    public static final int MAP_SIZE = 32;

    public static SceneTileMap generateScene() {
        SceneTileMap sceneTileMap = new SceneTileMap((short) MAP_SIZE);

        for (short x = 0; x < MAP_SIZE; x++) {
            for (short y = 0; y < MAP_SIZE; y++) {
                double rnd = Math.random();
                if (rnd > 0.1f) {
                    GameObject floor = new GameObject(PrimitiveMeshShape.QUAD, ShaderType.DEFAULT, new Hitbox(HitboxType.AABB, 0.5), x, y);
                    floor.setTextureKey("stone_rough_purple_dark_no_highlights");
                    floor.setRenderLayer((short) 0);
                    floor.setSurface(true);
                    sceneTileMap.setTile(new Tile(floor, x, y), x, y);
                }
            }
        }
        return sceneTileMap;
    }
}
