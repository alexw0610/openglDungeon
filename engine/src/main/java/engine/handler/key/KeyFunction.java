package engine.handler.key;

import engine.object.Camera;
import engine.object.Player;

public interface KeyFunction {
    void run(double frameDelta, Player player, Camera camera);
}
