package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public interface GameDaoInterface {

    void createGame(GameData game);

    GameData getGame(int gameID) throws DataAccessException;

    void updateGame(ChessGame.TeamColor playerColor, String username, int gameID) throws DataAccessException;

    void deleteGame(int gameID) throws DataAccessException;

    void deleteAllGames();

    ArrayList<GameData> listGames();
}
