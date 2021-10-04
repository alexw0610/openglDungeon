package engine.handler;

import engine.enums.TextureKey;
import engine.loader.TextureLoader;
import engine.object.Texture;
import org.joml.Vector2i;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class TextureHandler {
    public static final TextureHandler TEXTURE_HANDLER = new TextureHandler();
    private static final TextureLoader TEXTURE_LOADER = new TextureLoader();
    public static final int DEFAULT_TILE_SIZE = 32;

    private final Map<TextureKey, Texture> loadedTextureMap = new HashMap<>();

    private TextureHandler() {
        ByteBuffer defaultBuffer = ByteBuffer.allocate(4);
        defaultBuffer.put(new byte[]{(byte) 128, (byte) 32, (byte) 255, (byte) 255});
        defaultBuffer.flip();
        Texture defaultTexture = new Texture(1, 1, defaultBuffer);
        loadedTextureMap.put(TextureKey.DEFAULT, defaultTexture);
    }

    public void bindTextureWithKey(TextureKey textureKey) {
        if (loadedTextureMap.containsKey(textureKey)) {
            Texture texture = loadedTextureMap.get(textureKey);
            texture.loadTexture();
            texture.bind();
        } else {
            Texture texture = TEXTURE_LOADER.loadTexture(textureKey.value());
            texture.loadTexture();
            texture.bind();
            loadedTextureMap.put(textureKey, texture);
        }
    }

    public Vector2i getTileMapDimensions(TextureKey textureKey) {
        if (!loadedTextureMap.containsKey(textureKey)) {
            Texture texture = TEXTURE_LOADER.loadTexture(textureKey.value());
            loadedTextureMap.put(textureKey, texture);
        }
        return new Vector2i(loadedTextureMap.get(textureKey).getWidth() / DEFAULT_TILE_SIZE,
                loadedTextureMap.get(textureKey).getHeight() / DEFAULT_TILE_SIZE);
    }
}
