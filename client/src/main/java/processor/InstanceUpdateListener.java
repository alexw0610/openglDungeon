package processor;

import dto.Request;
import dto.tcp.InventoryItem;
import dto.tcp.TcpEncryptionWrapper;
import dto.udp.PlayerUpdateDto;
import engine.Engine;
import engine.component.*;
import engine.entity.ComponentBuilder;
import engine.entity.Entity;
import engine.handler.template.ComponentTemplateHandler;
import exception.EncryptionException;
import org.joml.Vector2d;
import security.EncryptionHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class InstanceUpdateListener implements Runnable {
    private final EncryptionHandler encryptionHandler;
    private final ObjectInputStream objectInputStream;
    private final Engine engine;

    public InstanceUpdateListener(EncryptionHandler encryptionHandler, ObjectInputStream objectInputStream, Engine engine) {
        this.encryptionHandler = encryptionHandler;
        this.objectInputStream = objectInputStream;
        this.engine = engine;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Request request;
                while ((request = (Request) this.objectInputStream.readObject()) != null) {
                    Class<?> objectType = ((TcpEncryptionWrapper) request).getObjectType();
                    if (PlayerUpdateDto.class.equals(objectType)) {
                        PlayerUpdateDto playerUpdateDto = (PlayerUpdateDto) encryptionHandler.decryptByteArrayToObject(((TcpEncryptionWrapper) request).getEncryptedPayload());
                        InventoryComponent inventory = engine.getEntityHandler()
                                .getEntityWithComponent(PlayerTag.class)
                                .getComponentOfType(InventoryComponent.class);
                        if (inventory != null) {
                            List<InventoryItem> currentInventoryItems = inventory
                                    .getItems()
                                    .stream()
                                    .map(item -> InventoryItem.builder()
                                            .itemId(item.getItemId())
                                            .itemTypeId(item.getItemTypeId()).build()
                                    )
                                    .collect(Collectors.toList());
                            if (currentInventoryItems.hashCode() != playerUpdateDto.getInventoryItems().hashCode()) {
                                inventory.getItems().clear();
                                playerUpdateDto
                                        .getInventoryItems()
                                        .forEach(inventoryItem -> {
                                            ItemComponent item = (ItemComponent) ComponentBuilder.fromTemplate(ComponentTemplateHandler.getInstance().getNameForItemTypeId(inventoryItem.getItemTypeId()));
                                            item.setItemId(inventoryItem.getItemId());
                                            inventory.getItems().add(item);
                                        });
                            }
                        }
                        System.out.println("Local/Server player delta: " + new Vector2d(playerUpdateDto.getPositionX(), playerUpdateDto.getPositionY()).distance(engine.getEntityHandler().getEntityWithComponent(PlayerTag.class).getComponentOfType(TransformationComponent.class).getPosition()));
                        if (engine.getNavHandler().getNavMap().getSeed() != playerUpdateDto.getZoneId()) {
                            ZoneChangeComponent zoneChangeComponent = new ZoneChangeComponent((int) playerUpdateDto.getZoneId());
                            engine.getEntityHandler().getEntityWithComponent(PlayerTag.class).addComponent(zoneChangeComponent);
                            System.out.println("changed zone from " + engine.getNavHandler().getNavMap().getSeed() + " to " + playerUpdateDto.getZoneId());
                        }
                    } else if (List.class.equals(objectType)) {
                        Serializable retrieveObject = encryptionHandler.decryptByteArrayToObject(((TcpEncryptionWrapper) request).getEncryptedPayload());
                        if (retrieveObject instanceof List) {
                            List<Entity> synchronizedEntities = (List<Entity>) retrieveObject;
                            addOrUpdateSynchronizedEntities(synchronizedEntities);
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException | EncryptionException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    private void addOrUpdateSynchronizedEntities(List<Entity> synchronizedEntities) {
        for (Entity synchronizedEntity : synchronizedEntities) {
            if (engine.getEntityHandler().getObject(String.valueOf(synchronizedEntity.getEntityId())) == null) {
                System.out.println("Added synchronized entity!");
                engine.getEntityHandler().addObject(String.valueOf(synchronizedEntity.getEntityId()), synchronizedEntity);
            } else {
                Entity entity = engine.getEntityHandler().getObject(String.valueOf(synchronizedEntity.getEntityId()));
                if (entity.getComponentOfType(AIComponent.class) != null) {
                    entity.getComponentOfType(AIComponent.class).setPathToTarget(synchronizedEntity.getComponentOfType(AIComponent.class).getPathToTarget());
                    entity.getComponentOfType(AIComponent.class).setCurrentState(synchronizedEntity.getComponentOfType(AIComponent.class).getCurrentState());
                }
            }
        }
    }
}