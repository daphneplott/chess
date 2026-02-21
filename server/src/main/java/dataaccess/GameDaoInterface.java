package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public interface GameDaoInterface {

    void createGame(GameData game) throws DataAccessException, BadDataRequestException;

    GameData getGame(int gameID) throws DataAccessException, BadDataRequestException;

    void updateGame(ChessGame.TeamColor playerColor, String username, int gameID) throws DataAccessException, BadDataRequestException;

    void deleteGame(int gameID) throws DataAccessException, BadDataRequestException;

    void deleteAllGames() throws DataAccessException, BadDataRequestException;

    ArrayList<GameData> listGames() throws DataAccessException, BadDataRequestException;
}
