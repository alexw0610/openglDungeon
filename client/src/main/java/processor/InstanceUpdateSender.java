package processor;

import dto.tcp.InventoryItem;
import dto.tcp.InventoryUpdateDto;
import dto.tcp.TcpEncryptionWrapper;
import engine.Engine;
import engine.component.Component;
import engine.component.ItemComponent;
import engine.enums.EventType;
import engine.object.Event;
import security.EncryptionHandler;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class InstanceUpdateSender implements Runnable {
    private final EncryptionHandler encryptionHandler;
    private final ObjectOutputStream objectOutputStream;
    private final Engine engine;
    private final int connectionId;

    public InstanceUpdateSender(EncryptionHandler encryptionHandler, ObjectOutputStream objectOutputStream, Engine engine, int connectionId) {
        this.encryptionHandler = encryptionHandler;
        this.objectOutputStream = objectOutputStream;
        this.engine = engine;
        this.connectionId = connectionId;
    }

    @Override
    public void run() {
        while (true) {
            Event<? extends Component> event = engine.getEventHandler().pollEventForType(EventType.INVENTORY_ITEM_MOVED);
            if (event != null) {
                try {
                    objectOutputStream.writeObject(TcpEncryptionWrapper.builder()
                            .encryptedPayload(encryptionHandler.encryptSerializable(InventoryUpdateDto.builder()
                                    .targetEntityId(event.getTargetEntity())
                                    .sourceEntityId(event.getSourceEntity())
                                    .inventoryItem(
                                            InventoryItem.builder()
                                                    .itemId(((ItemComponent) event.getComponent()).getItemId())
                                                    .itemTypeId(((ItemComponent) event.getComponent()).getItemTypeId())
                                                    .build())
                                    .build()
                            ))
                            .connectionId(connectionId)
                            .objectType(InventoryUpdateDto.class)
                            .build());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }

        }
    }

}
