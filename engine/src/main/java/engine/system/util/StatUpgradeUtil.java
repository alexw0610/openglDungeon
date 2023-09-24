package engine.system.util;

import engine.component.StatComponent;
import engine.component.UpgradeComponent;
import engine.enums.UpgradeType;

public class StatUpgradeUtil {

    public static void handleStatUpgrade(UpgradeComponent upgradeComponent, StatComponent statComponent) {
        if (UpgradeType.MAX_HEALTH.getKey().equals(upgradeComponent.getUpgradeType())) {
            statComponent.setMaxHealthPoints(statComponent.getMaxHealthPoints() + upgradeComponent.getModifierValue());
        } else if (UpgradeType.ATTACK_SPEED.getKey().equals(upgradeComponent.getUpgradeType())) {
            handleAttackSpeedUpgrade(upgradeComponent, statComponent);
        } else if (UpgradeType.ATTACK_DAMAGE.getKey().equals(upgradeComponent.getUpgradeType())) {
            handleAttackDamageUpgrade(upgradeComponent, statComponent);
        } else if (UpgradeType.BULLET_VELOCITY.getKey().equals(upgradeComponent.getUpgradeType())) {
            handleBulletVelocityUpgrade(upgradeComponent, statComponent);
        } else if (UpgradeType.MOVEMENT_SPEED.getKey().equals(upgradeComponent.getUpgradeType())) {
            statComponent.setMovementSpeed(statComponent.getMovementSpeed() + upgradeComponent.getModifierValue());
        } else if (UpgradeType.MAX_SHIELD.getKey().equals(upgradeComponent.getUpgradeType())) {
            statComponent.setMaxShield(statComponent.getMaxShield() + upgradeComponent.getModifierValue());
        } else if (UpgradeType.DASH_COOLDOWN.getKey().equals(upgradeComponent.getUpgradeType())) {
            statComponent.setDashCooldownSpeed(statComponent.getDashCooldownSpeed() - (statComponent.getDashCooldownSpeed() * upgradeComponent.getModifierValue()));
        } else if (UpgradeType.DASH_STUN.getKey().equals(upgradeComponent.getUpgradeType())) {
            statComponent.setDashStunDuration(statComponent.getDashStunDuration() + upgradeComponent.getModifierValue());
        }
    }

    private static void handleBulletVelocityUpgrade(UpgradeComponent upgradeComponent, StatComponent statComponent) {
        if (upgradeComponent.getUpgradeSlot().equals("primary")) {
            statComponent.setBulletSpeedPrimary(statComponent.getBulletSpeedPrimary() * upgradeComponent.getModifierValue());
        } else {
            statComponent.setBulletSpeedSecondary(statComponent.getBulletSpeedSecondary() * upgradeComponent.getModifierValue());
        }
    }

    private static void handleAttackDamageUpgrade(UpgradeComponent upgradeComponent, StatComponent statComponent) {
        if (upgradeComponent.getUpgradeSlot().equals("primary")) {
            statComponent.setBaseBulletDamagePrimary(statComponent.getBaseBulletDamagePrimary() * upgradeComponent.getModifierValue());
        } else {
            statComponent.setBaseBulletDamageSecondary(statComponent.getBaseBulletDamageSecondary() * upgradeComponent.getModifierValue());
        }
    }

    private static void handleAttackSpeedUpgrade(UpgradeComponent upgradeComponent, StatComponent statComponent) {
        if (upgradeComponent.getUpgradeSlot().equals("primary")) {
            statComponent.setAttackSpeedPrimary(statComponent.getAttackSpeedPrimary() - upgradeComponent.getModifierValue());
        } else {
            statComponent.setAttackSpeedSecondary(statComponent.getAttackSpeedSecondary() - upgradeComponent.getModifierValue());
        }
    }
}
