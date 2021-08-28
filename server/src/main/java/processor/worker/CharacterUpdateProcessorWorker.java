package processor.worker;

import protocol.dto.udp.PlayerUpdateDto;
import repository.CharacterRepository;
import repository.DatabaseConnection;

public class CharacterUpdateProcessorWorker implements Runnable {

    private final DatabaseConnection connection;
    private final PlayerUpdateDto playerUpdateDto;

    public CharacterUpdateProcessorWorker(DatabaseConnection connection, PlayerUpdateDto playerUpdateDto) {
        this.connection = connection;
        this.playerUpdateDto = playerUpdateDto;
    }

    @Override
    public void run() {
        CharacterRepository.updateCharacterLocation(connection, playerUpdateDto);
    }
}
