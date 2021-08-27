import engine.enumeration.PrimitiveMeshShape;
import engine.enumeration.ShaderType;
import engine.handler.SceneHandler;
import engine.object.Character;
import protocol.dto.udp.UpdateEncryptionWrapper;
import protocol.dto.update.CharacterListUpdateDto;
import security.EncryptionHandler;
import util.SerializableUtil;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

public class ServerUpdateProcessor implements Runnable {
    private final BlockingQueue<UpdateEncryptionWrapper> updatesToProcess;
    private final byte[] encryptionKey;

    public ServerUpdateProcessor(BlockingQueue<UpdateEncryptionWrapper> updatesToProcess, byte[] encryptionKey) {
        this.updatesToProcess = updatesToProcess;
        this.encryptionKey = encryptionKey;
    }

    @Override
    public void run() {
        while (true) {
            try {
                UpdateEncryptionWrapper update = this.updatesToProcess.take();
                byte[] payload = update.getEncryptedPayload();
                EncryptionHandler encryptionHandler = new EncryptionHandler(this.encryptionKey);
                payload = encryptionHandler.decryptByteArray(payload);
                int packetSizeUnpadded = EncryptionHandler.readHeaderToInt(payload);
                byte[] unpaddedPayload = Arrays.copyOfRange(payload, 2, packetSizeUnpadded + 2);
                CharacterListUpdateDto characterListUpdateDto = (CharacterListUpdateDto) SerializableUtil.fromByteArray(unpaddedPayload);
                for (CharacterListUpdateDto.CharacterUpdateDto characterUpdateDto : characterListUpdateDto.getCharacterUpdateDtos()) {
                    if (SceneHandler.SCENE_HANDLER.getCharacter(String.valueOf(characterUpdateDto.getCharacterId())) == null) {
                        Character character = new Character(PrimitiveMeshShape.QUAD, ShaderType.DEFAULT);
                        SceneHandler.SCENE_HANDLER.addCharacter(String.valueOf(characterUpdateDto.getCharacterId()), character);
                        System.out.println("created character with id " + characterUpdateDto.getCharacterId());
                    } else {
                        Character character = SceneHandler.SCENE_HANDLER.getCharacter(String.valueOf(characterUpdateDto.getCharacterId()));
                        character.setPositionX(characterUpdateDto.getPositionX());
                        character.setPositionY(characterUpdateDto.getPositionY());
                        System.out.println("update character with id " + characterUpdateDto.getCharacterId());
                    }
                    System.out.println("processed one server character update for character id " + characterUpdateDto.getCharacterId());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
