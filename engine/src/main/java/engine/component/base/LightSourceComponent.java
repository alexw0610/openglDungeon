package engine.component.base;

import engine.component.Component;
import org.joml.Vector3d;

public class LightSourceComponent implements Component {

    private static final long serialVersionUID = 3303986516626186876L;
    private Vector3d lightColor;
    private double lightStrength;
    private double lightFallOff;

    public LightSourceComponent(Double lightColorR, Double lightColorG, Double lightColorB, Double lightStrength, Double lightFallOff) {
        this.lightColor = new Vector3d(lightColorR, lightColorG, lightColorB);
        this.lightStrength = lightStrength;
        this.lightFallOff = lightFallOff;
    }

    public LightSourceComponent(Vector3d lightColor, double lightStrength, double lightFallOff) {
        this.lightColor = lightColor;
        this.lightStrength = lightStrength;
        this.lightFallOff = lightFallOff;
    }

    public Vector3d getLightColor() {
        return lightColor;
    }

    public void setLightColor(Vector3d lightColor) {
        this.lightColor = lightColor;
    }

    public double getLightStrength() {
        return lightStrength;
    }

    public void setLightStrength(double lightStrength) {
        this.lightStrength = lightStrength;
    }

    public double getLightFallOff() {
        return lightFallOff;
    }

    public void setLightFallOff(double lightFallOff) {
        this.lightFallOff = lightFallOff;
    }

    @Override
    public void onRemove() {

    }
}
