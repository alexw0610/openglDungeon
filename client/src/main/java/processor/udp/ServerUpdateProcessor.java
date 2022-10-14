package processor.udp;

import dto.udp.CharacterListUpdateDto;
import dto.udp.UpdateEncryptionWrapper;
import engine.Engine;
import engine.component.AiTargetTag;
import engine.component.PhysicsComponent;
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

import java.util.*;
import java.util.stream.Collectors;

public class ServerUpdateProcessor implements Runnable {
    public static final double SMOOTHING = 0.34;
    private final Queue<UpdateEncryptionWrapper> updatesToProcess;
    private final byte[] encryptionKey;
    private final int connectionId;
    private final Map<Integer, Long> channelMap = new HashMap<>();
    private final Engine engine;

    public ServerUpdateProcessor(Queue<UpdateEncryptionWrapper> updatesToProcess, byte[] encryptionKey, int connectionId, Engine engine) {
        this.engine = engine;
        this.updatesToProcess = updatesToProcess;
        this.encryptionKey = encryptionKey;
        this.connectionId = connectionId;
    }

    @Override
    public void run() {
        while (true) {
            UpdateEncryptionWrapper update = this.updatesToProcess.poll();
            if (update != null && this.connectionId == update.getConnectionId()) {
                if (!channelMap.containsKey(update.getChannel())) {
                    this.channelMap.put(update.getChannel(), 0L);
                }
                if (channelMap.get(update.getChannel()) < update.getSequenceId()) {
                    CharacterListUpdateDto characterListUpdateDto = null;
                    try {
                        characterListUpdateDto = decryptPayload(update);
                    } catch (EncryptionException e) {
                        System.err.println(e.getMessage());
                    }
                    for (CharacterListUpdateDto.CharacterUpdateDto characterUpdateDto : characterListUpdateDto.getCharacterUpdateDtos()) {
                        addOrUpdateCharacter(characterUpdateDto);
                    }
                    List<Entity> players = EntityHandler.getInstance().getObjectsWithPrefix("PLAYER_");
                    CharacterListUpdateDto finalCharacterListUpdateDto = characterListUpdateDto;
                    players.stream()
                            .filter(player -> !finalCharacterListUpdateDto.getCharacterUpdateDtos()
                                    .stream()
                                    .map(CharacterListUpdateDto.CharacterUpdateDto::getCharacterId)
                                    .collect(Collectors.toList()).contains(Integer.parseInt(player.getEntityId().replaceFirst("PLAYER_", ""))))
                            .forEach(player -> EntityHandler.getInstance().removeObject(player.getEntityId()));
                }
            }
        }
    }


    private void addOrUpdateCharacter(CharacterListUpdateDto.CharacterUpdateDto characterUpdateDto) {
        if (engine.getEntityHandler().getObject(String.valueOf(characterUpdateDto.getCharacterId())) == null) {
            Entity entity = EntityBuilder.builder()
                    .fromTemplate("humanoid")
                    .at(characterUpdateDto.getPositionX(), characterUpdateDto.getPositionY())
                    .withComponent(new RenderComponent(PrimitiveMeshShape.QUAD.value(), "human_female", ShaderType.DEFAULT.value(), 1.0, 4))
                    .withComponent(new AiTargetTag())
                    .build();
            engine.getEntityHandler().addObject("PLAYER_" + characterUpdateDto.getCharacterId(), entity);
        } else {
            Entity character = engine.getEntityHandler().getObject(String.valueOf(characterUpdateDto.getCharacterId()));
            double deltaX = characterUpdateDto.getPositionX() - character.getComponentOfType(TransformationComponent.class).getPositionX();
            double deltaY = characterUpdateDto.getPositionY() - character.getComponentOfType(TransformationComponent.class).getPositionY();
            character.getComponentOfType(PhysicsComponent.class).setMomentumX(deltaX * SMOOTHING);
            character.getComponentOfType(PhysicsComponent.class).setMomentumY(deltaY * SMOOTHING);
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
