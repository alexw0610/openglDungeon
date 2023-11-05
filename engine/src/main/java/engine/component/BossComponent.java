package engine.component;

public class BossComponent implements Component {
    private static final long serialVersionUID = 3174183185306840419L;
    private double lastPrimaryAttack;
    private double lastSecondaryAttack;
    private double lastProximityAttack;
    private double lastAddSpawn;
    private double addSpawnIntervalSeconds;

    public BossComponent() {
        this.lastPrimaryAttack = 0;
        this.lastSecondaryAttack = 0;
        this.lastProximityAttack = 0;
        this.lastAddSpawn = 0;
        this.addSpawnIntervalSeconds = 10;
    }

    public double getLastPrimaryAttack() {
        return lastPrimaryAttack;
    }

    public void setLastPrimaryAttack(double lastPrimaryAttack) {
        this.lastPrimaryAttack = lastPrimaryAttack;
    }

    public double getLastSecondaryAttack() {
        return lastSecondaryAttack;
    }

    public void setLastSecondaryAttack(double lastSecondaryAttack) {
        this.lastSecondaryAttack = lastSecondaryAttack;
    }

    public double getLastProximityAttack() {
        return lastProximityAttack;
    }

    public void setLastProximityAttack(double lastProximityAttack) {
        this.lastProximityAttack = lastProximityAttack;
    }

    public double getLastAddSpawn() {
        return lastAddSpawn;
    }

    public void setLastAddSpawn(double lastAddSpawn) {
        this.lastAddSpawn = lastAddSpawn;
    }

    public double getAddSpawnIntervalSeconds() {
        return addSpawnIntervalSeconds;
    }

    public void setAddSpawnIntervalSeconds(double addSpawnIntervalSeconds) {
        this.addSpawnIntervalSeconds = addSpawnIntervalSeconds;
    }

    @Override
    public void onRemove() {

    }
}