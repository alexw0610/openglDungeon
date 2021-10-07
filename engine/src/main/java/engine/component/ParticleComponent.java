package engine.component;

import engine.enums.TextureKey;

public class ParticleComponent implements Component {
    private TextureKey particleTexture;
    private double particleFrequency;
    private double particleAmount;
    private double particleSize;

    private double emittedLast;

    public ParticleComponent(TextureKey particleTexture, double particleFrequency, double particleAmount, double particleSize) {
        this.particleTexture = particleTexture;
        this.particleFrequency = particleFrequency;
        this.particleAmount = particleAmount;
        this.particleSize = particleSize;
    }

    public TextureKey getParticleTexture() {
        return particleTexture;
    }

    public void setParticleTexture(TextureKey particleTexture) {
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

    public double getEmittedLast() {
        return emittedLast;
    }

    public void setEmittedLast(double emittedLast) {
        this.emittedLast = emittedLast;
    }
}
