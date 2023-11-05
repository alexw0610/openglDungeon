package engine.system;

import engine.EntityKeyConstants;
import engine.component.*;
import engine.component.base.PhysicsComponent;
import engine.component.base.RenderComponent;
import engine.component.base.TransformationComponent;
import engine.component.internal.CreatedByComponent;
import engine.component.tag.AiTargetTag;
import engine.component.tag.MobTag;
import engine.component.tag.PlayerTag;
import engine.component.tag.RangedMobTag;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.Slot;
import engine.handler.EntityHandler;
import engine.object.generation.World;
import engine.service.util.CollisionUtil;
import engine.service.util.Pathfinding;
import engine.system.util.AttackUtil;
import engine.system.util.ProjectileUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static engine.enums.AIBehaviourState.*;

public class AISystem {

    public static void processEntity(Entity entity) {
        AIComponent aiComponent = entity.getComponentOfType(AIComponent.class);
        TransformationComponent transformationComponent = entity.getComponentOfType(TransformationComponent.class);
        PhysicsComponent physicsComponent = entity.getComponentOfType(PhysicsComponent.class);
        if (aiComponent.isHostile()) {
            //checkAggro(entity, aiComponent, transformationComponent);
        }
        switch (aiComponent.getCurrentState()) {
            case IDLE:
                idle(aiComponent, transformationComponent);
                break;
            case ATTACKING:
                attacking(entity, aiComponent, transformationComponent);
                break;
            case PATHING:
                pathing(aiComponent, transformationComponent, physicsComponent);
                break;
        }
    }

    private static void idle(AIComponent aiComponent, TransformationComponent transformationComponent) {
        World world = EntityHandler.getInstance().getWorld();
        if (world != null) {
            Vector2d targetPosition = new Vector2d();
            List<Vector2i> pathToTarget = new ArrayList<>();
            Vector2d currentPosition = transformationComponent.getPosition();
            int attempts = 0;
            while (attempts < 9) {
                currentPosition.add((int) Math.round(((Math.random() * 2) - 1) * 6), (int) Math.round(((Math.random() * 2) - 1) * 6), targetPosition);
                pathToTarget = new Pathfinding().getPath(world, currentPosition, targetPosition);
                attempts++;
                if (!pathToTarget.isEmpty()) {
                    aiComponent.setCurrentState(PATHING);
                    aiComponent.setPathToTarget(pathToTarget);
                    break;
                }
            }

        }
    }

    private static void attacking(Entity entity, AIComponent aiComponent, TransformationComponent transformationComponent) {
        StatComponent statComponent = entity.getComponentOfType(StatComponent.class);
        if (aiComponent.getCurrentTarget().getComponentOfType(StatComponent.class).isDead()) {
            aiComponent.setCurrentState(IDLE);
        } else if ((aiComponent.getAttackedLast() == 0
                || aiComponent.getAttackedLast() < System.currentTimeMillis() - (statComponent.getAttackSpeedPrimary() * 1000))
                && !entity.hasComponentOfType(KnockbackComponent.class)
                && !entity.hasComponentOfType(StunComponent.class)) {
            if (entity.hasComponentOfType(RangedMobTag.class)) {
                attackRanged(entity, aiComponent, transformationComponent);
            } else {
                attack(entity, aiComponent, transformationComponent);
            }
            aiComponent.setAttackedLast(System.currentTimeMillis());
        }
    }

    private static void attack(Entity entity, AIComponent aiComponent, TransformationComponent transformationComponent) {
        if (CollisionUtil.hasLineOfSight(entity.getComponentOfType(TransformationComponent.class).getPosition(),
                aiComponent.getCurrentTarget().getComponentOfType(TransformationComponent.class).getPosition(), 10)) {
            AttackComponent attackComponent = AttackUtil.getAdjustedAttackComponent(Slot.PRIMARY, entity.getComponentOfType(StatComponent.class), "meleeAttack");
            attackComponent.setTargetComponentConstraint(PlayerTag.class);
            EntityBuilder.builder()
                    .withComponent(attackComponent)
                    .at(transformationComponent.getPosition().x(), transformationComponent.getPosition().y())
                    .buildAndInstantiate(EntityKeyConstants.ATTACK_PREFIX + RandomStringUtils.randomAlphanumeric(6));
        } else if (!aiComponent.getPathToTarget()
                .isEmpty()) {
            aiComponent.setCurrentState(PATHING);
        } else {
            aiComponent.setCurrentState(IDLE);
        }
    }

    private static void attackRanged(Entity entity, AIComponent aiComponent, TransformationComponent transformationComponent) {
        if (CollisionUtil.hasLineOfSight(entity.getComponentOfType(TransformationComponent.class).getPosition(),
                aiComponent.getCurrentTarget().getComponentOfType(TransformationComponent.class).getPosition(), 10)) {
            Vector2d direction = aiComponent.getCurrentTarget().getComponentOfType(TransformationComponent.class).getPosition().sub(transformationComponent.getPosition()).normalize();
            StatComponent statComponent = entity.getComponentOfType(StatComponent.class);
            Entity projectileEntity = EntityBuilder.builder()
                    .fromTemplate("projectileAlien")
                    .at(transformationComponent.getPosition().x(), transformationComponent.getPosition().y())
                    .buildAndInstantiate(EntityKeyConstants.PROJECTILE_PREFIX + RandomStringUtils.randomAlphanumeric(6));
            ProjectileComponent projectileComponent = projectileEntity.getComponentOfType(ProjectileComponent.class);
            AttackComponent attackComponent = AttackUtil.getAdjustedAttackComponent(Slot.PRIMARY,
                    statComponent,
                    projectileComponent.getOnCollisionAttack());
            projectileComponent.setAttackComponent(attackComponent);
            ProjectileUtil.adjustProjectileWithStats(Slot.PRIMARY,
                    projectileComponent,
                    statComponent);
            projectileEntity.addComponent(new CreatedByComponent(entity));
            projectileEntity.getComponentOfType(ProjectileComponent.class)
                    .setDirection(direction);
            projectileEntity.getComponentOfType(RenderComponent.class).setTextureRotation(new Vector2d(1.0, 0.0).angle(direction) * 180 / 3.14159265359);
        } else if (aiComponent.getPathToTarget() != null
                && !aiComponent.getPathToTarget().isEmpty()) {
            aiComponent.setCurrentState(PATHING);
        } else {
            aiComponent.setCurrentState(IDLE);
        }
    }

    private static void pathing(AIComponent aiComponent, TransformationComponent transformationComponent, PhysicsComponent physicsComponent) {
        List<Vector2i> currentPath = aiComponent.getPathToTarget();
        if (currentPath != null && !currentPath.isEmpty()) {
            Vector2i currentWaypointTarget = currentPath.get(0);
            Vector2d currentWaypointTargetD = new Vector2d(currentWaypointTarget.x(), currentWaypointTarget.y());
            if (transformationComponent.getPosition().distance(currentWaypointTargetD) < 0.05) {
                currentPath.remove(0);
                physicsComponent.setMoveToTarget(null);
            } else if (physicsComponent.getMoveToTarget() == null || !physicsComponent.getMoveToTarget().equals(currentWaypointTargetD)) {
                physicsComponent.setMoveToTarget(currentWaypointTargetD);
            }
        } else {
            aiComponent.setCurrentState(IDLE);
            aiComponent.setPathToTarget(null);
        }
    }

    private static void checkAggro(Entity entity, AIComponent aiComponent, TransformationComponent transformationComponent) {
        World world = EntityHandler.getInstance().getWorld();
        List<Entity> targets = getAliveTargets();
        Vector2d currentPosition = transformationComponent.getPosition();
        //Optional<Entity> nearestTarget = getNearestTarget(targets, currentPosition);
        Optional<Entity> player = Optional.ofNullable(EntityHandler.getInstance().getEntityWithComponent(PlayerTag.class));
        if (player.isPresent() && !player.get().getComponentOfType(StatComponent.class).isDead()) {
            Vector2d targetPosition = player.get().getComponentOfType(TransformationComponent.class).getPosition();
            //if (CollisionUtil.hasLineOfSight(currentPosition, targetPosition, 10)) {
            if (entity.hasComponentOfType(RangedMobTag.class) && currentPosition.distance(targetPosition) < 5.0) {
                aiComponent.setCurrentState(ATTACKING);
                aiComponent.setCurrentTarget(player.get());
            } else if (entity.hasComponentOfType(MobTag.class) && currentPosition.distance(targetPosition) < 1.0) {
                aiComponent.setCurrentState(ATTACKING);
                aiComponent.setCurrentTarget(player.get());
            } else if (aiComponent.getCurrentState().equals(PATHING)
                    && aiComponent.getPathToTarget() != null
                    && !aiComponent.getPathToTarget().isEmpty()
                    && targetDistanceDeltaSmallerThan(aiComponent, targetPosition, 2)) {
                aiComponent.setCurrentState(PATHING);
            } else {
                aiComponent.setCurrentState(PATHING);
                List<Vector2i> pathToTarget = new Pathfinding().getPath(world, currentPosition, targetPosition);
                aiComponent.setPathToTarget(pathToTarget);
            }
            //}
        }
    }

    private static Optional<Entity> getNearestTarget(List<Entity> targets, Vector2d currentPosition) {
        return targets.stream().min(Comparator.comparingDouble(targetA -> targetA.getComponentOfType(TransformationComponent.class).getPosition().distance(currentPosition)));
    }

    private static List<Entity> getAliveTargets() {
        List<Entity> targets = EntityHandler.getInstance().getAllEntitiesWithComponents(AiTargetTag.class, StatComponent.class);
        targets = targets.stream().filter(target -> !target.getComponentOfType(StatComponent.class).isDead()).collect(Collectors.toList());
        return targets;
    }

    private static boolean targetDistanceDeltaSmallerThan(AIComponent aiComponent, Vector2d playerPos, int distance) {
        Vector2i targetI = aiComponent.getPathToTarget().get(aiComponent.getPathToTarget().size() - 1);
        Vector2dc targetDc = new Vector2d(targetI.x(), targetI.y());
        return playerPos.distance(targetDc) < distance;
    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(AIComponent.class)
                && entity.hasComponentOfType(PhysicsComponent.class);
    }
}
