package server.protocol.runnable;

import protocol.dto.update.PlayerUpdateDto;
import server.DatabaseConnection;
import server.repository.CharacterRepository;

public class CharacterUpdateWorker implements Runnable {

    private final DatabaseConnection connection;
    private final PlayerUpdateDto playerUpdateDto;

    public CharacterUpdateWorker(DatabaseConnection connection, PlayerUpdateDto playerUpdateDto) {
        this.connection = connection;
        this.playerUpdateDto = playerUpdateDto;
    }

    @Override
    public void run() {
        if (!CharacterRepository.updateCharacterLocation(connection, playerUpdateDto)) {
            System.err.println("Error while updating character location! " + this.playerUpdateDto.toString());
        }
    }
}
