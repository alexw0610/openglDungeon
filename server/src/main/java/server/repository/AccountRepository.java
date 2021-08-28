package server.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.repository.dto.UserDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountRepository {

    private static final Logger LOG = LoggerFactory.getLogger(CharacterRepository.class);
    private static final String SELECT_USERNAME_PASSWORD = "SELECT user_account_id, username, password " +
            "FROM accounts " +
            "WHERE username = ? " +
            "AND password = ?;";

    private static final String INSERT_CONNECTED_USER = "INSERT INTO CONNECTED_ACCOUNTS " +
            "(user_account_id, connected_ip_address, connected_udp_port) " +
            "VALUES " +
            "(?, ?, ?)" +
            "ON CONFLICT (user_account_id) " +
            "DO UPDATE SET " +
            "connected_ip_address = EXCLUDED.connected_ip_address, " +
            "connected_udp_port = EXCLUDED.connected_udp_port;";

    public static UserDto getByUsernameAndPassword(DatabaseConnection connection, String username, String password) {
        UserDto userDto = null;
        ResultSet resultSet = connection.execute(SELECT_USERNAME_PASSWORD, username, password);
        try {
            resultSet.next();
            userDto = UserDto.builder()
                    .userAccountId(resultSet.getInt("user_account_id"))
                    .username(resultSet.getString("username"))
                    .password(resultSet.getString("password"))
                    .build();
        } catch (SQLException e) {
            LOG.warn("Failed to extract DTO {}", e.getMessage());
        }
        return userDto;
    }

    public static boolean setUserAccountActive(DatabaseConnection connection, int user_account_id, String ip_address, String udp_port) {
        return connection.executeUpdate(INSERT_CONNECTED_USER, user_account_id, ip_address, udp_port);
    }
}
