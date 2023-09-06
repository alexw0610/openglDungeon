package engine.service.util;

import engine.component.GunComponent;
import engine.component.ProjectileComponent;
import engine.component.StatComponent;
import engine.enums.Slot;

public class BulletModifierUtil {

    public static void adjustProjectileComponentWithStats(Slot slot, ProjectileComponent projectileComponent, StatComponent statComponent, GunComponent gunComponent) {
        if (Slot.PRIMARY.equals(slot)) {
            projectileComponent.setDamageOverwrite(
                    gunComponent.getPrimaryBaseDamage() * statComponent.getBaseBulletDamagePrimary());
            projectileComponent.setSpeed(gunComponent.getPrimaryBulletSpeed() * statComponent.getBulletSpeedPrimary());
            System.out.println(projectileComponent.getDamageOverwrite());
        } else if (Slot.SECONDARY.equals(slot)) {
            projectileComponent.setDamageOverwrite(
                    gunComponent.getSecondaryBaseDamage() * statComponent.getBaseBulletDamageSecondary());
            projectileComponent.setSpeed(gunComponent.getSecondaryBulletSpeed() * statComponent.getBulletSpeedSecondary());
        }
    }
}
