package engine.handler;

import engine.loader.ShaderLoader;
import engine.object.Shader;

import java.util.HashMap;
import java.util.Map;

public class ShaderHandler {
    public static final ShaderHandler SHADER_HANDLER = new ShaderHandler();
    private static final ShaderLoader SHADER_LOADER = new ShaderLoader();

    private final Map<String, Shader> shaderMap = new HashMap<>();
    private String boundShaderType;

    private ShaderHandler() {
    }

    public void bindShaderOfType(String shaderType) {
        if (boundShaderType == null || !boundShaderType.equals(shaderType)) {
            if (shaderMap.containsKey(shaderType)) {
                shaderMap.get(shaderType).bind();
            } else {
                Shader shader = SHADER_LOADER.loadShader(shaderType);
                shader.bind();
                shaderMap.put(shaderType, shader);
            }
            boundShaderType = shaderType;
        }
    }
}
