package engine.component;

import engine.component.lambda.Vector2dFunction;
import org.joml.Vector2d;

public class ParticleComponent implements Component {
    private String particleTexture;
    private double particleFrequency;
    private double particleAmount;
    private double particleSize;
    private double particleLifeTime;
    private double particleVelocity;
    private Vector2dFunction particleDirection;

    private double emittedLast;

    public ParticleComponent(String particleTexture, Double particleFrequency, Double particleAmount, Double particleSize, Double particleLifeTime, Double particleVelocity) {
        this.particleTexture = particleTexture;
        this.particleFrequency = particleFrequency;
        this.particleAmount = particleAmount;
        this.particleSize = particleSize;
        this.particleLifeTime = particleLifeTime;
        this.particleDirection = () -> new Vector2d(Math.random() - 0.5, Math.random() - 0.5);
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

    public Vector2dFunction getParticleDirection() {
        return particleDirection;
    }

    public void setParticleDirection(Vector2dFunction particleDirection) {
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
}
