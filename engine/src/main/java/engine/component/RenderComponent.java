package engine.component;

import org.joml.Vector3d;

public class RenderComponent implements Component {

    private String meshKey;
    private String textureKey;
    private String shaderType;
    private double scale;
    private int layer;
    private boolean shadeless;
    private boolean alwaysVisible;
    private double textureOffSetX;
    private double textureOffSetY;
    private double textureRotation;
    private boolean mirrored;
    private double perspectiveLayer;
    private double colorROverride;
    private double colorGOverride;
    private double colorBOverride;

    public RenderComponent(String meshKey, String textureKey, String shaderType, Double scale, Integer layer) {
        super();
        this.meshKey = meshKey;
        this.textureKey = textureKey;
        this.shaderType = shaderType;
        this.scale = scale;
        this.layer = layer;
        this.shadeless = false;
        this.alwaysVisible = false;
        this.textureOffSetX = 0;
        this.textureOffSetY = 0;
        this.textureRotation = 0;
        this.mirrored = false;
        this.perspectiveLayer = 1;
        this.colorROverride = -1.0;
        this.colorGOverride = -1.0;
        this.colorBOverride = -1.0;
    }

    public String getMeshKey() {
        return meshKey;
    }

    public void setMeshKey(String meshKey) {
        this.meshKey = meshKey;
    }

    public String getTextureKey() {
        return textureKey;
    }

    public void setTextureKey(String textureKey) {
        this.textureKey = textureKey;
    }

    public String getShaderType() {
        return shaderType;
    }

    public void setShaderType(String shaderType) {
        this.shaderType = shaderType;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public boolean isShadeless() {
        return shadeless;
    }

    public void setShadeless(Boolean shadeless) {
        this.shadeless = shadeless;
    }

    public boolean isAlwaysVisible() {
        return alwaysVisible;
    }

    public void setAlwaysVisible(Boolean alwaysVisible) {
        this.alwaysVisible = alwaysVisible;
    }

    public double getTextureOffSetX() {
        return textureOffSetX;
    }

    public void setTextureOffSetX(Double textureOffSetX) {
        this.textureOffSetX = textureOffSetX;
    }

    public double getTextureOffSetY() {
        return textureOffSetY;
    }

    public void setTextureOffSetY(Double textureOffSetY) {
        this.textureOffSetY = textureOffSetY;
    }

    public double getTextureRotation() {
        return textureRotation;
    }

    public void setTextureRotation(Double textureRotation) {
        this.textureRotation = textureRotation;
    }

    public boolean isMirrored() {
        return mirrored;
    }

    public void setMirrored(Boolean mirrored) {
        this.mirrored = mirrored;
    }

    public double getPerspectiveLayer() {
        return perspectiveLayer;
    }

    public void setPerspectiveLayer(Double perspectiveLayer) {
        this.perspectiveLayer = perspectiveLayer;
    }

    public Vector3d getColorOverride() {
        return new Vector3d(colorROverride, colorGOverride, colorBOverride);
    }

    public void setColorOverride(Vector3d colorOverride) {
        this.colorROverride = colorOverride.x();
        this.colorGOverride = colorOverride.y();
        this.colorBOverride = colorOverride.z();
    }
}
