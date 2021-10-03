package engine.component;

import org.joml.Vector3d;

public class LightSourceComponent implements Component {

    private Vector3d lightColor;
    private double lightStrength;
    private double lightFallOff;

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
}
