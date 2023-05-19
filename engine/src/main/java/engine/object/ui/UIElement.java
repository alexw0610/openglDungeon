package engine.object.ui;

import engine.EngineConstants;
import engine.enums.Color;
import engine.enums.TextureKey;
import engine.service.RenderService;

public class UIElement implements Cloneable {

    private String elementId;
    private String textureKey;
    private double posTopLeftX;
    private double posTopLeftY;
    private double posBottomRightX;
    private double posBottomRightY;
    private double screenPositionX;
    private double screenPositionY;
    private double width;
    private double height;
    private String tooltip;
    private boolean fixedSize;
    private int layer;
    private String color;

    public UIElement(double screenPositionX, double screenPositionY, double width, double height) {
        this.screenPositionX = screenPositionX;
        this.screenPositionY = screenPositionY;
        this.posTopLeftX = screenPositionX - width / 2;
        this.posTopLeftY = screenPositionY + height / 2;
        this.posBottomRightX = screenPositionX + width / 2;
        this.posBottomRightY = screenPositionY - height / 2;
        this.width = width;
        this.height = height;
        this.textureKey = TextureKey.DEFAULT.value();
        this.color = Color.BLACK.value();
        this.fixedSize = false;
        this.layer = 1;
    }

    public String getTextureKey() {
        return textureKey;
    }

    public void setTextureKey(String textureKey) {
        this.textureKey = textureKey;
    }

    public double getPosTopLeftX() {
        return posTopLeftX;
    }

    public double getPosTopLeftY() {
        return posTopLeftY;
    }

    public double getPosBottomRightX() {
        return posBottomRightX;
    }

    public double getPosBottomRightY() {
        return posBottomRightY;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Color getColor() {
        return Color.getColorForKey(color);
    }

    public double getScreenPositionX() {
        return this.screenPositionX;
    }

    public void setScreenPositionX(double screenPositionX) {
        this.posTopLeftX = screenPositionX - this.width / 2;
        this.posBottomRightX = screenPositionX + this.width / 2;
        this.screenPositionX = screenPositionX;
    }

    public double getScreenPositionY() {
        return this.screenPositionY;
    }

    public void setScreenPositionY(double screenPositionY) {
        this.posTopLeftY = screenPositionY + (this.height / 2) * EngineConstants.getAspectRatio();
        this.posBottomRightY = screenPositionY - (this.height / 2) * EngineConstants.getAspectRatio();
        this.screenPositionY = screenPositionY;
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public boolean isFixedSize() {
        return fixedSize;
    }

    public void setFixedSize(boolean fixedSize) {
        this.fixedSize = fixedSize;
    }

    @Override
    public UIElement clone() {
        try {
            UIElement clone = (UIElement) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
