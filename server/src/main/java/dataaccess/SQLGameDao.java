package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public class SQLGameDao implements GameDaoInterface {

    public SQLGameDao() {
        configureDatabase();
    }

    @Override
    public int createGame(GameData game) throws DataAccessException, BadDataRequestException {
        // Insert statement, return id
        return 0;
    };

    @Override
    public GameData getGame(int gameID) throws DataAccessException, BadDataRequestException {
        // Query statement
        return null;
    };

    @Override
    public void updateGame(ChessGame.TeamColor playerColor, String username, int gameID) throws DataAccessException, BadDataRequestException {
        // Update statement by searching through id
    };

    @Override
    public void deleteAllGames() throws DataAccessException {
        // Update statement with TRUNCATE
    };

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException, BadDataRequestException {
        // Query statement, iterate through all returned results to create list
        return null;
    };

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
            `gameID` int NOT NULL AUTO_INCREMENT,
            `whiteUsername` varchar(256) DEFAULT NULL,
            `blackUsername` varchar(256) DEFAULT NULL,
            `gameName` varchar(256) NOT NULL,
            `game` varchar(256) NOT NULL,
            `json` TEXT DEFAULT NULL,
            PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() {

    }
}
