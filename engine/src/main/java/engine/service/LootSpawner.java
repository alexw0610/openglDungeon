package engine.service;

import engine.EntityKeyConstants;
import engine.component.GunComponent;
import engine.component.StatComponent;
import engine.component.UpgradeComponent;
import engine.component.base.RenderComponent;
import engine.component.tag.LootChoiceTag;
import engine.component.tag.PlayerTag;
import engine.entity.ComponentBuilder;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.handler.EntityHandler;
import engine.handler.template.ComponentTemplateHandler;
import engine.loader.template.ComponentTemplate;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class LootSpawner {

    public static void spawnSafeZoneLoot() {
        spawnLootOptions(10.0, 1.5, 3, 3, 0.95,0.75, 0.50);
    }

    public static void spawnBossLoot() {
        int playerLevel = EntityHandler.getInstance()
                .getEntityWithComponent(PlayerTag.class)
                .getComponentOfType(StatComponent.class)
                .getLevel();
        String gunKey;
        switch (playerLevel) {
            case 5:
                gunKey = "gunAdvancedScoutBlaster";
                break;
            case 10:
                gunKey = "gunHeavyMarineBlaster";
                break;
            case 15:
                gunKey = "gunPreciseReconBlaster";
                break;
            default:
                spawnLootOptions(16, 14.5, 2, 3, 0.85, 0.50, 0.25);
                return;
        }
        Entity gunItem = getGunItemWithKey(gunKey);
        EntityHandler.getInstance()
                .addObject(EntityKeyConstants.ITEM_ENTITY_PREFIX + RandomStringUtils.randomAlphanumeric(6), gunItem);
    }

    private static Entity getGunItemWithKey(String gunKey) {
        Entity gunItem = EntityBuilder.builder()
                .fromTemplate("item")
                .at(16.5, 14)
                .build();
        gunItem.addComponent(ComponentBuilder.fromTemplate(gunKey));
        gunItem.getComponentOfType(RenderComponent.class)
                .setTextureKey(gunItem
                        .getComponentOfType(GunComponent.class)
                        .getGunSprite());
        return gunItem;
    }

    private static void spawnLootOptions(double positionY, double positionX, int spacingX, int lootChoices, double legendaryChance, double epicChance, double rareChance) {
        int level = EntityHandler.getInstance()
                .getEntityWithComponent(PlayerTag.class)
                .getComponentOfType(StatComponent.class)
                .getLevel();
        List<ComponentTemplate> availableUpgrades =
                ComponentTemplateHandler.getInstance()
                        .getAllObjects()
                        .stream()
                        .filter(template -> template.getType().equals("UpgradeComponent")
                                && ((Integer) template.getModifiers().get("minSpawnLevel")).compareTo(level) <= 0)
                        .collect(Collectors.toList());
        List<ComponentTemplate> commonUpgrades = availableUpgrades
                .stream()
                .filter(template -> template.getModifiers().get("upgradeRarity").equals("Common"))
                .collect(Collectors.toList());
        List<ComponentTemplate> rareUpgrades = availableUpgrades
                .stream()
                .filter(template -> template.getModifiers().get("upgradeRarity").equals("Rare"))
                .collect(Collectors.toList());
        List<ComponentTemplate> epicUpgrades = availableUpgrades
                .stream()
                .filter(template -> template.getModifiers().get("upgradeRarity").equals("Epic"))
                .collect(Collectors.toList());
        List<ComponentTemplate> legendaryUpgrades = availableUpgrades
                .stream()
                .filter(template -> template.getModifiers().get("upgradeRarity").equals("Legendary"))
                .collect(Collectors.toList());
        for (int i = 0; i < lootChoices; i++) {
            Entity item = EntityBuilder.builder()
                    .fromTemplate("item")
                    .at(positionX + (i * spacingX), positionY)
                    .build();
            double rarityChance = Math.random();
            if (rarityChance > legendaryChance && legendaryUpgrades.size() > 0) {
                addUpgradeComponent(item, legendaryUpgrades);
            } else if (rarityChance > epicChance && epicUpgrades.size() > 0) {
                addUpgradeComponent(item, epicUpgrades);
            } else if (rarityChance > rareChance && rareUpgrades.size() > 0) {
                addUpgradeComponent(item, rareUpgrades);
            } else if (commonUpgrades.size() > 0) {
                addUpgradeComponent(item, commonUpgrades);
            }
            item.addComponent(new LootChoiceTag());
            EntityHandler.getInstance().addObject(EntityKeyConstants.LOOT_CHOICE_PREFIX + i + "_", item);
        }
    }

    public static void spawnPortal(double x, double y) {
        Entity portal = EntityBuilder.builder()
                .fromTemplate("portal")
                .at(x, y)
                .build();
        EntityHandler.getInstance().addObject(EntityKeyConstants.PORTAL_KEY, portal);
    }

    private static void addUpgradeComponent(Entity item, List<ComponentTemplate> upgrades) {
        int upgradeId = (int) Math.floor(Math.random() * upgrades.size());
        UpgradeComponent upgradeComponent = (UpgradeComponent) ComponentBuilder.fromTemplate(upgrades.get(upgradeId).getTemplateName());
        item.addComponent(upgradeComponent);
        item.getComponentOfType(RenderComponent.class).setTextureKey(upgradeComponent.getUpgradeIcon());
        upgrades.remove(upgradeId);
    }

}
