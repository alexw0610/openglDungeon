package engine.system.util;

import engine.component.AttackComponent;
import engine.component.GunComponent;
import engine.component.StatComponent;
import engine.entity.ComponentBuilder;
import engine.enums.Slot;

public class AttackUtil {

    public static AttackComponent getAdjustedAttackComponent(Slot slot, StatComponent statComponent, GunComponent gunComponent, String templateName) {
        return getAdjustedAttackComponent(slot, statComponent, statComponent.getBaseDamagePrimary() * gunComponent.getPrimaryBaseDamage(), statComponent.getBaseDamageSecondary() * gunComponent.getSecondaryBaseDamage(), templateName);
    }

    public static AttackComponent getAdjustedAttackComponent(Slot slot, StatComponent statComponent, String templateName) {
        return getAdjustedAttackComponent(slot, statComponent, statComponent.getBaseDamagePrimary(), statComponent.getBaseDamageSecondary(), templateName);
    }

    private static AttackComponent getAdjustedAttackComponent(Slot slot, StatComponent statComponent, double bulletDamagePrimary, double bulletDamageSecondary, String templateName) {
        AttackComponent attackComponent = (AttackComponent) ComponentBuilder.fromTemplate(templateName);
        if (Slot.PRIMARY.equals(slot)) {
            attackComponent.setDamage(bulletDamagePrimary);
        } else if (Slot.SECONDARY.equals(slot)) {
            attackComponent.setDamage(bulletDamageSecondary);
        }
        attackComponent.setCriticalHitChance(statComponent.getCritChance());
        attackComponent.setCriticalBonusModifier(statComponent.getCritBonusDamage());
        return attackComponent;
    }
}
