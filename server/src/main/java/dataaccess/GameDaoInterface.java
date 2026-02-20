package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;

public interface GameDaoInterface {

    void createGame(GameData game);

    GameData getGame(int gameID);

    void updateGame(ChessGame.TeamColor playerColor, int gameID);

    void deleteGame(int gameID);

    void deleteAllGames();

    void listGames();
}
