package engine.enums;

public enum ShaderType {
    DEFAULT("shader"),
    VIEW_POLYGON_SHADER("viewPolygonShader"),
    LIGHT_POLYGON_SHADER("lightPolygonShader");

    public final String shaderKey;

    ShaderType(String shaderKey) {
        this.shaderKey = shaderKey;
    }

    public String value() {
        return this.shaderKey;
    }
}
