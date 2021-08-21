package server.repository;

import protocol.dto.update.PlayerUpdateDto;
import server.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CharacterRepository {

    public static final String UPSERT_CHARACTER_LOCATION = "INSERT INTO CHARACTER_LOCATIONS " +
            "(character_id, character_pos_x, character_pos_y) " +
            "VALUES " +
            "(?, ?, ?)" +
            "ON CONFLICT (character_id) " +
            "DO UPDATE SET " +
            "character_pos_x = EXCLUDED.character_pos_x, " +
            "character_pos_y = EXCLUDED.character_pos_y;";


    public static boolean updateCharacterLocation(DatabaseConnection connection, PlayerUpdateDto playerUpdateDto) {
        try {
            PreparedStatement statement = connection.getConnection().prepareStatement(UPSERT_CHARACTER_LOCATION);
            statement.setInt(1, playerUpdateDto.getCharacterId());
            statement.setDouble(2, playerUpdateDto.getPositionX());
            statement.setDouble(3, playerUpdateDto.getPositionY());
            statement.executeUpdate();
            return true;
        } catch (SQLException throwables) {
            System.err.println("Error while executing Query!");
            throwables.printStackTrace();
            return false;
        }
    }
}
