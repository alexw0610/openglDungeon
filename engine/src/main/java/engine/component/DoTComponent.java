package engine.component;

import engine.entity.Entity;
import engine.enums.BulletModifier;
import engine.handler.EntityHandler;

public class DoTComponent implements Component {
    private static final long serialVersionUID = -1780562017582146121L;

    private Entity dotSpriteEntity;
    private final double dotStartTime;
    private double dotDurationSeconds;
    private boolean spread;
    private boolean slow;
    private double slowModifierValue;
    private double lastDotTick;
    private double dotDamagePerTick;
    private String dotSpriteTextureKey;

    private BulletModifier originModifier;

    public DoTComponent(Double dotDurationSeconds, Double dotDamagePerTick, Boolean spread, Boolean slow, Double slowModifierValue, String dotSpriteTextureKey) {
        this.dotStartTime = System.nanoTime();
        this.dotDurationSeconds = dotDurationSeconds;
        this.spread = spread;
        this.slow = slow;
        this.slowModifierValue = slowModifierValue;
        this.dotDamagePerTick = dotDamagePerTick;
        this.dotSpriteTextureKey = dotSpriteTextureKey;
        this.lastDotTick = 0;
    }

    public Entity getDotSpriteEntity() {
        return dotSpriteEntity;
    }

    public void setDotSpriteEntity(Entity dotSpriteEntity) {
        this.dotSpriteEntity = dotSpriteEntity;
    }

    public double getDotStartTime() {
        return dotStartTime;
    }

    public double getDotDurationSeconds() {
        return dotDurationSeconds;
    }

    public void setDotDurationSeconds(double dotDurationSeconds) {
        this.dotDurationSeconds = dotDurationSeconds;
    }

    public double getLastDotTick() {
        return lastDotTick;
    }

    public void setLastDotTick(double lastDotTick) {
        this.lastDotTick = lastDotTick;
    }

    public boolean isSpread() {
        return spread;
    }

    public void setSpread(boolean spread) {
        this.spread = spread;
    }

    public boolean isSlow() {
        return slow;
    }

    public void setSlow(boolean slow) {
        this.slow = slow;
    }

    public double getSlowModifierValue() {
        return slowModifierValue;
    }

    public void setSlowModifierValue(double slowModifierValue) {
        this.slowModifierValue = slowModifierValue;
    }

    public double getDotDamagePerTick() {
        return dotDamagePerTick;
    }

    public void setDotDamagePerTick(double dotDamagePerTick) {
        this.dotDamagePerTick = dotDamagePerTick;
    }

    public String getDotSpriteTextureKey() {
        return dotSpriteTextureKey;
    }

    public void setDotSpriteTextureKey(String dotSpriteTextureKey) {
        this.dotSpriteTextureKey = dotSpriteTextureKey;
    }

    public BulletModifier getOriginModifier() {
        return originModifier;
    }

    public void setOriginModifier(BulletModifier originModifier) {
        this.originModifier = originModifier;
    }

    @Override
    public void onRemove() {
        if (this.dotSpriteEntity != null) {
            EntityHandler.getInstance().removeObject(this.dotSpriteEntity.getEntityId());
        }
    }
}
