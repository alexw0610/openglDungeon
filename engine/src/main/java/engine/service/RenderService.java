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
import org.joml.Vector2d;

import java.nio.DoubleBuffer;
import java.util.Arrays;

import static engine.EngineConstants.WINDOW_HEIGHT;
import static engine.EngineConstants.WINDOW_WIDTH;

public class RenderService {

    private final ShaderHandler shaderHandler = ShaderHandler.SHADER_HANDLER;
    private final MeshHandler meshHandler = MeshHandler.MESH_HANDLER;
    private final RenderHandler renderHandler = RenderHandler.RENDER_HANDLER;
    private final TextureHandler textureHandler = TextureHandler.TEXTURE_HANDLER;
    private final Camera camera = Camera.CAMERA;

    private final int[] uniformBuffers;
    private final DoubleBuffer uboDataBuffer;

    private long lastExecutionTimestamp = 0;

    Vector2d aspectRatio;

    public RenderService() {
        this.uniformBuffers = new int[1];
        this.uboDataBuffer = DoubleBuffer.allocate(10);
        GL4 gl = GLContext.getCurrent().getGL().getGL4();
        gl.glGenBuffers(1, uniformBuffers, 0);
        this.aspectRatio = new Vector2d(0, WINDOW_WIDTH / WINDOW_HEIGHT);
    }

    public void renderNextFrame() {
        clearCall();
        Renderable[] renderables = renderHandler.getRenderables();
        Arrays.sort(renderables);
        for (Renderable renderable : renderables) {
            this.render(renderable);
        }
        renderHandler.setCurrentFrameDeltaMs((System.nanoTime() - lastExecutionTimestamp) / 1000);
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

        this.uboDataBuffer.clear();
        this.uboDataBuffer.put(0, camera.getPosition().x());
        this.uboDataBuffer.put(1, camera.getPosition().y());
        this.uboDataBuffer.put(2, camera.getPosition().z());
        this.uboDataBuffer.put(3, 0);
        this.uboDataBuffer.put(4, renderable.getPosition().x());
        this.uboDataBuffer.put(5, renderable.getPosition().y());
        this.uboDataBuffer.put(6, renderable.getScale());
        this.uboDataBuffer.put(7, 0);
        this.uboDataBuffer.put(8, this.aspectRatio.x());
        this.uboDataBuffer.put(9, this.aspectRatio.y());

        gl.glBindBuffer(gl.GL_UNIFORM_BUFFER, this.uniformBuffers[0]);
        gl.glBufferData(gl.GL_UNIFORM_BUFFER, 8 * this.uboDataBuffer.capacity(), uboDataBuffer, gl.GL_DYNAMIC_DRAW);
        gl.glBindBuffer(gl.GL_UNIFORM_BUFFER, 0);
        gl.glBindBufferBase(gl.GL_UNIFORM_BUFFER, 0, this.uniformBuffers[0]);
    }
}
