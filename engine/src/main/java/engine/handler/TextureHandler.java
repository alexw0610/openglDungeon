package engine.handler;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;
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

    private final Map<String, Texture> loadedTextureMap = new HashMap<>();
    private final Map<Integer, String> boundTextureMap = new HashMap<>();

    private TextureHandler() {
        ByteBuffer defaultBuffer = ByteBuffer.allocate(4);
        defaultBuffer.put(new byte[]{(byte) 128, (byte) 32, (byte) 255, (byte) 255});
        defaultBuffer.flip();
        Texture defaultTexture = new Texture(1, 1, defaultBuffer);
        loadedTextureMap.put(TextureKey.DEFAULT.value(), defaultTexture);
    }

    public void bindTextureWithKey(String textureKey, int target) {
        GL4 gl = GLContext.getCurrent().getGL().getGL4();
        if (!boundTextureMap.containsKey(target) || !boundTextureMap.get(target).equals(textureKey)) {
            gl.glActiveTexture(target);
            load(textureKey);
            boundTextureMap.put(target, textureKey);
        }
    }

    private void load(String textureKey) {
        if (loadedTextureMap.containsKey(textureKey)) {
            Texture texture = loadedTextureMap.get(textureKey);
            texture.loadTexture();
            texture.bind();
        } else {
            Texture texture = TEXTURE_LOADER.loadTexture(textureKey);
            texture.loadTexture();
            texture.bind();
            loadedTextureMap.put(textureKey, texture);
        }
    }

    public Vector2i getTileMapDimensions(String textureKey) {
        if (!loadedTextureMap.containsKey(textureKey)) {
            Texture texture = TEXTURE_LOADER.loadTexture(textureKey);
            loadedTextureMap.put(textureKey, texture);
        }
        return new Vector2i(loadedTextureMap.get(textureKey).getWidth() / DEFAULT_TILE_SIZE,
                loadedTextureMap.get(textureKey).getHeight() / DEFAULT_TILE_SIZE);
    }
}
