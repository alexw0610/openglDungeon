package server.protocol.runnable;

import protocol.dto.update.CharacterListUpdateDto;
import server.DatabaseConnection;
import server.connection.SubscribedClient;
import server.repository.CharacterRepository;
import server.repository.dto.CharacterLocationDto;
import udp.UpdateSender;

import java.util.LinkedList;
import java.util.List;

public class CharacterUpdateSenderWorker implements Runnable {

    private final DatabaseConnection connection;
    private final SubscribedClient subscribedClient;
    private final int connectionId;

    public CharacterUpdateSenderWorker(DatabaseConnection connection, SubscribedClient subscribedClient) {
        this.connection = connection;
        this.subscribedClient = subscribedClient;
        this.connectionId = subscribedClient.getConnectionId();
    }

    @Override
    public void run() {
        UpdateSender sender = new UpdateSender(subscribedClient.getClientAddress(), Integer.parseInt(subscribedClient.getClientPort()), subscribedClient.getEncryptionKey());
        List<CharacterLocationDto> characterLocationDtoList = CharacterRepository.getCharacterLocationsForCharacterId(connection, subscribedClient.getCharacterId());
        List<CharacterListUpdateDto.CharacterUpdateDto> characterUpdateDtoList = new LinkedList<>();
        for (CharacterLocationDto characterLocationDto : characterLocationDtoList) {
            characterUpdateDtoList.add(CharacterListUpdateDto.CharacterUpdateDto.builder()
                    .characterId(characterLocationDto.getCharacterId())
                    .positionX(characterLocationDto.getPositionX())
                    .positionY(characterLocationDto.getPositionY())
                    .build());
        }
        CharacterListUpdateDto characterListUpdateDto = new CharacterListUpdateDto();
        characterListUpdateDto.setCharacterUpdateDtos(characterUpdateDtoList);
        sender.sendUpdate(characterListUpdateDto, connectionId);
        System.out.println("sent update to client");
    }
}
