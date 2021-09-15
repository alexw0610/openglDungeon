package processor;

import dto.udp.CharacterListUpdateDto;
import dto.udp.UpdateEncryptionWrapper;
import engine.enumeration.PrimitiveMeshShape;
import engine.enumeration.ShaderType;
import engine.object.Character;
import exception.EncryptionException;
import security.EncryptionHandler;
import util.SerializableUtil;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

import static engine.handler.SceneHandler.SCENE_HANDLER;

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
        if (SCENE_HANDLER.containsCharacter(String.valueOf(characterUpdateDto.getCharacterId()))) {
            Character character = SCENE_HANDLER.getCharacter(String.valueOf(characterUpdateDto.getCharacterId()));
            character.setPositionX(characterUpdateDto.getPositionX());
            character.setPositionY(characterUpdateDto.getPositionY());
        } else {
            Character character = new Character(PrimitiveMeshShape.QUAD, ShaderType.DEFAULT);
            SCENE_HANDLER.addCharacter(String.valueOf(characterUpdateDto.getCharacterId()), character);
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
