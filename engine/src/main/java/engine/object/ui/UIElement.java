package engine.object.ui;

import engine.enums.Color;
import engine.enums.TextureKey;

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

    public void setPosTopLeftX(double posTopLeftX) {
        this.posTopLeftX = posTopLeftX;
    }

    public double getPosTopLeftY() {
        return posTopLeftY;
    }

    public void setPosTopLeftY(double posTopLeftY) {
        this.posTopLeftY = posTopLeftY;
    }

    public double getPosBottomRightX() {
        return posBottomRightX;
    }

    public void setPosBottomRightX(double posBottomRightX) {
        this.posBottomRightX = posBottomRightX;
    }

    public double getPosBottomRightY() {
        return posBottomRightY;
    }

    public void setPosBottomRightY(double posBottomRightY) {
        this.posBottomRightY = posBottomRightY;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public String getColorString() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Color getColor() {
        return Color.getColorForKey(color);
    }

    public double getScreenPositionX() {
        return (this.posBottomRightX + this.posTopLeftX) / 2;
    }

    public void setScreenPositionX(double screenPositionX) {
        this.posTopLeftX = screenPositionX - this.width / 2;
        this.posBottomRightX = screenPositionX + this.width / 2;
    }

    public double getScreenPositionY() {
        return (this.posBottomRightY + this.posTopLeftY) / 2;
    }

    public void setScreenPositionY(double screenPositionY) {
        this.posTopLeftY = screenPositionY + this.height / 2;
        this.posBottomRightY = screenPositionY - this.height / 2;
    }

    public double getWidth() {
        return this.posBottomRightX - this.posTopLeftX;
    }

    public double getHeight() {
        return this.posTopLeftY - this.posBottomRightY;
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
