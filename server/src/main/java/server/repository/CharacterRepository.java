package server.repository;

import protocol.dto.update.PlayerUpdateDto;
import server.DatabaseConnection;
import server.repository.dto.CharacterLocationDto;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class CharacterRepository {

    public static final String UPSERT_CHARACTER_LOCATION = "INSERT INTO CHARACTER_LOCATIONS " +
            "(character_id, character_pos_x, character_pos_y) " +
            "VALUES " +
            "(?, ?, ?)" +
            "ON CONFLICT (character_id) " +
            "DO UPDATE SET " +
            "character_pos_x = EXCLUDED.character_pos_x, " +
            "character_pos_y = EXCLUDED.character_pos_y;";

    public static final String RETRIEVE_CHARACTER_LOCATIONS = "SELECT " +
            "character_id, character_pos_x, character_pos_y " +
            "FROM CHARACTER_LOCATIONS;";


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

    public static List<CharacterLocationDto> getCharacterLocationsForCharacterId(DatabaseConnection connection, int characterId) {
        List<CharacterLocationDto> resultList = new LinkedList<>();
        try {
            ResultSet result = connection.executeQuery(RETRIEVE_CHARACTER_LOCATIONS);
            while (result.next()) {
                resultList.add(CharacterLocationDto.builder()
                        .characterId(result.getInt(1))
                        .positionX(result.getDouble(2))
                        .positionY(result.getDouble(3))
                        .build());
            }
        } catch (SQLException throwables) {
            System.err.println("Error while executing Query!");
            throwables.printStackTrace();
        }
        return resultList;
    }
}
