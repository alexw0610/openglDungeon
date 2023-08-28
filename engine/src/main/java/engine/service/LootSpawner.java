package engine.service;

import engine.component.base.RenderComponent;
import engine.component.tag.StartLevelTag;
import engine.entity.ComponentBuilder;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.handler.EntityHandler;
import engine.handler.template.ComponentTemplateHandler;
import engine.loader.template.ComponentTemplate;

import java.util.List;
import java.util.stream.Collectors;

public class LootSpawner {


    public static void spawnLootOptions() {
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
        for (int i = 0; i < 3; i++) {
            Entity item = EntityBuilder.builder()
                    .fromTemplate("item")
                    .at(2.5 + (i * 2), 13)
                    .build();
            if (Math.random() > 0.50 && epicUpgrades.size() > 0) {
                addUpgradeComponent(item, "upgrade_epic", epicUpgrades);
            } else if (Math.random() > 0.75 && rareUpgrades.size() > 0) {
                addUpgradeComponent(item, "upgrade_rare", rareUpgrades);
            } else if (commonUpgrades.size() > 0) {
                addUpgradeComponent(item, "upgrade_common", commonUpgrades);
            }
            item.addComponent(new StartLevelTag());
            EntityHandler.getInstance().addObject("PICKUP_" + i + "_", item);
        }
    }

    private static void addUpgradeComponent(Entity item, String textureKey, List<ComponentTemplate> upgrades) {
        item.getComponentOfType(RenderComponent.class).setTextureKey(textureKey);
        int upgradeId = (int) Math.floor(Math.random() * upgrades.size());
        item.addComponent(ComponentBuilder.fromTemplate(upgrades.get(upgradeId).getTemplateName()));
        upgrades.remove(upgradeId);
    }

    public static void spawnBossLoot() {
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
        for (int i = 0; i < 3; i++) {
            Entity item = EntityBuilder.builder()
                    .fromTemplate("item")
                    .at(6 + (i * 2), 8)
                    .build();
            if (Math.random() > 0.50 && epicUpgrades.size() > 0) {
                addUpgradeComponent(item, "upgrade_epic", epicUpgrades);
            } else if (Math.random() > 0.25 && rareUpgrades.size() > 0) {
                addUpgradeComponent(item, "upgrade_rare", rareUpgrades);
            } else if (commonUpgrades.size() > 0) {
                addUpgradeComponent(item, "upgrade_common", commonUpgrades);
            }
            item.addComponent(new StartLevelTag());
            EntityHandler.getInstance().addObject("PICKUP_" + i + "_", item);
        }
    }

    public static void clearLoot() {
        EntityHandler.getInstance().removeObjectsWithPrefix("PICKUP_");
    }
}
