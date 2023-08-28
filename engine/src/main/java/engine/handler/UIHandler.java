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

    public static void setInstance(UIHandler entityHandler) {
        INSTANCE.set(entityHandler);
    }

    @Override
    public void addObject(String key, UIElement object) {
        this.elements.put(key, object);
    }

    public void addObject(String key, UIText object) {
        this.texts.put(key, object);
    }

    @Override
    public void addObject(UIElement object) {
        String key = RandomStringUtils.randomAlphanumeric(16);
        this.elements.put(key, object);
    }

    public String addObject(UIText object) {
        String key = RandomStringUtils.randomAlphanumeric(16);
        this.texts.put(key, object);
        return key;
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

    public void removeTextObject(String key) {
        this.texts.remove(key);
    }

    public void removeAllObjects() {
        this.elements.clear();
    }

    public void removeTextObjectsWithPrefix(String prefix) {
        for (String key : this.texts.keySet().stream().filter(key -> key.startsWith(prefix)).collect(Collectors.toList())) {
            removeTextObject(key);
        }
    }

    public void toggleUIGroupVisible(UIGroupKey targetGroupKey, boolean visible) {
        this.elements.values().stream().filter(element -> element.getUiGroupKey().equals(targetGroupKey)).forEach(element -> element.setVisible(visible));
        this.texts.values().stream().filter(text -> text.getUiGroupKey().equals(targetGroupKey)).forEach(text -> text.setVisible(visible));
    }
}
