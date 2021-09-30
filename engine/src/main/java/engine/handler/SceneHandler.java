package engine.handler;

import engine.object.Camera;
import engine.object.Character;
import engine.object.GameObject;
import engine.object.Player;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class SceneHandler {
    private static final SceneHandler SCENE_HANDLER = new SceneHandler();

    private final HashMap<String, ImmutablePair<String, Character>> characters = new HashMap<>();
    private final HashMap<String, ImmutablePair<String, GameObject>> objects = new HashMap<>();
    private ImmutablePair<String, Player> player;
    private Camera activeCamera;

    private SceneHandler() {

    }

    public static SceneHandler getInstance() {
        return SCENE_HANDLER;
    }

    public void addCharacter(String key, Character character) {
        String renderableKey = RenderHandler.RENDER_HANDLER.addToRenderQueue(character);
        ImmutablePair<String, Character> previousEntry = this.characters.put(key, new ImmutablePair<>(renderableKey, character));
        if (previousEntry != null) {
            RenderHandler.RENDER_HANDLER.removeFromRenderQueue(previousEntry.getKey());
        }
    }

    public void removeCharacter(String key) {
        ImmutablePair<String, Character> entry = characters.get(key);
        if (entry != null) {
            this.characters.remove(key);
            RenderHandler.RENDER_HANDLER.removeFromRenderQueue(entry.getKey());
        }
    }

    public Character getCharacter(String key) {
        if (this.characters.containsKey(key)) {
            return this.characters.get(key).getValue();
        }
        return null;
    }

    public boolean containsCharacter(String key) {
        return this.characters.containsKey(key);
    }

    public void addObject(String key, GameObject gameObject) {
        String renderableKey = RenderHandler.RENDER_HANDLER.addToRenderQueue(gameObject);
        synchronized (this.objects) {
            ImmutablePair<String, GameObject> previousEntry = this.objects.put(key, new ImmutablePair<>(renderableKey, gameObject));
            if (previousEntry != null) {
                RenderHandler.RENDER_HANDLER.removeFromRenderQueue(previousEntry.getKey());
            }
        }
    }

    public void addObject(GameObject gameObject) {
        addObject(RandomStringUtils.randomAlphanumeric(16), gameObject);
    }

    public void removeObject(String key) {
        ImmutablePair<String, GameObject> entry = this.objects.get(key);
        if (entry != null) {
            this.objects.remove(key);
            RenderHandler.RENDER_HANDLER.removeFromRenderQueue(entry.getKey());
        }
    }

    public GameObject getObject(String key) {
        return this.objects.get(key).getValue();
    }

    public Collection<GameObject> getObjects() {
        List<GameObject> gameObjects = new ArrayList<>();
        synchronized (this.objects) {
            for (ImmutablePair<String, GameObject> set : this.objects.values()) {
                gameObjects.add(set.getValue());
            }
        }
        return gameObjects;
    }

    public Player getPlayer() {
        if (this.player != null) {
            return this.player.getValue();
        }
        return null;
    }

    public void setPlayer(Player player) {
        String key = RenderHandler.RENDER_HANDLER.addToRenderQueue(player);
        this.player = new ImmutablePair<>(key, player);
    }

    public void removePlayer() {
        RenderHandler.RENDER_HANDLER.removeFromRenderQueue(this.player.getKey());
        this.player = null;
    }

    public void setActiveCamera(Camera camera) {
        this.activeCamera = camera;
    }

    public Camera getActiveCamera() {
        return this.activeCamera;
    }
}
