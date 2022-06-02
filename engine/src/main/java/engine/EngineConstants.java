package engine;

public class EngineConstants {
    public static final String TITLE = "openglDungeon";
    public static final double WINDOW_WIDTH = 1280;
    public static final double WINDOW_HEIGHT = 960;
    public static final boolean FULLSCREEN = false;
    public static final int FPS = 500;
    public static final double INERTIA = 0.00001;
    public static final double DECAY = 0.75;
    public static final double MAX_ZOOM_DISTANCE = 0.5;
    public static final double MIN_ZOOM_DISTANCE = 0.01;
    public static final double LERP_SPEED = 0.00001;
    public static final double CAMERA_MOVE_SPEED = 0.00001;
    public static final double CAMERA_ZOOM_SPEED = 0.000001;
    public static final double RENDER_DISTANCE = 10;
    public static final double KNOCKBACK_VALUE_FACTOR = 100.0;
    public static final double MOVEMENT_SPEED_VALUE_FACTOR = 1000.0;

    public static final EngineConstants INSTANCE = new EngineConstants();

    private boolean offlineMode;
    private boolean serverMode;

    private EngineConstants() {
    }

    public boolean isServerMode() {
        return serverMode;
    }

    public void setServerMode(boolean serverMode) {
        this.serverMode = serverMode;
    }

    public boolean isOfflineMode() {
        return offlineMode;
    }

    public void setOfflineMode(boolean offlineMode) {
        this.offlineMode = offlineMode;
    }

    public static double getAspectRatio() {
        return WINDOW_WIDTH / WINDOW_HEIGHT;
    }
}
