package dataaccess;

import com.google.gson.Gson;
import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static java.sql.Types.NULL;

public class SQLAuthDao implements AuthDaoInterface{

    public SQLAuthDao() throws DataAccessException {
        configureDatabase();
    };

    @Override
    public void createAuth(AuthData auth) throws DataAccessException, BadDataRequestException {
        var statement = "INSERT INTO auth (authToken, username, json) VALUES (?, ?, ?)";
        String json = new Gson().toJson(auth);
        executeUpdate(statement, auth.authToken(), auth.username(),json);
    };

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException, BadDataRequestException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, json FROM auth WHERE authToken = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1,authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                    else {
                        throw new BadDataRequestException("Auth data not found");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    };

    @Override
    public void deleteAuth(String authToken) throws DataAccessException, BadDataRequestException {
        var statement = "DELETE FROM auth WHERE authToken = ?";
        executeUpdate(statement, authToken);
    };

    @Override
    public void deleteAllAuth() throws DataAccessException, BadDataRequestException {
        var statement = "TRUNCATE auth";
        executeUpdate(statement);
    };

    @Override
    public ArrayList<AuthData> getAuthTokens() throws DataAccessException {
        var result = new ArrayList<AuthData>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT json FROM auth";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readAuth(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s",e.getMessage()));
        }
        return result;
    };

    @Override
    public ArrayList<String> getAuthValues() throws DataAccessException {
        var result = new ArrayList<String>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken FROM auth";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(rs.getString("authToken"));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s",e.getMessage()));
        }
        return result;
    };

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        AuthData auth = new Gson().fromJson(json, AuthData.class);
        return auth;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS auth (
                `authToken` varchar(256) NOT NULL,
                `username` varchar(256) NOT NULL,
                `json` varchar(256) NOT NULL,
                PRIMARY KEY (`authToken`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof String p) ps.setString(i+1,p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    };

    private void configureDatabase() throws DataAccessException {
        System.out.println(4);
        DatabaseManager.createDatabase();
        System.out.println(5);
        try (Connection conn = DatabaseManager.getConnection()) {
            System.out.println(6);
            for (String statement : createStatements) {
                System.out.println(7);
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    System.out.println(8);
                    preparedStatement.executeUpdate();
                    System.out.println(9);
                }
            }
        } catch (SQLException e) {
            System.out.println(10);
            System.out.println(e.getMessage());
            throw new DataAccessException(String.format("Unable to configure database: %s", e.getMessage()));
        }
    };
}
