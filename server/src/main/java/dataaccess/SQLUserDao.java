package dataaccess;

import com.google.gson.Gson;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static java.sql.Types.NULL;

public class SQLUserDao implements UserDaoInterface {

    public SQLUserDao() throws DataAccessException{
        configureDatabase();
    };

    @Override
    public void createUser(UserData user) throws DataAccessException, BadDataRequestException {
        // First check that username is not taken
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT json FROM user WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1,user.username());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        throw new BadDataRequestException("Username already taken");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }

        var statement = "INSERT INTO user (username, password, email, json) VALUES (?, ?, ?, ?)";
        String json = new Gson().toJson(user);
        executeUpdate(statement, user.username(),user.password(),user.email(),json);
    };

    @Override
    public UserData getUser(String username) throws DataAccessException, BadDataRequestException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT json FROM user WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1,username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                    else {
                        throw new BadDataRequestException("This username does not exist");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    };

    @Override
    public void deleteAllUsers() throws DataAccessException, BadDataRequestException {
        var statement = "TRUNCATE user";
        executeUpdate(statement);
    };

    @Override
    public ArrayList<String> getUsernames() throws DataAccessException{
        var result = new ArrayList<String>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM user";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(rs.getString("username"));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to read database: %s", e.getMessage()));
        }
        return result;
    };

    @Override
    public ArrayList<UserData> getUsers() throws DataAccessException {
        var result = new ArrayList<UserData>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT json FROM user";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readUser(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to read database: %s", e.getMessage()));
        }
        return result;
    };

    private UserData readUser(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        UserData user = new Gson().fromJson(json,UserData.class);
        return user;
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) { ps.setString(i + 1, p); }
                    else if (param == null) { ps.setNull(i + 1, NULL); }
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
        """
        CREATE TABLE IF NOT EXISTS user (
        `username` varchar(256) NOT NULL,
        `password` varchar(256) NOT NULL,
        `email` varchar(256) NOT NULL,
        `json` varchar(256) NOT NULL,
        PRIMARY KEY (`username`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (PreparedStatement ps = conn.prepareStatement(statement)) {
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to configure database, %s",e.getMessage()));
        }
    }
}

