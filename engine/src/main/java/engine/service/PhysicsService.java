package engine.service;

import engine.handler.SceneHandler;
import engine.object.GameObject;
import engine.object.Player;
import engine.service.util.AxisAlignedBoundingBox;
import engine.service.util.CollisionUtil;
import org.joml.Vector2d;

import java.util.List;
import java.util.stream.Collectors;

import static engine.EngineConstants.DECAY;
import static engine.EngineConstants.INERTIA;


public class PhysicsService {
    public void doPhysics(long delta) {
        applyPlayerPhysics(delta);
    }

    private void applyPlayerPhysics(long delta) {
        Player player = SceneHandler.getInstance().getPlayer();
        if (player != null) {
            if (!checkCollisionAtNextPosition(player)) {
                applyPlayerMomentum(player);
            } else {
                zeroMomentum(player);
            }
        }
    }

    private boolean checkCollisionAtNextPosition(Player player) {
        Vector2d nextPosition = getNextPosition(player);
        List<GameObject> surfaces = SceneHandler.getInstance().getObjects().stream().filter(set -> set.object.isSurface()).map(set -> set.object).collect(Collectors.toList());
        List<GameObject> obstacles = SceneHandler.getInstance().getObjects().stream().filter(set -> set.object.isObstacle()).map(set -> set.object).collect(Collectors.toList());
        if (checkIfCollision(player, nextPosition, surfaces)) {
            return checkIfCollision(player, nextPosition, obstacles);
        } else {
            return true;
        }
    }

    private boolean checkIfCollision(Player player, Vector2d nextPosition, List<GameObject> objects) {
        boolean collision;
        for (GameObject object : objects) {
            collision = checkCollsion(player, nextPosition, object);
            if (collision) {
                return true;
            }
        }
        return false;
    }

    private boolean checkCollsion(Player player, Vector2d nextPosition, GameObject object) {
        switch (player.getHitbox().getHitboxType()) {
            case AABB:
                switch (object.getHitbox().getHitboxType()) {
                    case AABB:
                        return CollisionUtil.checkCollisionAABBWithAABB(getAABBForObject(nextPosition, player), getAABBForObject(object.getPosition(), object));
                    case CIRCLE:
                        return CollisionUtil.checkCollisionAABBWithCircle();
                }
            case CIRCLE:
                switch (object.getHitbox().getHitboxType()) {
                    case AABB:
                        return CollisionUtil.checkCollisionAABBWithCircle();
                    case CIRCLE:
                        return CollisionUtil.checkCollisionCircleWithCircle();
                }
        }
        return false;
    }

    private AxisAlignedBoundingBox getAABBForObject(Vector2d position, GameObject object) {
        return new AxisAlignedBoundingBox(
                position.x() - object.getHitbox().getSize(),
                position.x() + object.getHitbox().getSize(),
                position.y() - object.getHitbox().getSize(),
                position.y() + object.getHitbox().getSize());
    }

    private Vector2d getNextPosition(Player player) {
        double x = player.getMomentumX();
        double newPosX = player.getPosition().x() + (x * INERTIA * player.getCharacterStats().getMovementSpeed());
        double y = player.getMomentumY();
        double newPosY = player.getPosition().y() + (y * INERTIA * player.getCharacterStats().getMovementSpeed());
        return new Vector2d(newPosX, newPosY);
    }

    private void zeroMomentum(Player player) {
        player.setMomentumX(0);
        player.setMomentumY(0);
    }

    private void applyPlayerMomentum(Player player) {
        double x = player.getMomentumX();
        player.moveX(x * INERTIA);
        double y = player.getMomentumY();
        player.moveY(y * INERTIA);
        decayMomentum(player);
    }

    private void decayMomentum(Player player) {
        player.setMomentumX(decay(player.getMomentumX()));
        player.setMomentumY(decay(player.getMomentumY()));
    }

    private double decay(double momentum) {
        System.out.println(momentum);
        if (Math.abs(momentum) > 0.001) {
            return momentum * DECAY;
        } else {
            return 0;
        }
    }

}
