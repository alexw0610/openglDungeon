package engine.object.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UIGroup {

    private final List<UIElement> uiElementList = new ArrayList<>();
    private final List<UIText> uiTextList = new ArrayList<>();

    public List<UIElement> getUiElementList() {
        return uiElementList;
    }

    public void addUiElements(UIElement... uiElements) {
        this.uiElementList.addAll(Arrays.asList(uiElements));
    }

    public List<UIText> getUiTextList() {
        return uiTextList;
    }

    public void addUiTexts(UIText... uiTexts) {
        this.uiTextList.addAll(Arrays.asList(uiTexts));
    }
}
