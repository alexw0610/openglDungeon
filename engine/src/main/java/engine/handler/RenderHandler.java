package engine.handler;

import engine.object.interfaces.Renderable;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;

public class RenderHandler {
    public static final RenderHandler RENDER_HANDLER = new RenderHandler();
    private long frameDelta = 0;

    private final HashMap<String, Renderable> renderables = new HashMap<>();

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

    public String addToRenderQueue(Renderable renderable) {
        String key = generateUnusedRenderableKey();
        synchronized (renderables) {
            this.renderables.put(key, renderable);
        }
        return key;
    }

    private String generateUnusedRenderableKey() {
        String key = RandomStringUtils.randomAlphanumeric(16);
        while (this.renderables.containsKey(key)) {
            key = RandomStringUtils.randomAlphanumeric(16);
        }
        return key;
    }

    public void removeFromRenderQueue(String renderableKey) {
        synchronized (renderables) {
            this.renderables.remove(renderableKey);
        }
    }

    public void setCurrentFrameDeltaMs(long frameDelta) {
        this.frameDelta = frameDelta;
    }

    public long getCurrentFrameDeltaMs() {
        return this.frameDelta;
    }

}
