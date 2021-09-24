package engine.handler;

import engine.object.Mesh;
import engine.object.interfaces.Renderable;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RenderHandler {
    public static final RenderHandler RENDER_HANDLER = new RenderHandler();
    private long frameDelta = 0;

    private final HashMap<String, Renderable> renderables = new HashMap<>();
    private final List<Mesh> debugMeshes = new ArrayList<>();

    private RenderHandler() {
    }

    public Renderable[] getRenderables() {
        Renderable[] renderablesArray;
        synchronized (renderables) {
            renderablesArray = new Renderable[renderables.size()];
            renderables.values().toArray(renderablesArray);
        }
        return renderablesArray;
    }

    public Mesh[] getDebugMeshes() {
        Mesh[] meshesArray;
        synchronized (debugMeshes) {
            meshesArray = new Mesh[debugMeshes.size()];
            debugMeshes.toArray(meshesArray);
        }
        return meshesArray;
    }

    public String addToRenderQueue(Renderable renderable) {
        String key = generateUnusedRenderableKey();
        synchronized (renderables) {
            this.renderables.put(key, renderable);
        }
        return key;
    }

    public void addToDebugMeshes(Mesh mesh) {
        synchronized (debugMeshes) {
            this.debugMeshes.add(mesh);
        }
    }

    public void removeFromRenderQueue(String renderableKey) {
        synchronized (renderables) {
            this.renderables.remove(renderableKey);
        }
    }

    public void clearDebugMeshes() {
        synchronized (debugMeshes) {
            this.debugMeshes.clear();
        }
    }

    public void setCurrentFrameDeltaMs(long frameDelta) {
        this.frameDelta = frameDelta;
    }

    public long getCurrentFrameDeltaMs() {
        return this.frameDelta;
    }

    private String generateUnusedRenderableKey() {
        String key = RandomStringUtils.randomAlphanumeric(16);
        while (this.renderables.containsKey(key)) {
            key = RandomStringUtils.randomAlphanumeric(16);
        }
        return key;
    }

}
