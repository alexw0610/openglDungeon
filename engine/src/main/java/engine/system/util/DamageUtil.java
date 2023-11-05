package engine.system.util;

import engine.EntityKeyConstants;
import engine.component.DamageTextComponent;
import engine.component.StatComponent;
import engine.component.base.AudioComponent;
import engine.component.base.TransformationComponent;
import engine.component.tag.PlayerTag;
import engine.component.tag.TerrainTag;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import org.apache.commons.lang3.RandomStringUtils;

public class DamageUtil {

    public static double applyDamage(Entity entity, double damage, double criticalHitChance, double criticalBonusModifier, boolean executeOnCrit) {
        StatComponent statComponent = entity.getComponentOfType(StatComponent.class);
        if (statComponent != null) {
            DamageTextComponent damageTextComponent;
            if (Math.random() <= criticalHitChance) {
                if (executeOnCrit && statComponent.getHealthPercentage() <= 0.2) {
                    damage = statComponent.getCurrentHealthPoints();
                } else {
                    damage = damage * (criticalBonusModifier + (Math.random() - 0.5));
                }
                damageTextComponent = new DamageTextComponent(damage);
                damageTextComponent.setCriticalHit(true);
            } else {
                damageTextComponent = new DamageTextComponent(damage);
            }
            if (entity.hasComponentOfType(PlayerTag.class)) {
                damageTextComponent.setPlayer(true);
            }
            if (statComponent.getMaxShield() > 0) {
                double leftOverDamage = Math.max(damage - statComponent.getCurrentShield(), 0.0);
                statComponent.subtractShieldPoints(damage);
                damage = leftOverDamage;
            }
            statComponent.subtractHealthPoints(damage);
            AudioComponent audioComponent = new AudioComponent();
            audioComponent.setAudioKey("hurt");
            audioComponent.setPlayOnce(true);
            entity.addComponent(audioComponent);
            if (!entity.hasComponentOfType(TerrainTag.class)) {
                EntityBuilder.builder()
                        .withComponent(damageTextComponent)
                        .withComponent(entity.getComponentOfType(TransformationComponent.class))
                        .buildAndInstantiate(EntityKeyConstants.DAMAGE_TEXT_PREFIX + RandomStringUtils.randomAlphanumeric(6));
            }
        }
        return damage;
    }
}
