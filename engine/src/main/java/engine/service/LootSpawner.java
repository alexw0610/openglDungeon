package engine.service;

import engine.EntityKeyConstants;
import engine.component.UpgradeComponent;
import engine.component.base.RenderComponent;
import engine.component.tag.LootChoiceTag;
import engine.entity.ComponentBuilder;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.handler.EntityHandler;
import engine.handler.template.ComponentTemplateHandler;
import engine.loader.template.ComponentTemplate;

import java.util.List;
import java.util.stream.Collectors;

public class LootSpawner {

    public static void spawnSafeZoneLoot() {
        spawnLootOptions(10.0, 1.5, 3, 3, 0.85, 0.65);
    }
    public static void spawnBossLoot() {
        spawnLootOptions(8, 6, 2, 3, 0.50, 0.25);
    }

    public static void spawnGunOption() {
    }

    private static void spawnLootOptions(double positionY, double positionX, int spacingX, int lootChoices, double epicChance, double rareChance) {
        List<ComponentTemplate> availableUpgrades =
                ComponentTemplateHandler.getInstance()
                        .getAllObjects()
                        .stream()
                        .filter(template -> template.getType().equals("UpgradeComponent"))
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
        for (int i = 0; i < lootChoices; i++) {
            Entity item = EntityBuilder.builder()
                    .fromTemplate("item")
                    .at(positionX + (i * spacingX), positionY)
                    .build();
            if (Math.random() > epicChance && epicUpgrades.size() > 0) {
                addUpgradeComponent(item, epicUpgrades);
            } else if (Math.random() > rareChance && rareUpgrades.size() > 0) {
                addUpgradeComponent(item, rareUpgrades);
            } else if (commonUpgrades.size() > 0) {
                addUpgradeComponent(item, commonUpgrades);
            }
            item.addComponent(new LootChoiceTag());
            EntityHandler.getInstance().addObject(EntityKeyConstants.LOOT_CHOICE_PREFIX + i + "_", item);
        }
    }

    public static void spawnPortal() {
        Entity portal = EntityBuilder.builder()
                .fromTemplate("portal")
                .at(4.5, 13)
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
