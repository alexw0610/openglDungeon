package engine.handler;

import engine.enums.TextureKey;
import engine.loader.TextureLoader;
import engine.object.Texture;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class TextureHandler {
    public static final TextureHandler TEXTURE_HANDLER = new TextureHandler();
    private static final TextureLoader TEXTURE_LOADER = new TextureLoader();

    private final Map<TextureKey, Texture> loadedTextureMap = new HashMap<>();

    private TextureHandler() {
        ByteBuffer defaultBuffer = ByteBuffer.allocate(4);
        defaultBuffer.put(new byte[]{(byte)128,(byte)32,(byte)255,0});
        defaultBuffer.flip();
        Texture defaultTexture = new Texture(1,1, defaultBuffer);
        defaultTexture.loadTexture();
        loadedTextureMap.put(TextureKey.DEFAULT, defaultTexture);
    }

    public void bindTextureWithKey(TextureKey textureKey) {
        if (loadedTextureMap.containsKey(textureKey)) {
            loadedTextureMap.get(textureKey).bind();
        } else {
            Texture texture = TEXTURE_LOADER.loadTexture(textureKey.value());
            texture.loadTexture();
            texture.bind();
            loadedTextureMap.put(textureKey, texture);
        }
    }

}
