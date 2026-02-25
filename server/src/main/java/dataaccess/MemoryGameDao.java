package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public class MemoryGameDao implements GameDaoInterface {

    ArrayList<GameData> games;
    int gameID = 1;

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
                if (colorTaken(game,playerColor)) {
                    throw new BadDataRequestException("Color already taken");
                }
                GameData newgame = game.updatePlayerColor(playerColor, username);
                games.remove(game);
                games.add(gameID-1, newgame);
                return;
            }
        }
        throw new BadDataRequestException("Game ID not found");
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

    @Override
    public int getGameID() {
        gameID += 1;
        return gameID - 1;
    }

    @Override
    public void resetGameID() {
        gameID = 1;
    }
}
