package engine.service;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;
import engine.enumeration.ShaderType;
import engine.exception.MeshNotFoundException;
import engine.handler.*;
import engine.object.Camera;
import engine.object.GameObject;
import engine.object.Mesh;
import engine.object.interfaces.Renderable;
import org.joml.Vector2d;

import java.nio.DoubleBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static engine.EngineConstants.WINDOW_HEIGHT;
import static engine.EngineConstants.WINDOW_WIDTH;

public class RenderService {

    private final ShaderHandler shaderHandler = ShaderHandler.SHADER_HANDLER;
    private final MeshHandler meshHandler = MeshHandler.MESH_HANDLER;
    private final RenderHandler renderHandler = RenderHandler.RENDER_HANDLER;
    private final TextureHandler textureHandler = TextureHandler.TEXTURE_HANDLER;
    private final Camera camera = Camera.CAMERA;

    private final int[] uniformBuffers;
    private final int[] frameBuffers;
    private final int[] renderedTextures;

    private final DoubleBuffer uboDataBuffer;

    private long lastExecutionTimestamp = 0;
    private double renderTick = 0;

    Vector2d aspectRatio;

    public RenderService() {
        GL4 gl = GLContext.getCurrent().getGL().getGL4();
        this.uniformBuffers = new int[1];
        this.uboDataBuffer = DoubleBuffer.allocate(16);
        gl.glGenBuffers(1, uniformBuffers, 0);
        this.frameBuffers = new int[2];
        this.renderedTextures = new int[2];
        gl.glGenTextures(2, this.renderedTextures, 0);
        gl.glGenFramebuffers(2, frameBuffers, 0);
        this.aspectRatio = new Vector2d(0, WINDOW_WIDTH / WINDOW_HEIGHT);
        linkTextureToFbo(this.frameBuffers[0], this.renderedTextures[0]);
        linkTextureToFbo(this.frameBuffers[1], this.renderedTextures[1]);
        gl.glEnable(gl.GL_BLEND);
        gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
        gl.glClearColor(0, 0, 0, 1);
    }

    public void renderNextFrame() {
        clearCall(0);
        List<GameObject> obstacles = SceneHandler.getInstance().getObjects().stream().filter(GameObject::isObstacle).collect(Collectors.toList());
        List<GameObject> lightSources = SceneHandler.getInstance().getObjects().stream().filter(GameObject::isLightSource).collect(Collectors.toList());
        Mesh playerLineOfSightPolygon = VisibilityPolygonFactory.generateVisibilityPolygon(
                obstacles,
                getPlayerPositionOrDefault(new Vector2d(0, 0)),
                20);
        renderLineOfSightPolygonToTexture(frameBuffers[0], playerLineOfSightPolygon, ShaderType.VIEW_POLYGON_SHADER);
        renderLineOfSightPolygonToTexture(frameBuffers[1], getLineOfSightPolygons(obstacles, lightSources));
        renderRenderables(renderHandler.getRenderables());
        renderDebugMeshes(renderHandler.getEphemeralDebugMeshes());
        renderDebugMeshes(renderHandler.getDebugMeshes());
        long renderDelta = (System.nanoTime() - lastExecutionTimestamp) / 1000;
        renderHandler.setCurrentFrameDeltaMs(renderDelta);
        this.renderTick += 1000.0 / renderDelta;
        lastExecutionTimestamp = System.nanoTime();
        renderHandler.clearEphemeralDebugMeshes();
    }

    private Map<Vector2d, Mesh> getLineOfSightPolygons(List<GameObject> obstacles, List<GameObject> lightSources) {
        Map<Vector2d, Mesh> lineOfSightPolygons = new HashMap<>();
        for (GameObject lightSource : lightSources) {
            lineOfSightPolygons.put(lightSource.getPosition(), VisibilityPolygonFactory.generateVisibilityPolygon(
                    obstacles,
                    lightSource.getPosition(),
                    20));
        }
        return lineOfSightPolygons;
    }

    private void renderLineOfSightPolygonToTexture(int frameBuffer, Map<Vector2d, Mesh> meshes) {
        clearCall(frameBuffer);
        for (Vector2d key : meshes.keySet()) {
            updateUbo(key.x(), key.y(), 1, 0);
            renderLineOfSightPolygonToTexture(frameBuffer, meshes.get(key), ShaderType.LIGHT_POLYGON_SHADER);
        }
    }

    private void renderLineOfSightPolygonToTexture(int frameBuffer, Mesh mesh, ShaderType shaderType) {
        GL4 gl = GLContext.getCurrent().getGL().getGL4();
        gl.glBindFramebuffer(gl.GL_FRAMEBUFFER, frameBuffer);
        shaderHandler.bindShaderOfType(shaderType);
        textureHandler.bindTextureWithKey(TextureHandler.DEFAULT_TEXTURE_KEY);
        gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_FILL);
        mesh.loadMesh();
        drawCall(mesh, gl.GL_TRIANGLES);
        mesh.unload();
        gl.glBindFramebuffer(gl.GL_FRAMEBUFFER, 0);
    }

    private void renderDebugMeshes(Mesh[] meshes) {
        GL4 gl = GLContext.getCurrent().getGL().getGL4();
        updateUbo(0, 0, 1, 0);
        shaderHandler.bindShaderOfType(ShaderType.DEFAULT);
        textureHandler.bindTextureWithKey(TextureHandler.DEFAULT_TEXTURE_KEY);
        gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_LINE);
        for (Mesh mesh : meshes) {
            mesh.loadMesh();
            drawCall(mesh, gl.GL_LINES);
        }
    }

    private void renderRenderables(Renderable[] renderables) {
        GL4 gl = GLContext.getCurrent().getGL().getGL4();
        Arrays.sort(renderables);
        gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_FILL);
        for (Renderable renderable : renderables) {
            this.render(renderable);
        }
    }

    private void render(Renderable renderable) {
        GL4 gl = GLContext.getCurrent().getGL().getGL4();
        if (renderable.getShaderType() != null) {
            shaderHandler.bindShaderOfType(renderable.getShaderType());
        } else {
            shaderHandler.bindShaderOfType(ShaderType.DEFAULT);
        }
        gl.glActiveTexture(gl.GL_TEXTURE0);
        if (renderable.getTextureKey() != null) {
            textureHandler.bindTextureWithKey(renderable.getTextureKey());
        } else {
            textureHandler.bindTextureWithKey(TextureHandler.DEFAULT_TEXTURE_KEY);
        }
        gl.glActiveTexture(gl.GL_TEXTURE1);
        gl.glBindTexture(gl.GL_TEXTURE_2D, this.renderedTextures[0]);

        gl.glActiveTexture(gl.GL_TEXTURE2);
        gl.glBindTexture(gl.GL_TEXTURE_2D, this.renderedTextures[1]);

        try {
            Mesh mesh = meshHandler.getMeshForKey(renderable.getPrimitiveMeshShape());
            updateUboForRenderable(renderable);
            drawCall(mesh, gl.GL_TRIANGLES);
        } catch (MeshNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void drawCall(Mesh mesh, int GL_RENDER_MODE) {
        GL4 gl = GLContext.getCurrent().getGL().getGL4();
        gl.glBindVertexArray(mesh.getVaoId());
        gl.glEnableVertexAttribArray(0);
        gl.glEnableVertexAttribArray(1);
        gl.glDrawElements(GL_RENDER_MODE, mesh.getIndices().length, gl.GL_UNSIGNED_INT, 0);
        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);
        gl.glBindVertexArray(0);
    }

    private static void clearCall(int framebuffer) {
        GL4 gl = GLContext.getCurrent().getGL().getGL4();
        gl.glBindFramebuffer(gl.GL_FRAMEBUFFER, framebuffer);
        gl.glClear(gl.GL_COLOR_BUFFER_BIT);
        gl.glBindFramebuffer(gl.GL_FRAMEBUFFER, 0);
    }

    private void linkTextureToFbo(int frameBuffer, int renderedTexture) {
        GL4 gl = GLContext.getCurrent().getGL().getGL4();
        gl.glBindFramebuffer(gl.GL_FRAMEBUFFER, frameBuffer);
        // "Bind" the newly created texture : all future texture functions will modify this texture
        gl.glBindTexture(gl.GL_TEXTURE_2D, renderedTexture);
        // Give an empty image to OpenGL ( the last "0" )
        gl.glTexImage2D(gl.GL_TEXTURE_2D, 0, gl.GL_RGBA, (int) WINDOW_WIDTH, (int) WINDOW_HEIGHT, 0, gl.GL_RGBA, gl.GL_UNSIGNED_BYTE, null);
        // Poor filtering. Needed !
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MAG_FILTER, gl.GL_NEAREST);
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MIN_FILTER, gl.GL_NEAREST);
        // Set "renderedTexture" as our colour attachement #0
        gl.glFramebufferTexture(gl.GL_FRAMEBUFFER, gl.GL_COLOR_ATTACHMENT0, renderedTexture, 0);
        // Set the list of draw buffers.
        int[] drawBuffers = new int[]{gl.GL_COLOR_ATTACHMENT0};

        gl.glDrawBuffers(1, drawBuffers, 0); // "1" is the size of DrawBuffers
        if (gl.glCheckFramebufferStatus(gl.GL_FRAMEBUFFER) != gl.GL_FRAMEBUFFER_COMPLETE) {
            System.err.println("Error creating framebuffer!");
            System.exit(1);
        }
        gl.glBindFramebuffer(gl.GL_FRAMEBUFFER, 0);
    }

    private void updateUboForRenderable(Renderable renderable) {
        updateUbo(renderable.getPosition().x(), renderable.getPosition().y(), renderable.getScale(), renderable.getTextureRotation());
    }

    private void updateUbo(double x, double y, double scale, double textureRotation) {
        GL4 gl = GLContext.getCurrent().getGL().getGL4();

        Vector2d viewPointPosition = getPlayerPositionOrDefault(new Vector2d(0, 0));

        this.uboDataBuffer.clear();
        this.uboDataBuffer.put(0, this.camera.getPosition().x());
        this.uboDataBuffer.put(1, this.camera.getPosition().y());
        this.uboDataBuffer.put(2, this.camera.getPosition().z());
        this.uboDataBuffer.put(3, 0);
        this.uboDataBuffer.put(4, x);
        this.uboDataBuffer.put(5, y);
        this.uboDataBuffer.put(6, scale);
        this.uboDataBuffer.put(7, 0);
        this.uboDataBuffer.put(8, this.aspectRatio.x());
        this.uboDataBuffer.put(9, this.aspectRatio.y());
        this.uboDataBuffer.put(10, textureRotation);
        this.uboDataBuffer.put(11, 0);
        this.uboDataBuffer.put(12, viewPointPosition.x());
        this.uboDataBuffer.put(13, viewPointPosition.y());
        this.uboDataBuffer.put(14, this.renderTick);

        gl.glBindBuffer(gl.GL_UNIFORM_BUFFER, this.uniformBuffers[0]);
        gl.glBufferData(gl.GL_UNIFORM_BUFFER, 8L * this.uboDataBuffer.capacity(), uboDataBuffer, gl.GL_DYNAMIC_DRAW);
        gl.glBindBuffer(gl.GL_UNIFORM_BUFFER, 0);
        gl.glBindBufferBase(gl.GL_UNIFORM_BUFFER, 0, this.uniformBuffers[0]);
    }

    private Vector2d getPlayerPositionOrDefault(Vector2d defaultVector) {
        return SceneHandler.getInstance().getPlayer() != null ?
                SceneHandler.getInstance().getPlayer().getPosition() : defaultVector;
    }
}
