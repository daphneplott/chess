package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public class MemoryGameDao implements GameDaoInterface {

    ArrayList<GameData> games;

    public MemoryGameDao() {
        games = new ArrayList<>();
    }

    @Override
    public void createGame(GameData game) {
        games.add(game);
    };

    @Override
    public GameData getGame(int gameID) {
        for (GameData game : games) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        throw new BadDataRequestException("Game ID not found.");
    };

    @Override
    public void updateGame(ChessGame.TeamColor playerColor, String username, int gameID)
        throws DataAccessException {
        for (GameData game : games) {
            if (game.gameID() == gameID) {
                GameData newgame = game.updatePlayerColor(playerColor, username);
                games.remove(game);
                games.add(gameID-1, newgame);
                return;
            }
        }
        throw new BadDataRequestException("Game ID not found");
    };

    @Override
    public void deleteGame(int gameID) throws DataAccessException {
        for (GameData game : games) {
            if (game.gameID() == gameID) {
                games.remove(game);
                return;
            }
        }
        throw new BadDataRequestException("Game ID not found");
    };

    @Override
    public void deleteAllGames() {
        games.clear();
    };

    @Override
    public ArrayList<GameData> listGames() {
        return games;
    };
}
