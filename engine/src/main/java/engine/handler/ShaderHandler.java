package engine.handler;

import engine.loader.ShaderLoader;
import engine.object.Shader;

import java.util.HashMap;
import java.util.Map;

public class ShaderHandler {
    private static final ThreadLocal<ShaderHandler> INSTANCE = ThreadLocal.withInitial(ShaderHandler::new);
    private final Map<String, Shader> shaderMap = new HashMap<>();
    private String boundShaderType;

    public static ShaderHandler getInstance() {
        return INSTANCE.get();
    }

    private ShaderHandler() {
    }

    public void bindShaderOfType(String shaderType) {
        if (boundShaderType == null || !boundShaderType.equals(shaderType)) {
            if (shaderMap.containsKey(shaderType)) {
                shaderMap.get(shaderType).bind();
            } else {
                Shader shader = ShaderLoader.loadShader(shaderType);
                shader.bind();
                shaderMap.put(shaderType, shader);
            }
            boundShaderType = shaderType;
        }
    }
}
