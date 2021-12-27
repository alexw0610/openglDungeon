package processor;

import dto.udp.CharacterListUpdateDto;
import dto.udp.UpdateEncryptionWrapper;
import engine.component.RenderComponent;
import engine.component.TransformationComponent;
import engine.entity.Entity;
import engine.entity.EntityBuilder;
import engine.enums.PrimitiveMeshShape;
import engine.enums.ShaderType;
import engine.handler.EntityHandler;
import exception.EncryptionException;
import security.EncryptionHandler;
import util.SerializableUtil;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

public class ServerUpdateProcessor implements Runnable {
    private final BlockingQueue<UpdateEncryptionWrapper> updatesToProcess;
    private final byte[] encryptionKey;
    private final int connectionId;

    public ServerUpdateProcessor(BlockingQueue<UpdateEncryptionWrapper> updatesToProcess, byte[] encryptionKey, int connectionId) {
        this.updatesToProcess = updatesToProcess;
        this.encryptionKey = encryptionKey;
        this.connectionId = connectionId;
    }

    @Override
    public void run() {
        while (true) {
            try {
                UpdateEncryptionWrapper update = this.updatesToProcess.take();
                if (connectionId == update.getConnectionId()) {
                    CharacterListUpdateDto characterListUpdateDto = null;
                    try {
                        characterListUpdateDto = decryptPayload(update);
                    } catch (EncryptionException e) {
                        System.err.println(e.getMessage());
                    }
                    for (CharacterListUpdateDto.CharacterUpdateDto characterUpdateDto : characterListUpdateDto.getCharacterUpdateDtos()) {
                        addOrUpdateCharacter(characterUpdateDto);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void addOrUpdateCharacter(CharacterListUpdateDto.CharacterUpdateDto characterUpdateDto) {
        if (EntityHandler.getInstance().getObject(String.valueOf(characterUpdateDto.getCharacterId())) == null) {
            Entity entity = EntityBuilder.builder().fromTemplate("humanoid")
                    .withComponent(new RenderComponent(PrimitiveMeshShape.QUAD.value(), "human_female", ShaderType.DEFAULT.value(), 1.0, 4))
                    .build();
            EntityHandler.getInstance().addObject(String.valueOf(characterUpdateDto.getCharacterId()), entity);
            System.out.println("Added new character");
        } else {
            Entity character = EntityHandler.getInstance().getObject(String.valueOf(characterUpdateDto.getCharacterId()));
            character.getComponentOfType(TransformationComponent.class).setPositionX(characterUpdateDto.getPositionX());
            character.getComponentOfType(TransformationComponent.class).setPositionY(characterUpdateDto.getPositionY());
            System.out.println("Updated character");
        }
    }

    private CharacterListUpdateDto decryptPayload(UpdateEncryptionWrapper update) throws EncryptionException {
        byte[] payload = update.getEncryptedPayload();
        EncryptionHandler encryptionHandler = new EncryptionHandler(this.encryptionKey);
        payload = encryptionHandler.decryptByteArray(payload);
        int packetSizeUnpadded = EncryptionHandler.readHeaderToInt(payload);
        byte[] unpaddedPayload = Arrays.copyOfRange(payload, 2, packetSizeUnpadded + 2);
        return (CharacterListUpdateDto) SerializableUtil.fromByteArray(unpaddedPayload);
    }
}
