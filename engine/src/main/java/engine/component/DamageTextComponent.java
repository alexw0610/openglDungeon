package engine.component;

import engine.object.ui.UIText;

public class DamageTextComponent implements Component {
    private static final long serialVersionUID = 3393607852315109274L;

    private final double initTime;
    private final double damage;
    private UIText damageText;
    private boolean criticalHit;
    private boolean isPlayer;

    public DamageTextComponent(double damage) {
        this.initTime = System.nanoTime();
        this.damage = damage;
        criticalHit = false;
        isPlayer = false;
    }

    public double getInitTime() {
        return initTime;
    }

    public double getDamage() {
        return damage;
    }

    public UIText getDamageText() {
        return damageText;
    }

    public boolean isCriticalHit() {
        return criticalHit;
    }

    public void setCriticalHit(boolean criticalHit) {
        this.criticalHit = criticalHit;
    }

    public boolean isPlayer() {
        return isPlayer;
    }

    public void setPlayer(boolean player) {
        isPlayer = player;
    }

    public void setDamageText(UIText damageText) {
        this.damageText = damageText;
    }
}
