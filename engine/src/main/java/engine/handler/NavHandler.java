package engine.handler;

import engine.object.NavMap;

public class NavHandler {

    private static final ThreadLocal<NavHandler> INSTANCE = ThreadLocal.withInitial(NavHandler::new);
    private NavMap navMap;

    private NavHandler() {
    }

    public static NavHandler getInstance() {
        return INSTANCE.get();
    }

    public static void setInstance(NavHandler navHandler) {
        INSTANCE.set(navHandler);
    }

    public void setNavMap(NavMap navMap) {
        this.navMap = navMap;
    }

    public NavMap getNavMap() {
        return this.navMap;
    }

}
