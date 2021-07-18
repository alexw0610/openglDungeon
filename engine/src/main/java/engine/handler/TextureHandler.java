package engine.handler;

import engine.loader.TextureLoader;
import engine.object.Texture;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class TextureHandler {
    public static final TextureHandler TEXTURE_HANDLER = new TextureHandler();
    public static final String DEFAULT_TEXTURE_KEY = "DEFAULT";
    private static final TextureLoader TEXTURE_LOADER = new TextureLoader();

    private final Map<String, Texture> loadedTextureMap = new HashMap<>();

    private TextureHandler() {
        ByteBuffer defaultBuffer = ByteBuffer.allocate(4);
        defaultBuffer.put(new byte[]{(byte)128,(byte)32,(byte)255,0});
        defaultBuffer.flip();
        Texture defaultTexture = new Texture(1,1, defaultBuffer);
        defaultTexture.loadTexture();
        loadedTextureMap.put(DEFAULT_TEXTURE_KEY, defaultTexture);
    }

    public void bindTextureWithKey(String textureKey) {
        if (loadedTextureMap.containsKey(textureKey)) {
            loadedTextureMap.get(textureKey).bind();
        } else {
            Texture texture = TEXTURE_LOADER.loadTexture(textureKey);
            texture.loadTexture();
            texture.bind();
            loadedTextureMap.put(textureKey, texture);
        }
    }

}
