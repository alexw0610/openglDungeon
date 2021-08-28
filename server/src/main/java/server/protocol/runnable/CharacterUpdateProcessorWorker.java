package server.protocol.runnable;

import protocol.dto.update.PlayerUpdateDto;
import server.repository.CharacterRepository;
import server.repository.DatabaseConnection;

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
