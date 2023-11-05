package engine.service;

import engine.component.GunComponent;
import engine.component.StatComponent;
import engine.component.UpgradeComponent;
import engine.component.base.RenderComponent;
import engine.component.base.TransformationComponent;
import engine.component.tag.PlayerTag;
import engine.entity.ComponentBuilder;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.handler.EntityHandler;

import static engine.EntityKeyConstants.GUN_ENTITY_KEY;
import static engine.EntityKeyConstants.PLAYER_ENTITY_KEY;

public class GameStateService {
    public static void initPlayer() {
        EntityHandler.getInstance().removeObject(PLAYER_ENTITY_KEY);
        Entity player = EntityBuilder
                .builder()
                .fromTemplate("player")
                .at(0, 0)
                .build();
        EntityHandler.getInstance().addObject(PLAYER_ENTITY_KEY, player);
        EntityHandler.getInstance().removeObject(GUN_ENTITY_KEY);
        Entity gun = EntityBuilder
                .builder()
                .fromTemplate("gun")
                .at(0, 0)
                .build();
        gun.addComponent(player.getComponentOfType(TransformationComponent.class));
        EntityHandler.getInstance().addObject(GUN_ENTITY_KEY, gun);
        Entity gunItem = EntityBuilder.builder()
                .fromTemplate("item")
                .at(4.5, 7)
                .buildAndInstantiate();
        gunItem.addComponent(ComponentBuilder.fromTemplate("gunStandardIssueBlaster"));
        gunItem.getComponentOfType(RenderComponent.class)
                .setTextureKey(gunItem
                        .getComponentOfType(GunComponent.class)
                        .getGunSprite());

        WorldSceneService.loadSafeZone();
        UISceneService.getInstance().showOutOfCombatUI();
    }

    public static void initTestMode(){
        Entity player = EntityHandler.getInstance().getEntityWithComponent(PlayerTag.class);
        StatComponent statComponent = player.getComponentOfType(StatComponent.class);
        statComponent.addUpgrade((UpgradeComponent) ComponentBuilder.fromTemplate("upgradeFreezingBullet"));
        statComponent.addUpgrade((UpgradeComponent) ComponentBuilder.fromTemplate("upgradeDeepFreezeBullet"));
        statComponent.addUpgrade((UpgradeComponent) ComponentBuilder.fromTemplate("upgradeCorrosiveBullet"));
        statComponent.addUpgrade((UpgradeComponent) ComponentBuilder.fromTemplate("upgradeExecutionBullet"));
        statComponent.addUpgrade((UpgradeComponent) ComponentBuilder.fromTemplate("upgradeExplosiveBullet"));
        statComponent.addUpgrade((UpgradeComponent) ComponentBuilder.fromTemplate("upgradeImpactBullet"));
        statComponent.addUpgrade((UpgradeComponent) ComponentBuilder.fromTemplate("upgradeHighVelocityBullet"));
        statComponent.addUpgrade((UpgradeComponent) ComponentBuilder.fromTemplate("upgradeIncendiaryBullet"));
        statComponent.addUpgrade((UpgradeComponent) ComponentBuilder.fromTemplate("upgradeLifestealBullet"));
        statComponent.addUpgrade((UpgradeComponent) ComponentBuilder.fromTemplate("upgradeHeatSeekingBullet"));
        statComponent.addUpgrade((UpgradeComponent) ComponentBuilder.fromTemplate("upgradeBlastWave"));
        statComponent.addUpgrade((UpgradeComponent) ComponentBuilder.fromTemplate("upgradeTripleShot"));
        statComponent.addGuns((GunComponent) ComponentBuilder.fromTemplate("gunAdvancedScoutBlaster"));
        statComponent.addGuns((GunComponent) ComponentBuilder.fromTemplate("gunHeavyMarineBlaster"));
        statComponent.addGuns((GunComponent) ComponentBuilder.fromTemplate("gunPreciseReconBlaster"));
        statComponent.setMaxHealthPoints(100.0);
    }
}
