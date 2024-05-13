package server.managers;

import global.models.*;
import org.slf4j.*;
//import server.interstates.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Менеджер, который управляет операциями с БД, включая создание БД,
 * таблиц и управление пользователями
 * @author Kostya666
 */
public class Interstate60 {
//    private static final UserInterstate60 userInterstate60 = new UserInterstate60();
//    private static final Interstate60 interstate60 = new Interstate60();
    private static final Logger LOGGER = LoggerFactory.getLogger(Interstate60.class);

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/studs";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin";

    private static final String INSERT_ROUTES_SQL = "INSERT INTO routes ( " +
            "name, " +
            "coordinate_x, " +
            "coordinate_y, " +
            "creation_date, " +
            "from_name, " +
            "from_x, " +
            "from_y, " +
            "to_name, " +
            "to_x, " +
            "to_y, " +
            "distance, " +
            "user_id) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SELECT_ALL_ROUTES_SQL = "SELECT * FROM routes";
    private static final String REMOVE_ROUTES_SQL = "DELETE FROM routes WHERE id = ?";
    private static final String UPDATE_ROUTES_SQL = "UPDATE routes SET " +
            "name = ?, " +
            "coordinate_x = ?, " +
            "coordinate_y = ?, " +
            "creation_date = ?, " +
            "from_name = ?, " +
            "from_x = ?, " +
            "from_y = ?, " +
            "to_name = ?, " +
            "to_x = ?, " +
            "to_y = ?, " +
            "distance = ? " +
            "WHERE id = ?";
    private static final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS users (" +
            "id SERIAL PRIMARY KEY," +
            "username VARCHAR(50) UNIQUE NOT NULL," +
            "password_hash VARCHAR(256) NOT NULL," +
            "salt VARCHAR(32) NOT NULL," +
            "registration_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
            "last_login_date TIMESTAMP)";
    private static final String CREATE_ROUTES_TABLE_SQL = "CREATE TABLE IF NOT EXISTS routes (" +
            "id SERIAL PRIMARY KEY," +
            "name TEXT NOT NULL CHECK (name <> '')," +
            "coordinate_x FLOAT NOT NULL," +
            "coordinate_y FLOAT NOT NULL," +
            "creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
            "from_name VARCHAR(502) NOT NULL," +
            "from_x BIGINT NOT NULL," +
            "from_y INT NOT NULL," +
            "to_name VARCHAR(502) NOT NULL," +
            "to_x BIGINT NOT NULL," +
            "to_y INT NOT NULL," +
            "distance FLOAT CHECK (distance > 1)," +
            "user_id INT," +
            "FOREIGN KEY (user_id) REFERENCES users(id))";

    /**
     * Добавить новый маршрут в БД
     * @param route добавляемый маршрут
     * @param userId id пользователя, добавляющего маршрут
     */
    public int addRoute(Route route, int userId) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(INSERT_ROUTES_SQL, Statement.RETURN_GENERATED_KEYS)) {
                setAttributes(statement, route, userId);

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    ResultSet generatedKeys = statement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    } else {
                        LOGGER.error("Failed to retrieve generated keys after adding route");
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error adding route.");
        }
        return -1;
    }

    /**
     * Добавляет коллекцию маршрутов в БД
     * @param routes коллекция маршрутов
     * @param userId id пользователя, добавляющего коллекцию маршрутов
     */
    public void addRoutes(Collection<Route> routes, int userId) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(INSERT_ROUTES_SQL)) {
                for (Route route : routes) {
                    setAttributes(statement, route, userId);
                    statement.addBatch();
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error while adding routes.");
        }
    }

    /**
     * Получает маршруты из БД
     * @return коллекция маршрутов
     */
    public List<Route> getRoutes() {
        List<Route> routes = new LinkedList<>();
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SELECT_ALL_ROUTES_SQL);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Route route = getRouteFromDatabase(resultSet);
                routes.add(route);
            }
        } catch (SQLException e) {
            LOGGER.error("Error while retrieving routes from the database");
        }
        return routes;
    }

    /**
     * Удалить маршрут по заданному значению id
     * @param id id
     */
    public void removeRouteById(int id) {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(REMOVE_ROUTES_SQL);
            statement.setInt(1, id);
        } catch (SQLException e) {
            LOGGER.error("Error while deleting ticket with id");
        }
    }

    /**
     * Обновляет маршрут
     * @param route маршрут
     */
    public void updateRoute(Route route) {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(UPDATE_ROUTES_SQL);
            setAttributes(statement, route, route.getId());
            //return executePrepareUpdate(statement) > 0;
        } catch (SQLException e) {
            LOGGER.error("Error while updating route");
            //return false;
        }
    }

    public static void createDatabaseIfNotExists() {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                boolean databaseExists = checkDatabaseExists(connection);
                LOGGER.info("Database and tables created successfully.");
            } else {
                LOGGER.info("Database already exists.");
            }
            createTablesIfNotExists(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Создаёт таблицу маршрутов
     * @param connection подключение
     */
    public static void createRoutesTable(Connection connection) {
        try {
            Statement statement = connection.createStatement();
            if (statement == null) {
                LOGGER.error("Statement is null.");
                return;
            }
            statement.executeUpdate(CREATE_ROUTES_TABLE_SQL);
        } catch (SQLException e) {
            LOGGER.error("Error creating statement", e);
        }
    }

    /**
     * Создаёт таблицу пользователей
     * @param connection подключение
     */
    public static void createUsersTable(Connection connection) {
        try {
            Statement statement = connection.createStatement();
            if (statement == null) {
                LOGGER.error("Statement is null");
                return;
            }
            statement.executeUpdate(CREATE_USER_TABLE);
        } catch (SQLException e) {
            LOGGER.error("Error creating statement", e);
        }
    }

    /**
     * Проверяет существование таблиц
     * @param connection подключение к БД
     * @return true, если подключение к БД установлено
     * @throws SQLException SQL-ошибка
     */
    public static boolean checkDatabaseExists(Connection connection) throws SQLException {
        return connection.getMetaData().getCatalogs().next();
    }

    public static void createTablesIfNotExists(Connection connection) throws SQLException {
        if (connection != null) {
            createUsersTable(connection);
            createRoutesTable(connection);
            LOGGER.info("Tables created successfully (if not existed).");
        } else {
            LOGGER.error("Connection is null.");
        }
    }

    private static Statement createStatement(Connection connection) {
        if (connection == null) {
            LOGGER.error("Connection is null");
            return null;
        }
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            LOGGER.error("Error creating statement", e);
            return null;
        }
    }

    /**
     * Устанавливает атрибуты маршрута
     * @param statement SQL-запрос
     * @param route маршрут
     * @param userId id пользователя
     * @throws SQLException SQL-исключение
     */
    private void setAttributes(PreparedStatement statement, Route route, int userId) throws SQLException {
        statement.setString(1, route.getName());
        statement.setFloat(2, route.getCoordinates().getX());
        statement.setFloat(3, route.getCoordinates().getY());
        statement.setTimestamp(4, Timestamp.from(route.getCreationDate().toInstant()));
        statement.setString(5, route.getFrom().getName());
        statement.setLong(6, route.getFrom().getX());
        statement.setInt(7, route.getFrom().getY());
        statement.setString(8, route.getTo().getName());
        statement.setLong(9, route.getTo().getX());
        statement.setInt(10, route.getTo().getY());
        statement.setFloat(11, route.getDistance());
        statement.setInt(12, userId);
    }

    /**
     * Получает маршрут из БД
     * @param resultSet
     * @return маршрут
     * @throws SQLException SQL-исключение
     */
    private Route getRouteFromDatabase(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        Float coordinateX = resultSet.getFloat("coordinate_x");
        Float coordinateY = resultSet.getFloat("coordinate_y");
        Date creationDate = resultSet.getTimestamp("creation_date");
        String fromName = resultSet.getString("from_name");
        Long fromX = resultSet.getLong("from_x");
        Integer fromY = resultSet.getInt("from_y");
        String toName = resultSet.getString("to_name");
        Long toX = resultSet.getLong("to_x");
        Integer toY = resultSet.getInt("to_y");
        float distance = resultSet.getFloat("distance");
        if (resultSet.wasNull()) {
            if (coordinateX == 0.0f) { coordinateX = null; }
            if (coordinateY == 0.0f) { coordinateY = null; }

            if (fromX == 0L) { fromX = null; }
            if (fromY == 0L) { fromY = null; }

            if (toX == 0L) { toX = null; }
            if (toY == 0L) { toY = null; }
        }
        return new Route(id, name, new Coordinates(coordinateX, coordinateY),creationDate,
                new Location(fromX, fromY, fromName), new Location(toX, toY, toName), distance);
    }

    /**
     * Выполняет подключение к БД
     * @return подключение к БД
     * @throws SQLException SQL-исключение
     */
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }
}
