package engine.component;

import org.joml.Vector2d;

public class ParticleComponent implements Component {
    private static final long serialVersionUID = -3352980751842570691L;
    private String particleTexture;
    private double colorROverride;
    private double colorGOverride;
    private double colorBOverride;
    private double particleFrequency;
    private double particleAmount;
    private double particleSize;
    private double particleLifeTime;
    private double particleVelocity;
    private Vector2d particleDirection;

    private double emittedLast;

    public ParticleComponent(String particleTexture, Double particleFrequency, Double particleAmount, Double particleSize, Double particleLifeTime, Double particleVelocity) {
        this.particleTexture = particleTexture;
        this.particleFrequency = particleFrequency;
        this.particleAmount = particleAmount;
        this.particleSize = particleSize;
        this.particleLifeTime = particleLifeTime;
        this.particleDirection = new Vector2d(1, 1);
        this.particleVelocity = particleVelocity;
    }

    public ParticleComponent(Double r, Double g, Double b, Double particleFrequency, Double particleAmount, Double particleSize, Double particleLifeTime, Double particleVelocity) {
        this.colorROverride = r;
        this.colorGOverride = g;
        this.colorBOverride = b;
        this.particleFrequency = particleFrequency;
        this.particleAmount = particleAmount;
        this.particleSize = particleSize;
        this.particleLifeTime = particleLifeTime;
        this.particleDirection = new Vector2d(1, 1);
        this.particleVelocity = particleVelocity;
    }

    public String getParticleTexture() {
        return particleTexture;
    }

    public void setParticleTexture(String particleTexture) {
        this.particleTexture = particleTexture;
    }

    public double getParticleAmount() {
        return particleAmount;
    }

    public void setParticleAmount(double particleAmount) {
        this.particleAmount = particleAmount;
    }

    public double getParticleSize() {
        return particleSize;
    }

    public void setParticleSize(double particleSize) {
        this.particleSize = particleSize;
    }

    public double getParticleFrequency() {
        return particleFrequency;
    }

    public void setParticleFrequency(double particleFrequency) {
        this.particleFrequency = particleFrequency;
    }

    public double getParticleLifeTime() {
        return particleLifeTime;
    }

    public void setParticleLifeTime(double particleLifeTime) {
        this.particleLifeTime = particleLifeTime;
    }

    public Vector2d getParticleDirection() {
        return particleDirection;
    }

    public void setParticleDirection(Vector2d particleDirection) {
        this.particleDirection = particleDirection;
    }

    public double getParticleVelocity() {
        return particleVelocity;
    }

    public void setParticleVelocity(double particleVelocity) {
        this.particleVelocity = particleVelocity;
    }

    public double getEmittedLast() {
        return emittedLast;
    }

    public void setEmittedLast(double emittedLast) {
        this.emittedLast = emittedLast;
    }

    public double getColorROverride() {
        return colorROverride;
    }

    public void setColorROverride(double colorROverride) {
        this.colorROverride = colorROverride;
    }

    public double getColorGOverride() {
        return colorGOverride;
    }

    public void setColorGOverride(double colorGOverride) {
        this.colorGOverride = colorGOverride;
    }

    public double getColorBOverride() {
        return colorBOverride;
    }

    public void setColorBOverride(double colorBOverride) {
        this.colorBOverride = colorBOverride;
    }
}
