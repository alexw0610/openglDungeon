package engine.handler;

import engine.enums.ShaderType;
import engine.loader.ShaderLoader;
import engine.object.Shader;

import java.util.HashMap;
import java.util.Map;

public class ShaderHandler {
    public static final ShaderHandler SHADER_HANDLER = new ShaderHandler();
    private static final ShaderLoader SHADER_LOADER = new ShaderLoader();

    private final Map<ShaderType, Shader> shaderMap = new HashMap<>();

    private ShaderHandler() {
    }

    public void bindShaderOfType(ShaderType shaderType) {
        if (shaderMap.containsKey(shaderType)) {
            shaderMap.get(shaderType).bind();
        } else {
            Shader shader = SHADER_LOADER.loadShader(shaderType);
            shader.bind();
            shaderMap.put(shaderType, shader);
        }
    }

}
