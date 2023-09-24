package engine.handler;

import engine.enums.UIGroupKey;
import engine.object.ui.UIElement;
import engine.object.ui.UIText;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UIHandler implements Handler<UIElement> {
    private static final ThreadLocal<UIHandler> INSTANCE = ThreadLocal.withInitial(UIHandler::new);
    private final Map<String, UIElement> elements = new HashMap<>();
    private final Map<String, UIText> texts = new HashMap<>();

    private UIHandler() {
    }

    public static UIHandler getInstance() {
        return INSTANCE.get();
    }

    public static void setInstance(UIHandler uiHandler) {
        INSTANCE.set(uiHandler);
    }

    @Override
    public void addObject(String key, UIElement object) {
        object.setElementKey(key);
        this.elements.put(key, object);
    }

    public void addObject(String key, UIText object) {
        object.setKey(key);
        this.texts.put(key, object);
    }

    @Override
    public void addObject(UIElement object) {
        String key = RandomStringUtils.randomAlphanumeric(16);
        addObject(key, object);
    }

    public void addObject(UIText object) {
        String key = RandomStringUtils.randomAlphanumeric(16);
        addObject(key, object);
    }

    @Override
    public UIElement getObject(String key) {
        return this.elements.get(key);
    }

    public List<UIElement> getAllObjects() {
        return new ArrayList<>(this.elements.values());
    }

    public UIText getTextObject(String key) {
        return this.texts.get(key);
    }

    public List<UIText> getAllTextObjects() {
        return new ArrayList<>(this.texts.values());
    }

    @Override
    public void removeObject(String key) {
        this.elements.remove(key);
    }

    @Override
    public void cleanup() {
        this.elements.clear();
        this.texts.clear();
    }

    public void removeTextObject(String key) {
        this.texts.remove(key);
    }

    public void removeAllObjects() {
        this.elements.clear();
    }

    public void removeAllObjectsWithPrefix(String prefix) {
        removeObjectsWithPrefix(prefix);
        removeTextObjectsWithPrefix(prefix);
    }

    public void removeTextObjectsWithPrefix(String prefix) {
        for (String key : this.texts.keySet().stream().filter(key -> key.startsWith(prefix)).collect(Collectors.toList())) {
            removeTextObject(key);
        }
    }

    public void removeObjectsWithPrefix(String prefix) {
        for (String key : this.elements.keySet().stream().filter(key -> key.startsWith(prefix)).collect(Collectors.toList())) {
            removeObject(key);
        }
    }

    public int getElementCount() {
        return this.elements.size() + this.texts.size();
    }
}
