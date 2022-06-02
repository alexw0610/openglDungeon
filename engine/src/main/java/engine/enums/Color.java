package engine.enums;

import java.util.Arrays;

public enum Color {
    GREEN("green"),
    BLUE("blue"),
    RED("red"),
    YELLOW("yellow"),
    PURPLE("purple"),
    BLACK("black"),
    WHITE("white");

    private final String colorKey;
    private float red;
    private float green;
    private float blue;

    static {
        WHITE.red = 1;
        WHITE.green = 1;
        WHITE.blue = 1;

        GREEN.red = 0;
        GREEN.green = 1;
        GREEN.blue = 0;

        BLUE.red = 44 / 255.0f;
        BLUE.green = 57 / 255.0f;
        BLUE.blue = 240 / 255.0f;

        RED.red = 234.0f / 255.0f;
        RED.green = 30 / 255.0f;
        RED.blue = 64 / 255.0f;

        YELLOW.red = 242 / 255.0f;
        YELLOW.green = 221 / 255.0f;
        YELLOW.blue = 39 / 255.0f;

        PURPLE.red = 29 / 255.0f;
        PURPLE.green = 30 / 255.0f;
        PURPLE.blue = 46 / 255.0f;

        BLACK.red = 0 / 255.0f;
        BLACK.green = 0 / 255.0f;
        BLACK.blue = 0 / 255.0f;
    }

    Color(String colorKey) {
        this.colorKey = colorKey;
    }

    public static Color getColorForKey(String key) {
        return Arrays.stream(Color.values()).filter(color -> color.colorKey.equals(key)).findFirst().get();
    }

    public String value() {
        return this.colorKey;
    }

    public float getRed() {
        return this.red;
    }

    public float getGreen() {
        return this.green;
    }

    public float getBlue() {
        return this.blue;
    }
}
