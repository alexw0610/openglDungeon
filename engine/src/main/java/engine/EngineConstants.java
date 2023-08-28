package engine;

import org.joml.Vector3d;

public class EngineConstants {
    public static final String TITLE = "openGLECS";
    public static final double ITEM_HOVER_SPEED = 2.5;
    public static double WINDOW_WIDTH = 1280;
    public static double WINDOW_HEIGHT = 960;
    public static final boolean FULLSCREEN = false;
    public static final int FPS = 500;
    public static final double STEP_TIME_FACTOR = 0.0000000015;
    public static final double RENDER_DISTANCE = 15;
    public static final double KNOCKBACK_VALUE_FACTOR = 66;
    public static final double DEFAULT_SPRITE_SIZE = 8;
    public static final double DEFAULT_FONT_TEXTURE_SIZE = 512;
    public static final double SECONDS_TO_NANOSECONDS_FACTOR = 1000000000;
    public static final Vector3d TEXT_COLOR_WHITE= new Vector3d(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0);
    public static final Vector3d TEXT_COLOR_YELLOW = new Vector3d(239.0 / 255.0, 232.0 / 255.0, 145.0 / 255.0);
    public static final Vector3d STAT_VALUE_COLOR = new Vector3d(239.0 / 255.0, 232.0 / 255.0, 145.0 / 255.0);
    public static final Vector3d RARITY_COLOR_COMMON = new Vector3d(145.0 / 255.0, 165.0 / 255.0, 168.0 / 255.0);
    public static final Vector3d RARITY_COLOR_RARE = new Vector3d(62.0 / 255.0, 98.0 / 255.0, 216.0 / 255.0);
    public static final Vector3d RARITY_COLOR_EPIC = new Vector3d(217.0 / 255.0, 62.0 / 255.0, 228.0 / 255.0);
    public static final Vector3d DAMAGE_TEXT_PLAYER_COLOR = new Vector3d(255.0 / 255.0, 80.0 / 255.0, 110.0 / 255.0);
    public static final Vector3d DAMAGE_TEXT_COLOR = new Vector3d(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0);
    public static final Vector3d DAMAGE_TEXT_CRIT_COLOR = new Vector3d(155.0 / 255.0, 255.0 / 255.0, 155.0 / 255.0);

    public static double getAspectRatio() {
        return WINDOW_WIDTH / WINDOW_HEIGHT;
    }
    public static void setWindowWidth(double windowWidth) {
        WINDOW_WIDTH = windowWidth;
    }
    public static void setWindowHeight(double windowHeight) {
        WINDOW_HEIGHT = windowHeight;
    }
}
