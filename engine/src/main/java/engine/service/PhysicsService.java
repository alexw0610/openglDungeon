package engine.service;

import engine.EngineConstants;
import engine.handler.SceneHandler;
import engine.object.Player;

public class PhysicsService {

    private static final double INERTIA = 500.0;
    private static final double DECAY = 0.75;

    public void doPhysics(long delta) {
        applyPlayerMomentum(SceneHandler.getInstance().getPlayer(), delta * EngineConstants.FRAME_DELTA_FACTOR);
    }

    private void applyPlayerMomentum(Player player, double delta) {
        if (player == null) {
            return;
        }
        double x = player.getMomentumX() * INERTIA * delta;
        player.moveX(x);
        double y = player.getMomentumY() * INERTIA * delta;
        player.moveY(y);
        decayMomentum(player, delta);
    }

    private void decayMomentum(Player player, double delta) {
        player.setMomentumX(decay(player.getMomentumX(), delta));
        player.setMomentumY(decay(player.getMomentumY(), delta));
    }

    private double decay(double momentum, double delta) {
        if (Math.abs(momentum) > 0.000000001) {
            return momentum * DECAY;
        } else {
            return 0;
        }
    }

}
