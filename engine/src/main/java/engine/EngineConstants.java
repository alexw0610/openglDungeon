package engine;

public class EngineConstants {
    public static final String TITLE = "openGLECS";
    public static double WINDOW_WIDTH = 1280;
    public static double WINDOW_HEIGHT = 960;
    public static final boolean FULLSCREEN = false;
    public static final int FPS = 500;
    public static final double INERTIA = 0.00001;
    public static final double DECAY = 0.75;
    public static final double LERP_SPEED = 0.00001;
    public static final double RENDER_DISTANCE = 10;

    public static final EngineConstants INSTANCE = new EngineConstants();

    private EngineConstants() {
    }

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
