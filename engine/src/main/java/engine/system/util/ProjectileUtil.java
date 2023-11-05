package engine.system.util;

import engine.component.*;
import engine.entity.ComponentBuilder;
import engine.enums.BulletModifier;
import engine.enums.Slot;

public class ProjectileUtil {

    public static void adjustProjectileWithStats(Slot slot, ProjectileComponent projectileComponent, StatComponent statComponent, GunComponent gunComponent) {
        adjustProjectileWithStats(slot, projectileComponent, gunComponent.getPrimaryBaseBulletSpeed() * statComponent.getBulletSpeedPrimary(), gunComponent.getSecondaryBaseBulletSpeed() * statComponent.getBulletSpeedSecondary());
    }

    public static void adjustProjectileWithStats(Slot slot, ProjectileComponent projectileComponent, StatComponent statComponent) {
        adjustProjectileWithStats(slot, projectileComponent, statComponent.getBulletSpeedPrimary(), statComponent.getBulletSpeedSecondary());
    }

    private static void adjustProjectileWithStats(Slot slot, ProjectileComponent projectileComponent, double bulletSpeedPrimary, double bulletSpeedSecondary) {
        if (Slot.PRIMARY.equals(slot)) {
            projectileComponent.setSpeed(bulletSpeedPrimary);
        } else if (Slot.SECONDARY.equals(slot)) {
            projectileComponent.setSpeed(bulletSpeedSecondary);
        }
    }

    public static void addActiveBulletModifiers(Slot slot, GunComponent gunComponent, AttackComponent attackComponent) {
        if (Slot.PRIMARY.equals(slot)) {
            addIfActive(gunComponent.getPrimaryModifierSlotA(), attackComponent);
            addIfActive(gunComponent.getPrimaryModifierSlotB(), attackComponent);
        } else if (Slot.SECONDARY.equals(slot)) {
            addIfActive(gunComponent.getSecondaryModifierSlotA(), attackComponent);
            addIfActive(gunComponent.getSecondaryModifierSlotB(), attackComponent);
        }
    }

    private static void addIfActive(UpgradeComponent upgradeComponent, AttackComponent attackComponent) {
        if (upgradeComponent != null) {
            attackComponent.addBulletModifierToList(BulletModifier.valueOf(upgradeComponent.getUpgradeType().toUpperCase()));
        }
    }
}
