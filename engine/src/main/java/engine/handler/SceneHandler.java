package engine.handler;

import engine.object.Camera;
import engine.object.Character;
import engine.object.GameObject;
import engine.object.Player;
import engine.object.generic.KeyObjectSet;

import java.util.HashMap;

public class SceneHandler {
    public static final SceneHandler SCENE_HANDLER = new SceneHandler();

    private final HashMap<String, KeyObjectSet<String, Character>> characters = new HashMap<>();
    private final HashMap<String, KeyObjectSet<String, GameObject>> objects = new HashMap<>();
    private KeyObjectSet<String, Player> player;
    private Camera activeCamera;

    private SceneHandler() {

    }

    public void addCharacter(String key, Character character) {
        String renderableKey = RenderHandler.RENDER_HANDLER.addToRenderQueue(character);
        KeyObjectSet<String, Character> previousEntry = this.characters.put(key, new KeyObjectSet<>(renderableKey, character));
        if (previousEntry != null) {
            RenderHandler.RENDER_HANDLER.removeFromRenderQueue(previousEntry.key);
        }
    }

    public void removeCharacter(String key) {
        KeyObjectSet<String, Character> entry = characters.get(key);
        if (entry != null) {
            this.characters.remove(key);
            RenderHandler.RENDER_HANDLER.removeFromRenderQueue(entry.key);
        }
    }

    public Character getCharacter(String key) {
        if (this.characters.containsKey(key)) {
            return this.characters.get(key).object;
        }
        return null;
    }

    public boolean containsCharacter(String key) {
        return this.characters.containsKey(key);
    }

    public void addObject(String key, GameObject gameObject) {
        String renderableKey = RenderHandler.RENDER_HANDLER.addToRenderQueue(gameObject);
        KeyObjectSet<String, GameObject> previousEntry = this.objects.put(key, new KeyObjectSet<>(renderableKey, gameObject));
        if (previousEntry != null) {
            RenderHandler.RENDER_HANDLER.removeFromRenderQueue(previousEntry.key);
        }
    }

    public void removeObject(String key) {
        KeyObjectSet<String, GameObject> entry = this.objects.get(key);
        if (entry != null) {
            this.objects.remove(key);
            RenderHandler.RENDER_HANDLER.removeFromRenderQueue(entry.key);
        }
    }

    public GameObject getObject(String key) {
        return this.objects.get(key).object;
    }

    public Player getPlayer() {
        return this.player.object;
    }

    public void setPlayer(Player player) {
        String key = RenderHandler.RENDER_HANDLER.addToRenderQueue(player);
        this.player = new KeyObjectSet<>(key, player);
    }

    public void removePlayer() {
        RenderHandler.RENDER_HANDLER.removeFromRenderQueue(this.player.key);
        this.player = null;
    }

    public void setActiveCamera(Camera camera) {
        this.activeCamera = camera;
    }

    public Camera getActiveCamera() {
        return this.activeCamera;
    }
}
