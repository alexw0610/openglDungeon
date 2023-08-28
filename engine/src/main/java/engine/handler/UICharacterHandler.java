package engine.handler;

import engine.loader.UICharacterLoader;
import engine.object.ui.UICharacterTemplate;
import engine.object.ui.UIKerning;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

public class UICharacterHandler {
    private static UICharacterHandler INSTANCE;
    private final Map<Integer, UICharacterTemplate> uiCharacterMap = new HashMap<>();
    private final Map<KerningIdPair, UIKerning> uiKerningMap = new HashMap<>();


    public static UICharacterHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UICharacterHandler();
        }
        return INSTANCE;
    }

    private UICharacterHandler() {
        LinkedList<UICharacterTemplate> uiCharacterTemplates = UICharacterLoader.loadCharacters();
        for (UICharacterTemplate character : uiCharacterTemplates) {
            addCharacterTemplate(character);
        }
        LinkedList<UIKerning> uiKernings = UICharacterLoader.loadKernings();
        for (UIKerning kerning : uiKernings) {
            addKerningTemplate(kerning);
        }
    }

    public UICharacterTemplate getCharacterTemplate(int characterId) {
        return uiCharacterMap.get(characterId);
    }

    public UIKerning getKerning(int firstCharacterId, int secondCharacterId) {
        return uiKerningMap.get(new KerningIdPair(firstCharacterId, secondCharacterId));
    }

    private void addCharacterTemplate(UICharacterTemplate uiCharacterTemplate) {
        uiCharacterMap.put(uiCharacterTemplate.getId(), uiCharacterTemplate);
    }

    private void addKerningTemplate(UIKerning kerning) {
        this.uiKerningMap.put(new KerningIdPair(kerning.getFirstCharacterId(), kerning.getSecondCharacterId()), kerning);
    }

    private static class KerningIdPair {
        private final int firstCharacterId;
        private final int secondCharacterId;

        public KerningIdPair(int firstCharacterId, int secondCharacterId) {
            this.firstCharacterId = firstCharacterId;
            this.secondCharacterId = secondCharacterId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            KerningIdPair that = (KerningIdPair) o;
            return firstCharacterId == that.firstCharacterId && secondCharacterId == that.secondCharacterId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(firstCharacterId, secondCharacterId);
        }
    }


}
