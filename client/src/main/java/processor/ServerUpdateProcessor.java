package processor;

import dto.udp.CharacterListUpdateDto;
import dto.udp.UpdateEncryptionWrapper;
import engine.enums.PrimitiveMeshShape;
import engine.enums.ShaderType;
import engine.handler.SceneHandler;
import engine.object.Character;
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
        if (SceneHandler.getInstance().containsCharacter(String.valueOf(characterUpdateDto.getCharacterId()))) {
            Character character = SceneHandler.getInstance().getCharacter(String.valueOf(characterUpdateDto.getCharacterId()));
            character.setPositionX(characterUpdateDto.getPositionX());
            character.setPositionY(characterUpdateDto.getPositionY());
        } else {
            Character character = new Character(PrimitiveMeshShape.QUAD, ShaderType.DEFAULT);
            SceneHandler.getInstance().addCharacter(String.valueOf(characterUpdateDto.getCharacterId()), character);
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
