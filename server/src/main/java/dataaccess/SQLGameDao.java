package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameDao implements GameDaoInterface {

    public SQLGameDao() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public int createGame(GameData game) throws DataAccessException, BadDataRequestException {
        // Insert statement, return id
        var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, json) VALUES (?, ?, ?, ?, ?)";
        String json = new Gson().toJson(game.game());
        int id = executeUpdate(statement, game.whiteUsername(),game.blackUsername(),game.gameName(),json);
        return id;
    };

    @Override
    public GameData getGame(int gameID) throws DataAccessException, BadDataRequestException {
        // Query statement
        // BDR - ID not found
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, json FROM games WHERE gameID = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    } else {
                        throw new BadDataRequestException("Game ID not found");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    };

    @Override
    public void updateGame(ChessGame.TeamColor playerColor, String username, int gameID) throws DataAccessException, BadDataRequestException {
        GameData game = getGame(gameID);
        if (colorTaken(game,playerColor)) {
            throw new BadDataRequestException("Color already taken");
        }
        String statement;
        if (playerColor == ChessGame.TeamColor.WHITE) {
            statement = "UPDATE games SET whiteUsername = ? WHERE gameID = ?";
        } else { // playerColor == ChessGame.TeamColor.BLACK
            statement = "UPDATE games SET blackUsername = ? WHERE gameID = ?";
        }
        executeUpdate(statement,username,gameID);
    };

    private boolean colorTaken(GameData game, ChessGame.TeamColor playerColor) {
        if (playerColor == ChessGame.TeamColor.WHITE) {
            if (game.whiteUsername() == null) {
                return false;
            } else { return true;}
        } else { //playerColor == ChessGame.TeamColor.BLACK) {
            if (game.blackUsername() == null) {
                return false;
            } else { return true; }
        }
    }

    @Override
    public void deleteAllGames() throws DataAccessException {
        // Update statement with TRUNCATE
        var statement = "TRUNCATE games";
        executeUpdate(statement);
    };

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException, BadDataRequestException {
        // Query statement, iterate through all returned results to create list
        var result = new ArrayList<GameData>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, json FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    };

    private GameData readGame(ResultSet rs) throws SQLException {
        var id = rs.getInt("id");
        var json = rs.getString("json");
        GameData game = new Gson().fromJson(json, GameData.class);
        return game.setId(id);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1,p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to update, %s, %s", statement,e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
            `gameID` int NOT NULL AUTO_INCREMENT,
            `whiteUsername` varchar(256) DEFAULT NULL,
            `blackUsername` varchar(256) DEFAULT NULL,
            `gameName` varchar(256) NOT NULL,
            `json` TEXT DEFAULT NULL,
            PRIMARY KEY (`gameID`)
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
            throw new DataAccessException(String.format("Unable to configure Database: %s", e.getMessage()));
        }
    }
}
