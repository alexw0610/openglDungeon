package engine.component;

import engine.entity.Entity;
import engine.handler.EntityHandler;

public class StunComponent implements Component {
    private static final long serialVersionUID = 5939391868465433216L;

    private Entity stunSpriteEntity;
    private final double stunStartTime;
    private double stunDurationSeconds;

    public StunComponent(double stunDurationSeconds) {
        this.stunStartTime = System.nanoTime();
        this.stunDurationSeconds = stunDurationSeconds;
    }

    public double getStunStartTime() {
        return stunStartTime;
    }

    public double getStunDurationSeconds() {
        return stunDurationSeconds;
    }

    public void setStunDurationSeconds(double stunDurationSeconds) {
        this.stunDurationSeconds = stunDurationSeconds;
    }

    public Entity getStunSpriteEntity() {
        return stunSpriteEntity;
    }

    public void setStunSpriteEntity(Entity stunSpriteEntity) {
        this.stunSpriteEntity = stunSpriteEntity;
    }

    @Override
    public void onRemove() {
        if (this.stunSpriteEntity != null) {
            EntityHandler.getInstance().removeObject(this.stunSpriteEntity.getEntityId());
        }
    }
}
