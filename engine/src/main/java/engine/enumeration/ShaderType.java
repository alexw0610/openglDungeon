package engine.enumeration;

public enum ShaderType {
    DEFAULT("shader");

    public final String shaderKey;

    ShaderType(String shaderKey) {
        this.shaderKey = shaderKey;
    }

    public String value(){
        return this.shaderKey;
    }
}
