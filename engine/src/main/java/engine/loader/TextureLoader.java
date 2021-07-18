package engine.loader;

import engine.object.Texture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.MissingResourceException;

public class TextureLoader {

    private static final String RESOURCE_TEXTURE_SUBFOLDER = "texture/";
    private static final String DEFAULT_TEXTURE_FILE_EXTENSION = ".png";

    public Texture loadTexture(String textureName) {
        BufferedImage textureImage;
        try {
            InputStream resourceAsStream = TextureLoader.class.getClassLoader().getResourceAsStream(RESOURCE_TEXTURE_SUBFOLDER + textureName + DEFAULT_TEXTURE_FILE_EXTENSION);
            textureImage = ImageIO.read(resourceAsStream);
        } catch (IOException e) {
            throw new MissingResourceException("The specified Texture cant be found (or opened) in the resource path." + e, this.getClass().getName(), textureName);
        }

        ByteBuffer textureBuffer = ByteBuffer.allocate(textureImage.getWidth() * textureImage.getHeight() * 4);
        int[] temp = new int[textureImage.getWidth() * textureImage.getHeight()];
        textureImage.getRGB(0, 0, textureImage.getWidth(), textureImage.getHeight(), temp, 0, textureImage.getWidth());
        for (int y = 0; y < textureImage.getHeight(); ++y) {
            for (int x = 0; x < textureImage.getWidth(); ++x) {
                int pixel = temp[x + y * textureImage.getWidth()];
                textureBuffer.put((byte) ((pixel >> 16) & 0xFF));
                textureBuffer.put((byte) ((pixel >> 8) & 0xFF));
                textureBuffer.put((byte) (pixel & 0xFF));
                textureBuffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }
        textureBuffer.flip();
        return new Texture(textureImage.getWidth(), textureImage.getHeight(), textureBuffer);

    }


}
