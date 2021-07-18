package engine.handler;

import engine.object.Renderable;

import java.util.LinkedList;
import java.util.List;

public class RenderHandler {
    public static final RenderHandler RENDER_HANDLER = new RenderHandler();
    private long frameDelta = 0;

    private final List<Renderable> renderables = new LinkedList<>();

    private RenderHandler() {
    }

    public Renderable[] getRenderables() {
        Renderable[] renderablesArray;
        synchronized (renderables) {
            renderablesArray = new Renderable[renderables.size()];
            renderables.toArray(renderablesArray);
        }
        return renderablesArray;
    }

    public void addToRenderQueue(Renderable renderable) {
        synchronized (renderables) {
            this.renderables.add(renderable);
        }
    }

    public void removeFromRenderQueue(Renderable renderable) {
        synchronized (renderables) {
            this.renderables.remove(renderable);
        }
    }

    public void setCurrentFrameDelta(long frameDelta){
        this.frameDelta = frameDelta;
    }

    public long getCurrentFrameDelta(){
        return this.frameDelta;
    }

}
