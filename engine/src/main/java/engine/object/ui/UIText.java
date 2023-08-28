package engine.object.ui;

import engine.enums.UIGroupKey;
import engine.handler.UICharacterHandler;
import org.joml.Vector3d;

import java.util.LinkedList;

import static engine.EngineConstants.DEFAULT_FONT_TEXTURE_SIZE;

public class UIText {

    private String key;
    private String text;
    private final double x;
    private final double y;
    private final double width;
    private final double height;
    private final double scale;
    private int layer;
    private Vector3d color;
    private double maxReachedWidth;
    private double maxReachedHeight;
    private boolean visible;
    private UIGroupKey uiGroupKey;
    private LinkedList<UIElement> characters;

    public UIText(String text, double x, double y, double width, double height, double scale) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.maxReachedWidth = 0;
        this.maxReachedHeight = -((37 / DEFAULT_FONT_TEXTURE_SIZE) * this.scale);
        this.color = new Vector3d(1.0, 1.0, 1.0);
        this.visible = true;
        this.uiGroupKey = UIGroupKey.GENERAL;
        characters = generateCharacterInformation();
    }

    private LinkedList<UIElement> generateCharacterInformation() {
        LinkedList<UIElement> characters = new LinkedList<>();
        double xOffsetPointer = this.x;
        double yOffsetPointer = this.y - ((37 / DEFAULT_FONT_TEXTURE_SIZE) * this.scale);
        for (int characterPos = 0; characterPos < this.text.length(); characterPos++) {
            if (this.text.charAt(characterPos) == ' ') {
                double length = getNextWordLength(characterPos);
                if (xOffsetPointer + length - this.x >= this.width) {
                    xOffsetPointer = this.x;
                    yOffsetPointer -= (37 / DEFAULT_FONT_TEXTURE_SIZE) * this.scale;
                    continue;
                }
            }
            UICharacterTemplate uiCharacterTemplate = UICharacterHandler.getInstance().getCharacterTemplate(this.text.charAt(characterPos));
            UIElement character = generateCharacterFromTemplate(xOffsetPointer, yOffsetPointer, uiCharacterTemplate);
            characters.add(character);
            xOffsetPointer = advanceByKerning(xOffsetPointer, characterPos);
            xOffsetPointer += (uiCharacterTemplate.getXadvance() / DEFAULT_FONT_TEXTURE_SIZE) * this.scale;
            updateMaximumDimensions(xOffsetPointer, yOffsetPointer);
        }
        return characters;
    }

    private double getNextWordLength(int startPosition) {
        double totalWordOffsetX = 0.0;
        for (int characterPos = startPosition; characterPos < this.text.length(); characterPos++) {
            if (this.text.charAt(characterPos) == ' ') {
                return totalWordOffsetX;
            }
            UICharacterTemplate uiCharacterTemplate = UICharacterHandler.getInstance().getCharacterTemplate(this.text.charAt(characterPos));
            totalWordOffsetX += (uiCharacterTemplate.getXadvance() / DEFAULT_FONT_TEXTURE_SIZE) * this.scale;
        }
        return totalWordOffsetX;
    }

    private void updateMaximumDimensions(double xOffsetPointer, double yOffsetPointer) {
        if (xOffsetPointer - this.x > maxReachedWidth) {
            maxReachedWidth = xOffsetPointer - this.x;
        }
        if (yOffsetPointer - this.y < maxReachedHeight) {
            maxReachedHeight = yOffsetPointer - this.y;
        }
    }

    private UIElement generateCharacterFromTemplate(double xOffsetPointer, double yOffsetPointer, UICharacterTemplate uiCharacterTemplate) {
        UIElement character = new UIElement(xOffsetPointer,
                yOffsetPointer,
                (uiCharacterTemplate.getWidth() / DEFAULT_FONT_TEXTURE_SIZE) * this.scale,
                (uiCharacterTemplate.getHeight() / DEFAULT_FONT_TEXTURE_SIZE) * this.scale,
                layer,
                "font");
        addTextureInformation(uiCharacterTemplate, character);
        character.setColor(this.color);
        return character;
    }

    private static void addTextureInformation(UICharacterTemplate uiCharacterTemplate, UIElement character) {
        character.setTextureX((double) uiCharacterTemplate.getX() / DEFAULT_FONT_TEXTURE_SIZE);
        character.setTextureY((double) uiCharacterTemplate.getY() / DEFAULT_FONT_TEXTURE_SIZE);
        character.setTextureWidth((double) uiCharacterTemplate.getWidth() / DEFAULT_FONT_TEXTURE_SIZE);
        character.setTextureHeight((double) uiCharacterTemplate.getHeight() / DEFAULT_FONT_TEXTURE_SIZE);
    }

    private double advanceByKerning(double xOffsetPointer, int characterPos) {
        if (characterPos + 1 < this.text.length()) {
            UIKerning kerning = UICharacterHandler.getInstance().getKerning(this.text.charAt(characterPos), this.text.charAt(characterPos + 1));
            if (kerning != null) {
                xOffsetPointer += (kerning.getAmount() / DEFAULT_FONT_TEXTURE_SIZE) * this.scale;
            }
        }
        return xOffsetPointer;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        this.characters = generateCharacterInformation();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
        for (UIElement character : this.characters) {
            character.setLayer(this.layer);
        }
    }

    public LinkedList<UIElement> getCharacters() {
        return characters;
    }

    public Vector3d getColor() {
        return color;
    }

    public void setColor(Vector3d color) {
        this.color = color;
        for (UIElement character : this.characters) {
            character.setColor(this.color);
        }
    }

    public double getMaxReachedWidth() {
        return maxReachedWidth;
    }

    public double getMaxReachedHeight() {
        return maxReachedHeight;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public UIGroupKey getUiGroupKey() {
        return uiGroupKey;
    }

    public void setUiGroupKey(UIGroupKey uiGroupKey) {
        this.uiGroupKey = uiGroupKey;
    }
}
