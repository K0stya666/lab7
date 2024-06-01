package server.managers.databases;

import org.slf4j.*;
import server.utility.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Base64;

public class UserManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserManager.class);
    public static boolean authorized = false;

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/studs";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin";

    private static final String INSERT_USER = "INSERT INTO users (" +
            "username, " +
            "password_hash, " +
            "salt, " +
            "registration_date, " +
            "last_login_date) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS users (" +
            "id SERIAL PRIMARY KEY, " +
            "username VARCHAR(50) UNIQUE NOT NULL, " +
            "password_hash VARCHAR(256) NOT NULL, " +
            "salt VARCHAR(32) NOT NULL, " +
            "registration_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "last_login_date TIMESTAMP)";
    private static final String SELECT_USER_BY_NAME = "SELECT * FROM users WHERE username = ?";

    /**
     * Добавить нового пользователя в БД
     *
     * @param user пользователь
     * @return успешность добавления пользователя
     */
    public boolean addUser(User user) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
                setAttributes(statement, user);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    ResultSet generatedKeys = statement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        return true;
                    } else {
                        LOGGER.error("Failed to retrieve generated keys after adding user");
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error adding user.");
            e.printStackTrace();
        }
        return false;
    }

    public String hashPassword(User user) {
        try {
            var password = user.getPassword();
            var salt = user.getSalt();
            var saltPass = salt + password;

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] passHashBytes = md.digest(saltPass.getBytes());
            return Base64.getEncoder().encodeToString(passHashBytes);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Ошибка создания хэш-пароля");
        }
        return null;
    }

    public boolean checkUser(User user) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_USER_BY_NAME, Statement.RETURN_GENERATED_KEYS)) {
                var username = user.getUsername();
                var password = user.getPassword();

                statement.setString(1, username);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    var passHash = resultSet.getString("password_hash");
                    var salt = resultSet.getString("salt");

                    var verifiablePassword = salt + password;

                    MessageDigest md = MessageDigest.getInstance("SHA-256");
                    byte[] ph = md.digest(verifiablePassword.getBytes());
                    var verifiablePassHash = Base64.getEncoder().encodeToString(ph);

                    return passHash.equals(verifiablePassHash);
                }
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            LOGGER.error("Ошибка проверки пользователя");
        }
        return false;
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }
    public static int getUserId(User user) {
        var username = user.getUsername();
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_USER_BY_NAME, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, username);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Ошибка получения id пользователя");
        }
        return -1;
    }
    private void setAttributes(PreparedStatement statement, User user) throws SQLException {
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getPassword());
        statement.setString(3, user.getSalt());
        statement.setTimestamp(4, Timestamp.from(user.getlastLoginDate().toInstant()));
        statement.setTimestamp(5, Timestamp.from(user.getlastLoginDate().toInstant()));
    }
    public static void isAuthorized(boolean authorized) {
        UserManager.authorized = authorized;
    }
}
