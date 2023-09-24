package engine.object.ui;

import engine.enums.UIGroupKey;
import org.joml.Vector3d;

public class UIElement {

    private double x;
    private double y;
    private double width;
    private double height;
    private double scale;
    private double textureX;
    private double textureY;
    private double textureWidth;
    private double textureHeight;
    private boolean fixedTextureSize;
    private int layer;
    private String textureKey;
    private Vector3d color;
    private UIGroupKey uiGroupKey;

    private boolean alwaysVisible;

    private String elementKey;

    public UIElement(double x, double y, double width, double height, int layer, String texture) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.layer = layer;
        this.textureKey = texture;
        this.fixedTextureSize = false;
        this.scale = 1.0;
        this.textureX = 0;
        this.textureY = 0;
        this.textureWidth = 1;
        this.textureHeight = 1;
        this.color = new Vector3d(1, 1, 1);
        this.uiGroupKey = UIGroupKey.GENERAL;
        this.alwaysVisible = true;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public String getTextureKey() {
        return textureKey;
    }

    public void setTextureKey(String textureKey) {
        this.textureKey = textureKey;
    }

    public boolean isFixedTextureSize() {
        return fixedTextureSize;
    }

    public void setFixedTextureSize(boolean fixedTextureSize) {
        this.fixedTextureSize = fixedTextureSize;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public double getTextureX() {
        return textureX;
    }

    public void setTextureX(double textureX) {
        this.textureX = textureX;
    }

    public double getTextureY() {
        return textureY;
    }

    public void setTextureY(double textureY) {
        this.textureY = textureY;
    }

    public double getTextureWidth() {
        return textureWidth;
    }

    public void setTextureWidth(double textureWidth) {
        this.textureWidth = textureWidth;
    }

    public double getTextureHeight() {
        return textureHeight;
    }

    public void setTextureHeight(double textureHeight) {
        this.textureHeight = textureHeight;
    }

    public Vector3d getColor() {
        return color;
    }

    public void setColor(Vector3d color) {
        this.color = color;
    }

    public UIGroupKey getUiGroupKey() {
        return uiGroupKey;
    }

    public void setUiGroupKey(UIGroupKey uiGroupKey) {
        this.uiGroupKey = uiGroupKey;
    }

    public String getElementKey() {
        return elementKey;
    }

    public void setElementKey(String elementKey) {
        this.elementKey = elementKey;
    }

    public boolean isAlwaysVisible() {
        return alwaysVisible;
    }

    public void setAlwaysVisible(boolean alwaysVisible) {
        this.alwaysVisible = alwaysVisible;
    }
}
