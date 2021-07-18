package engine.handler;

import engine.object.Camera;
import engine.object.Player;

public class SceneHandler {
    public static final SceneHandler SCENE_HANDLER = new SceneHandler();
    private Player player;
    private Camera activeCamera;

    private SceneHandler(){

    }

    public Player getPlayer(){
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setActiveCamera(Camera camera){
        this.activeCamera = camera;
    }

    public Camera getActiveCamera() {
        return this.activeCamera;
    }
}
