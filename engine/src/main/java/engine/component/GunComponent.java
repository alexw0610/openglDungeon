package engine.component;

import engine.enums.UpgradeType;

import java.util.LinkedList;
import java.util.List;

public class GunComponent implements Component {
    private double lastShotTime;
    private double lastBombTime;
    private double bulletSpeed;

    private double bulletCount;
    private final List<UpgradeType> modifiers = new LinkedList<>();

    public double getLastShotTime() {
        return lastShotTime;
    }

    public void setLastShotTime(long lastShotTime) {
        this.lastShotTime = lastShotTime;
    }

    public double getLastBombTime() {
        return lastBombTime;
    }

    public void setLastBombTime(long lastBombTime) {
        this.lastBombTime = lastBombTime;
    }

    public double getBulletSpeed() {
        return bulletSpeed;
    }

    public void setBulletSpeed(Double bulletSpeed) {
        this.bulletSpeed = bulletSpeed;
    }

    public double getBulletCount() {
        return bulletCount;
    }

    public void setBulletCount(double bulletCount) {
        this.bulletCount = bulletCount;
    }

    public List<UpgradeType> getModifiers() {
        return modifiers;
    }

    public void addModifier(UpgradeType mod) {
        this.modifiers.add(mod);
    }
}
