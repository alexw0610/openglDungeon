package engine.object.ui;

import engine.enums.Color;
import engine.handler.CharacterMeshHandler;
import org.joml.Vector2d;
import org.joml.Vector2f;

public class UIText {
    private final char[] characters;
    private final float[] characterOffsets;
    private final float totalWidth;
    private double fontSize = 0.0005;
    private double spacing = 1.15;
    private int layer = 1;
    private Color color;
    private Vector2d screenPosition;
    private boolean fixedSize;

    public UIText(String content) {
        this.fixedSize = false;
        this.characters = new char[content.length()];
        this.characterOffsets = new float[content.length() * 2];
        float totalOffsetX = 0;
        for (int i = 0; i < content.length(); i++) {
            char character = content.charAt(i);
            characters[i] = character;
            Vector2f characterOffset = CharacterMeshHandler.getInstance().getDimensionForChar(character);
            float kerning = 0;
            if (i > 0) {
                kerning = CharacterMeshHandler.getInstance().getKerningForCharAAfterCharB(characters[i - 1], character);
            }
            characterOffsets[(2 * i)] = totalOffsetX + (characterOffset.x / 2.0f) + kerning;
            totalOffsetX += characterOffset.x + kerning;
            characterOffsets[(2 * i) + 1] = 0; // Potential height offset
        }
        this.totalWidth = totalOffsetX;
    }

    public char[] getCharacters() {
        return characters;
    }

    public float[] getCharacterOffsets() {
        return characterOffsets;
    }

    public UIText centered() {
        for (int i = 0; i < this.characters.length; i++) {
            this.characterOffsets[2 * i] -= totalWidth / 2.0f;
        }
        return this;
    }

    public UIText fontSize(double fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public UIText spacing(double spacing) {
        this.spacing = spacing;
        return this;
    }

    public UIText layer(int layer) {
        this.layer = layer;
        return this;
    }

    public UIText fixedSize(boolean fixedSize) {
        this.fixedSize = fixedSize;
        return this;
    }

    public double getFontSize() {
        return fontSize;
    }

    public double getSpacing() {
        return spacing;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Vector2d getScreenPosition() {
        return screenPosition;
    }

    public void setScreenPosition(Vector2d screenPosition) {
        this.screenPosition = screenPosition;
    }

    public boolean isFixedSize() {
        return fixedSize;
    }

    public void setFixedSize(boolean fixedSize) {
        this.fixedSize = fixedSize;
    }

    public float getTotalWidth() {
        return totalWidth;
    }
}
