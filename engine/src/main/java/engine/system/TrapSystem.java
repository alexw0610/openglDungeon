package engine.system;

import engine.component.StatComponent;
import engine.component.TrapComponent;
import engine.component.tag.PlayerTag;
import engine.entity.ComponentBuilder;
import engine.entity.Entity;
import engine.handler.EntityHandler;
import engine.service.util.CollisionUtil;

public class TrapSystem {
    public static void processEntity(Entity entity) {
        Entity player = EntityHandler.getInstance().getEntityWithComponent(PlayerTag.class);
        TrapComponent trapComponent = entity.getComponentOfType(TrapComponent.class);
        StatComponent statComponent = entity.getComponentOfType(StatComponent.class);
        if (player != null) {
            if (CollisionUtil.distanceLessThan(entity, player, 1.0)) {
                player.addComponent(ComponentBuilder.fromTemplate(trapComponent.getTargetDotComponentTemplateName()));
                entity.removeComponent(TrapComponent.class);
                if(statComponent != null){
                    statComponent.setCurrentHealthPoints(0.0);
                }
            }
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(TrapComponent.class);
    }
}
