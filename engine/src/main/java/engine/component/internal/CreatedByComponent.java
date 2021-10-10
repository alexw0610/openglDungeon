package engine.component.internal;

import engine.component.Component;
import engine.entity.Entity;

public class CreatedByComponent implements Component {
    private final Entity creatorEntity;

    public CreatedByComponent(Entity creatorEntity) {
        this.creatorEntity = creatorEntity;
    }

    public Entity getCreatorEntity() {
        return creatorEntity;
    }
}
