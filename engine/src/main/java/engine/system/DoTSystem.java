package engine.system;

import engine.EntityKeyConstants;
import engine.component.DoTComponent;
import engine.component.StatComponent;
import engine.component.base.AnimationComponent;
import engine.component.base.CollisionComponent;
import engine.component.base.RenderComponent;
import engine.component.base.TransformationComponent;
import engine.component.internal.CreatedByComponent;
import engine.component.tag.MobTag;
import engine.component.tag.RangedMobTag;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.PrimitiveMeshShape;
import engine.enums.ShaderType;
import engine.handler.EntityHandler;
import engine.service.util.CollisionUtil;
import engine.system.util.DamageUtil;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class DoTSystem {

    public static void processEntity(Entity entity) {
        DoTComponent doTComponent = entity.getComponentOfType(DoTComponent.class);
        StatComponent statComponent = entity.getComponentOfType(StatComponent.class);
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        CollisionComponent collisionComponent = entity.getComponentOfType(CollisionComponent.class);
        if (maxDurationReached(doTComponent) || entityIsDead(entity)) {
            entity.removeComponent(DoTComponent.class);
            return;
        }
        if (doTComponent.getDotSpriteEntity() == null) {
            createDotSpriteEntity(doTComponent, transformationComponent);
        }
        handleTick(entity, doTComponent);
        handleSpread(entity, doTComponent, transformationComponent, collisionComponent);
    }

    private static boolean entityIsDead(Entity entity) {
        return entity.hasComponentOfType(StatComponent.class) && entity.getComponentOfType(StatComponent.class).isDead();
    }

    private static boolean maxDurationReached(DoTComponent doTComponent) {
        return (System.nanoTime() - doTComponent.getDotStartTime()) > (doTComponent.getDotDurationSeconds() * 1000000000);
    }

    private static void handleSpread(Entity entity, DoTComponent doTComponent, TransformationComponent transformationComponent, CollisionComponent collisionComponent) {
        if (doTComponent.isSpread()) {
            List<Entity> fireSpreadTargets = getCollisions(entity, transformationComponent, collisionComponent);
            for (Entity target : fireSpreadTargets) {
                if (!target.hasComponentOfType(DoTComponent.class)) {
                    DoTComponent dotComponentClone = new DoTComponent(doTComponent.getDotDurationSeconds(),
                            doTComponent.getDotDamagePerTick(),
                            doTComponent.isSpread(),
                            doTComponent.isSlow(),
                            doTComponent.getSlowModifierValue(),
                            doTComponent.getDotSpriteTextureKey());
                    dotComponentClone.setOriginModifier(dotComponentClone.getOriginModifier());
                    target.addComponent(dotComponentClone);
                }
            }
        }
    }

    private static List<Entity> getCollisions(Entity entity, TransformationComponent transformationComponent, CollisionComponent collisionComponent) {
        List<Entity> obstacles = EntityHandler.getInstance().getAllEntitiesWithComponents(TransformationComponent.class, CollisionComponent.class).stream().filter(e -> (e.hasComponentOfType(MobTag.class) || e.hasComponentOfType(RangedMobTag.class))).collect(Collectors.toList());
        obstacles = obstacles.parallelStream().unordered().filter(e -> CollisionUtil.distanceLessThan(e, entity, 4.0)).collect(Collectors.toList());
        obstacles.remove(entity);
        if (entity.hasComponentOfType(CreatedByComponent.class)) {
            obstacles.remove(entity.getComponentOfType(CreatedByComponent.class).getCreatorEntity());
        }
        return CollisionUtil.getCollisions(transformationComponent, collisionComponent, obstacles);
    }

    private static void createDotSpriteEntity(DoTComponent doTComponent, TransformationComponent transformationComponent) {
        RenderComponent renderComponent = new RenderComponent(PrimitiveMeshShape.QUAD.value(), doTComponent.getDotSpriteTextureKey(), ShaderType.DEFAULT.value(), 1.0, 7);
        renderComponent.setAlwaysVisible(true);
        renderComponent.setShadeless(true);
        Entity fireSpriteEntity = EntityBuilder.builder()
                .withComponent(transformationComponent)
                .withComponent(new AnimationComponent(150.0, true, 11, true))
                .withComponent(renderComponent)
                .buildAndInstantiate(EntityKeyConstants.DOT_ENTITY_PREFIX + RandomStringUtils.randomAlphanumeric(6));
        doTComponent.setDotSpriteEntity(fireSpriteEntity);
    }

    private static void handleTick(Entity entity, DoTComponent doTComponent) {
        if ((System.nanoTime() - doTComponent.getLastDotTick()) > (1 * 1000000000)) {
            DamageUtil.applyDamage(entity, doTComponent.getDotDamagePerTick(), 0, 0, false);
            doTComponent.setLastDotTick(System.nanoTime());
        }
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(DoTComponent.class);
    }
}
