package server.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.dto.update.PlayerUpdateDto;
import server.repository.dto.CharacterDto;
import server.repository.dto.CharacterLocationDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class CharacterRepository {

    private static final Logger LOG = LoggerFactory.getLogger(CharacterRepository.class);
    private static final String UPSERT_CHARACTER_LOCATION = "INSERT INTO CHARACTER_LOCATIONS " +
            "(character_id, character_pos_x, character_pos_y) " +
            "VALUES " +
            "(?, ?, ?)" +
            "ON CONFLICT (character_id) " +
            "DO UPDATE SET " +
            "character_pos_x = EXCLUDED.character_pos_x, " +
            "character_pos_y = EXCLUDED.character_pos_y;";
    private static final String RETRIEVE_CHARACTER_LOCATIONS = "SELECT " +
            "character_id, character_pos_x, character_pos_y " +
            "FROM CHARACTER_LOCATIONS " +
            "WHERE character_id " +
            "NOT IN " +
            "(?);";
    private static final String RETRIEVE_CHARACTER = "SELECT " +
            "character_id, user_account_id, character_name, character_level " +
            "FROM CHARACTERS " +
            "WHERE user_account_id = ? " +
            "AND " +
            "character_name = ?;";


    public static boolean updateCharacterLocation(DatabaseConnection connection, PlayerUpdateDto playerUpdateDto) {
        return connection.executeUpdate(UPSERT_CHARACTER_LOCATION,
                playerUpdateDto.getCharacterId(),
                playerUpdateDto.getPositionX(),
                playerUpdateDto.getPositionY());
    }

    public static List<CharacterLocationDto> getCharacterLocationsForCharacterId(DatabaseConnection connection, int characterId) {
        List<CharacterLocationDto> resultList = new LinkedList<>();
        try {
            ResultSet resultSet = connection.execute(RETRIEVE_CHARACTER_LOCATIONS, characterId);
            while (resultSet.next()) {
                resultList.add(CharacterLocationDto.builder()
                        .characterId(resultSet.getInt(1))
                        .positionX(resultSet.getDouble(2))
                        .positionY(resultSet.getDouble(3))
                        .build());
            }
        } catch (SQLException e) {
            LOG.warn("Failed to extract any DTOs {}", e.getMessage());
        }
        return resultList;
    }

    public static CharacterDto getCharacterForAccountAndCharacterName(DatabaseConnection connection, int userAccountId, String characterName) {
        CharacterDto characterDto = null;
        try {
            ResultSet resultSet = connection.execute(RETRIEVE_CHARACTER, userAccountId, characterName);
            resultSet.next();
            characterDto = CharacterDto.builder()
                    .characterId(resultSet.getInt("character_id"))
                    .userAccountId(resultSet.getInt("user_account_id"))
                    .characterName(resultSet.getString("character_name"))
                    .characterLevel(resultSet.getInt("character_level"))
                    .build();
        } catch (SQLException e) {
            LOG.warn("Failed to extract DTO {}", e.getMessage());
        }
        return characterDto;
    }
}
