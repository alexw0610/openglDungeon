package engine.loader;

import engine.enums.ShaderType;
import engine.object.Shader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.MissingResourceException;

public class ShaderLoader {
    private static final String RESOURCE_SHADER_SUBFOLDER = "shader/";
    private static final String DEFAULT_VERTEX_SHADER_FILE_EXTENSION = ".vs";
    private static final String DEFAULT_FRAGMENT_SHADER_FILE_EXTENSION = ".fs";

    public Shader loadShader(ShaderType shaderType) {
        return new Shader(shaderType.value(),
                readfile(RESOURCE_SHADER_SUBFOLDER + shaderType.value() + DEFAULT_VERTEX_SHADER_FILE_EXTENSION),
                readfile(RESOURCE_SHADER_SUBFOLDER + shaderType.value() + DEFAULT_FRAGMENT_SHADER_FILE_EXTENSION));
    }

    private String[] readfile(String file) {
        StringBuilder shadercode = new StringBuilder();
        BufferedReader br;
        InputStream is = getClass().getClassLoader().getResourceAsStream(file);
        try {
            br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                shadercode.append(line);
                shadercode.append("\n");
            }
            br.close();
        } catch (IOException e) {
            throw new MissingResourceException("The specified Shader cant be found (or opened) in the resource path." + e, this.getClass().getName(), file);
        }
        return new String[]{shadercode.toString()};
    }

}
