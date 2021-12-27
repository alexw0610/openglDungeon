package engine.service;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;
import engine.component.RenderComponent;
import engine.component.TransformationComponent;
import engine.enums.RenderMode;
import engine.enums.ShaderType;
import engine.enums.TextureKey;
import engine.handler.MeshHandler;
import engine.handler.ShaderHandler;
import engine.handler.TextureHandler;
import engine.object.Mesh;
import org.joml.Vector2d;
import org.joml.Vector3d;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

import static engine.EngineConstants.WINDOW_HEIGHT;
import static engine.EngineConstants.WINDOW_WIDTH;

public class RenderService {

    private static RenderService INSTANCE;

    private final MeshHandler meshHandler = MeshHandler.getInstance();
    private final ShaderHandler shaderHandler = ShaderHandler.SHADER_HANDLER;
    private final TextureHandler textureHandler = TextureHandler.TEXTURE_HANDLER;

    private final int[] uniformBuffers;
    private final int[] frameBuffers;
    private final int[] renderedTextures;

    private final DoubleBuffer uboDataBuffer;
    private final DoubleBuffer lightUboDataBuffer;

    public static double cameraPosX;
    public static double cameraPosY;
    public static double cameraPosZ;

    public static double renderTick = 0;

    public static double entitiesRendered = 0;
    public static double lightsRendered = 0;
    public static double viewMapsRendered = 0;

    Vector2d aspectRatio;

    private RenderMode activeRenderMode = RenderMode.INITIAL;

    public static RenderService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RenderService();
        }
        return INSTANCE;
    }

    private RenderService() {
        GL4 gl = GLContext.getCurrent().getGL().getGL4();
        this.uniformBuffers = new int[2];
        this.uboDataBuffer = DoubleBuffer.allocate(19);
        this.lightUboDataBuffer = DoubleBuffer.allocate(16);
        gl.glGenBuffers(2, uniformBuffers, 0);
        this.frameBuffers = new int[2];
        this.renderedTextures = new int[2];
        gl.glGenTextures(2, this.renderedTextures, 0);
        gl.glGenFramebuffers(2, frameBuffers, 0);
        this.aspectRatio = new Vector2d(0, WINDOW_WIDTH / WINDOW_HEIGHT);
        linkTextureToFbo(this.frameBuffers[0], this.renderedTextures[0]);
        linkTextureToFbo(this.frameBuffers[1], this.renderedTextures[1]);
        gl.glEnable(gl.GL_BLEND);
        gl.glEnable(gl.GL_DEPTH_TEST);
        gl.glClearColor(.008f, .005f, .007f, 0);
        RenderService.cameraPosX = 0;
        RenderService.cameraPosY = 0;
        RenderService.cameraPosZ = 0.25;
        gl.glBindBuffer(gl.GL_UNIFORM_BUFFER, this.uniformBuffers[0]);
        gl.glBufferData(gl.GL_UNIFORM_BUFFER, 8L * this.uboDataBuffer.capacity(), uboDataBuffer, gl.GL_DYNAMIC_DRAW);
        gl.glBindBuffer(gl.GL_UNIFORM_BUFFER, this.uniformBuffers[1]);
        gl.glBufferData(gl.GL_UNIFORM_BUFFER, 8L * this.lightUboDataBuffer.capacity(), lightUboDataBuffer, gl.GL_DYNAMIC_DRAW);
        gl.glBindBufferBase(gl.GL_UNIFORM_BUFFER, 0, this.uniformBuffers[0]);
        gl.glBindBufferBase(gl.GL_UNIFORM_BUFFER, 1, this.uniformBuffers[1]);
    }

    public void switchRenderMode(RenderMode renderMode) {
        if (!activeRenderMode.equals(renderMode)) {
            GL4 gl = GLContext.getCurrent().getGL().getGL4();
            switch (renderMode) {
                case ENTITY:
                    gl.glBindFramebuffer(gl.GL_FRAMEBUFFER, 0);
                    gl.glDisable(gl.GL_DEPTH_TEST);
                    gl.glBlendEquationSeparate(gl.GL_FUNC_ADD, gl.GL_FUNC_ADD);
                    gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
                    gl.glActiveTexture(gl.GL_TEXTURE1);
                    gl.glBindTexture(gl.GL_TEXTURE_2D, this.renderedTextures[0]);
                    gl.glActiveTexture(gl.GL_TEXTURE2);
                    gl.glBindTexture(gl.GL_TEXTURE_2D, this.renderedTextures[1]);
                    break;
                case VIEW:
                    gl.glBindFramebuffer(gl.GL_FRAMEBUFFER, frameBuffers[0]);
                    gl.glEnable(gl.GL_DEPTH_TEST);
                    gl.glDepthFunc(gl.GL_LEQUAL);
                    gl.glBlendEquationSeparate(gl.GL_FUNC_ADD, gl.GL_FUNC_ADD);
                    gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
                    shaderHandler.bindShaderOfType(ShaderType.VIEW_POLYGON_SHADER.value());
                    textureHandler.bindTextureWithKey(TextureKey.DEFAULT.value(), gl.GL_TEXTURE0);
                    gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_FILL);
                    break;
                case LIGHT:
                    gl.glBindFramebuffer(gl.GL_FRAMEBUFFER, frameBuffers[1]);
                    gl.glBlendEquationSeparate(gl.GL_MAX, gl.GL_MAX);
                    gl.glEnable(gl.GL_DEPTH_TEST);
                    gl.glDepthFunc(gl.GL_LEQUAL);
                    shaderHandler.bindShaderOfType(ShaderType.LIGHT_POLYGON_SHADER.value());
                    textureHandler.bindTextureWithKey(TextureKey.DEFAULT.value(), gl.GL_TEXTURE0);
                    gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_FILL);
                    break;
            }
            activeRenderMode = renderMode;
        }
    }

    public void renderComponent(RenderComponent renderComponent, TransformationComponent transformationComponent) {
        GL4 gl = GLContext.getCurrent().getGL().getGL4();
        switchRenderMode(RenderMode.ENTITY);
        if (renderComponent.getShaderType() != null) {
            shaderHandler.bindShaderOfType(renderComponent.getShaderType());
        } else {
            shaderHandler.bindShaderOfType(ShaderType.DEFAULT.value());
        }
        if (renderComponent.getTextureKey() != null) {
            textureHandler.bindTextureWithKey(renderComponent.getTextureKey(), gl.GL_TEXTURE0);
        } else {
            textureHandler.bindTextureWithKey(TextureKey.DEFAULT.value(), gl.GL_TEXTURE0);
        }
        updateUbo(transformationComponent.getPositionX(),
                transformationComponent.getPositionY(),
                renderComponent.getScale(),
                renderComponent.getTextureOffSetX(),
                renderComponent.getTextureOffSetY(),
                renderComponent.getTextureRotation(),
                renderComponent.isAlwaysVisible(),
                renderComponent.isShadeless(),
                renderComponent.isMirrored(),
                renderComponent.getPerspectiveLayer(),
                renderComponent.getColorOverride().x(),
                renderComponent.getColorOverride().y(),
                renderComponent.getColorOverride().z());
        drawCall(meshHandler.getMeshForKey(renderComponent.getMeshKey()), gl.GL_TRIANGLES);
        entitiesRendered++;
    }

    public void renderToViewMap(Mesh mesh) {
        GL4 gl = GLContext.getCurrent().getGL().getGL4();
        switchRenderMode(RenderMode.VIEW);
        updateUbo(0, 0, 1, 1, 0, 0, false, false, false, 0, -1, -1, -1);
        drawCall(mesh, gl.GL_TRIANGLES);
        viewMapsRendered++;
    }

    public void renderToLightMap(Mesh mesh, Vector2d lightPosition, double lightStrength, double lightFallOff, Vector3d lightColor) {
        GL4 gl = GLContext.getCurrent().getGL().getGL4();
        switchRenderMode(RenderMode.LIGHT);
        updateLightUbo(lightPosition.x(), lightPosition.y(), 1, lightStrength, lightFallOff, lightColor);
        drawCall(mesh, gl.GL_TRIANGLES);
        lightsRendered++;
    }

    private void drawCall(Mesh mesh, int GL_RENDER_MODE) {
        GL4 gl = GLContext.getCurrent().getGL().getGL4();
        gl.glBindVertexArray(mesh.getVaoId());
        gl.glEnableVertexAttribArray(0);
        gl.glEnableVertexAttribArray(1);
        gl.glDrawElements(GL_RENDER_MODE, mesh.getIndices().length, gl.GL_UNSIGNED_INT, 0);
        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);
        gl.glBindVertexArray(0);
    }

    private void updateUbo(double x,
                           double y,
                           double scale,
                           double textureOffSetX,
                           double textureOffSetY,
                           double textureRotation,
                           boolean alwaysVisible,
                           boolean shadeless,
                           boolean mirrored,
                           double perspectiveLayer,
                           double colorROverwrite,
                           double colorGOverwrite,
                           double colorBOverwrite) {
        GL4 gl = GLContext.getCurrent().getGL().getGL4();

        this.uboDataBuffer.clear();
        this.uboDataBuffer.put(0, RenderService.cameraPosX);
        this.uboDataBuffer.put(1, RenderService.cameraPosY);
        this.uboDataBuffer.put(2, RenderService.cameraPosZ);
        this.uboDataBuffer.put(3, scale);

        this.uboDataBuffer.put(4, x);
        this.uboDataBuffer.put(5, y);
        this.uboDataBuffer.put(6, textureOffSetX);
        this.uboDataBuffer.put(7, textureOffSetY);

        this.uboDataBuffer.put(8, this.aspectRatio.x());
        this.uboDataBuffer.put(9, this.aspectRatio.y());
        this.uboDataBuffer.put(10, textureRotation);
        this.uboDataBuffer.put(11, renderTick);

        this.uboDataBuffer.put(12, alwaysVisible ? 1 : 0);
        this.uboDataBuffer.put(13, shadeless ? 1 : 0);
        this.uboDataBuffer.put(14, mirrored ? -1 : 1);
        this.uboDataBuffer.put(15, perspectiveLayer);

        this.uboDataBuffer.put(16, colorROverwrite);
        this.uboDataBuffer.put(17, colorGOverwrite);
        this.uboDataBuffer.put(18, colorBOverwrite);

        gl.glBindBuffer(gl.GL_UNIFORM_BUFFER, this.uniformBuffers[0]);
        ByteBuffer byteBuffer = gl.glMapBuffer(gl.GL_UNIFORM_BUFFER, GL.GL_WRITE_ONLY);
        DoubleBuffer doubleBuffer = byteBuffer.asDoubleBuffer();
        doubleBuffer.put(this.uboDataBuffer);
        gl.glUnmapBuffer(gl.GL_UNIFORM_BUFFER);
    }

    private void updateLightUbo(double x, double y, double textureOffSet, double lightStrength, double lightFallOff, Vector3d lightColor) {
        GL4 gl = GLContext.getCurrent().getGL().getGL4();

        this.lightUboDataBuffer.clear();
        this.lightUboDataBuffer.put(0, RenderService.cameraPosX);
        this.lightUboDataBuffer.put(1, RenderService.cameraPosY);
        this.lightUboDataBuffer.put(2, RenderService.cameraPosZ);
        this.lightUboDataBuffer.put(3, renderTick);
        this.lightUboDataBuffer.put(4, x);
        this.lightUboDataBuffer.put(5, y);
        this.lightUboDataBuffer.put(6, textureOffSet);
        this.lightUboDataBuffer.put(7, 0);
        this.lightUboDataBuffer.put(8, this.aspectRatio.x());
        this.lightUboDataBuffer.put(9, this.aspectRatio.y());
        this.lightUboDataBuffer.put(10, lightStrength);
        this.lightUboDataBuffer.put(11, lightFallOff);
        this.lightUboDataBuffer.put(12, lightColor.x());
        this.lightUboDataBuffer.put(13, lightColor.y());
        this.lightUboDataBuffer.put(14, lightColor.z());
        this.lightUboDataBuffer.put(15, 0);

        gl.glBindBuffer(gl.GL_UNIFORM_BUFFER, this.uniformBuffers[1]);
        ByteBuffer byteBuffer = gl.glMapBuffer(gl.GL_UNIFORM_BUFFER, GL.GL_WRITE_ONLY);
        DoubleBuffer doubleBuffer = byteBuffer.asDoubleBuffer();
        doubleBuffer.put(this.lightUboDataBuffer);
        gl.glUnmapBuffer(gl.GL_UNIFORM_BUFFER);
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
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_WRAP_T, gl.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_WRAP_S, gl.GL_CLAMP_TO_EDGE);
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

    public void clearCall() {
        GL4 gl = GLContext.getCurrent().getGL().getGL4();
        gl.glBindFramebuffer(gl.GL_FRAMEBUFFER, 0);
        gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT);
        gl.glBindFramebuffer(gl.GL_FRAMEBUFFER, this.frameBuffers[0]);
        gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT);
        gl.glBindFramebuffer(gl.GL_FRAMEBUFFER, this.frameBuffers[1]);
        gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT);
        entitiesRendered = 0;
        lightsRendered = 0;
        viewMapsRendered = 0;
        activeRenderMode = RenderMode.INITIAL;
    }

}
