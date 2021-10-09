package engine.handler;

import engine.object.NavMap;

public class NavHandler {

    private static final NavHandler INSTANCE = new NavHandler();
    private NavMap navMap;

    private NavHandler() {
    }

    public static NavHandler getInstance() {
        return INSTANCE;
    }

    public void setNavMap(NavMap navMap) {
        this.navMap = navMap;
    }

    public NavMap getNavMap() {
        return this.navMap;
    }

}
