package engine;

import engine.handler.EntityHandler;
import engine.handler.EventHandler;
import engine.handler.NavHandler;

public class ServerLoop implements Runnable {
    private final Engine engine;
    private boolean shouldClose;

    public ServerLoop(Engine engine) {
        this.shouldClose = false;
        this.engine = engine;
    }

    @Override
    public void run() {
        setup();
        while (!shouldClose) {
            this.engine.step();
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void setup() {
        this.engine.setEntityHandler(EntityHandler.getInstance());
        this.engine.setNavHandler(NavHandler.getInstance());
        this.engine.setEventHandler(EventHandler.getInstance());
        this.engine.setStarted(true);
    }

    public boolean isShouldClose() {
        return shouldClose;
    }

    public void setShouldClose(boolean shouldClose) {
        this.shouldClose = shouldClose;
    }
}
