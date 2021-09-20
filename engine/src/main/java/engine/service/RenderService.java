package engine.service;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;
import engine.enumeration.ShaderType;
import engine.exception.MeshNotFoundException;
import engine.handler.MeshHandler;
import engine.handler.RenderHandler;
import engine.handler.ShaderHandler;
import engine.handler.TextureHandler;
import engine.object.Camera;
import engine.object.Mesh;
import engine.object.interfaces.Renderable;

import java.nio.DoubleBuffer;
import java.util.Arrays;

public class RenderService {

    private final ShaderHandler shaderHandler = ShaderHandler.SHADER_HANDLER;
    private final MeshHandler meshHandler = MeshHandler.MESH_HANDLER;
    private final RenderHandler renderHandler = RenderHandler.RENDER_HANDLER;
    private final TextureHandler textureHandler = TextureHandler.TEXTURE_HANDLER;

    private final Camera camera = Camera.CAMERA;

    private long lastExecutionTimestamp = 0;

    public void renderNextFrame() {
        clearCall();
        Renderable[] renderables = renderHandler.getRenderables();
        Arrays.sort(renderables);
        for (Renderable renderable : renderables) {
            this.render(renderable);
        }
        renderHandler.setCurrentFrameDelta(System.nanoTime() - lastExecutionTimestamp);
        lastExecutionTimestamp = System.nanoTime();
    }

    private void render(Renderable renderable) {
        if (renderable.getShaderType() != null) {
            shaderHandler.bindShaderOfType(renderable.getShaderType());
        } else {
            shaderHandler.bindShaderOfType(ShaderType.DEFAULT);
        }

        if (renderable.getTextureKey() != null) {
            textureHandler.bindTextureWithKey(renderable.getTextureKey());
        } else {
            textureHandler.bindTextureWithKey(TextureHandler.DEFAULT_TEXTURE_KEY);
        }

        try {
            Mesh mesh = meshHandler.getMeshForKey(renderable.getPrimitiveMeshShape());
            updateUboForRenderable(renderable);
            drawCall(mesh);
        } catch (MeshNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void drawCall(Mesh mesh) {
        GL4 gl = GLContext.getCurrent().getGL().getGL4();
        gl.glActiveTexture(gl.GL_TEXTURE0);

        gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_FILL);
        gl.glBindVertexArray(mesh.getVaoId());

        gl.glEnableVertexAttribArray(0);
        gl.glEnableVertexAttribArray(1);

        gl.glDrawElements(gl.GL_TRIANGLES, mesh.getIndices().length, gl.GL_UNSIGNED_INT, 0);

        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);

        gl.glBindVertexArray(0);
    }

    private static void clearCall() {
        GL4 gl = GLContext.getCurrent().getGL().getGL4();
        gl.glClear(gl.GL_COLOR_BUFFER_BIT);
    }

    private void updateUboForRenderable(Renderable renderable) {
        GL4 gl = GLContext.getCurrent().getGL().getGL4();

        int[] uniformBuffers = new int[1];
        gl.glGenBuffers(1, uniformBuffers, 0);

        DoubleBuffer buffer = DoubleBuffer.allocate(7);

        buffer.put(0, camera.getPosition().x());
        buffer.put(1, camera.getPosition().y());
        buffer.put(2, camera.getPosition().z());
        buffer.put(3, 0);
        buffer.put(4, renderable.getPosition().x());
        buffer.put(5, renderable.getPosition().y());
        buffer.put(6, renderable.getScale());

        gl.glBindBuffer(gl.GL_UNIFORM_BUFFER, uniformBuffers[0]);
        gl.glBufferData(gl.GL_UNIFORM_BUFFER, 8 * buffer.capacity(), buffer, gl.GL_DYNAMIC_DRAW);
        gl.glBindBuffer(gl.GL_UNIFORM_BUFFER, 0);
        gl.glBindBufferBase(gl.GL_UNIFORM_BUFFER, 0, uniformBuffers[0]);
    }
}
