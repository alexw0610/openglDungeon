package engine.object;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLContext;

import java.nio.ByteBuffer;

public class Texture {

    private final int width;
    private final int height;
    private final ByteBuffer textureBuffer;
    private int textureId;
    private boolean isLoaded;

    public Texture(int width, int height, ByteBuffer textureBuffer) {
        this.width = width;
        this.height = height;
        this.textureBuffer = textureBuffer;
        this.isLoaded = false;

    }

    public void bind() {
        GL3 gl = GLContext.getCurrentGL().getGL3();
        gl.glBindTexture(gl.GL_TEXTURE_2D, textureId);
    }

    public void loadTexture() {
        if (!isLoaded) {
            GL3 gl = GLContext.getCurrentGL().getGL3();

            int[] texids = new int[1];
            gl.glGenTextures(1, texids, 0);
            textureId = texids[0];
            gl.glDeleteTextures(1, new int[]{textureId}, 0);

            gl.glBindTexture(gl.GL_TEXTURE_2D, textureId);
            gl.glPixelStorei(gl.GL_UNPACK_ALIGNMENT, 1);
            gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MAG_FILTER, gl.GL_NEAREST);
            gl.glTexImage2D(gl.GL_TEXTURE_2D, 0, gl.GL_RGBA, this.width, this.height, 0, gl.GL_RGBA, gl.GL_UNSIGNED_BYTE, this.textureBuffer);
            gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MIN_FILTER, gl.GL_NEAREST);
            gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_WRAP_S, gl.GL_REPEAT);
            gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_WRAP_T, gl.GL_REPEAT);
            isLoaded = true;
        }
    }

    public void unloadTexture() {
        GL3 gl = GLContext.getCurrentGL().getGL3();
        gl.glDeleteTextures(1, new int[]{textureId}, 0);
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ByteBuffer getImageBuffer() {
        return textureBuffer;
    }
}
