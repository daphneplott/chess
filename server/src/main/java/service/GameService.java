package service;

import chess.ChessGame;
import dataaccess.AuthDaoInterface;
import dataaccess.BadDataRequestException;
import dataaccess.DataAccessException;
import dataaccess.GameDaoInterface;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;

public class GameService {

    private GameDaoInterface gameDao;
    private AuthDaoInterface authDao;

    public GameService(GameDaoInterface gameDao, AuthDaoInterface authDao) {
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public Results.ListGamesResult listGames(Requests.ListGamesRequest request) {
        try {
            authDao.getAuth(request.auth());
        } catch (BadDataRequestException e) {
            return new Results.ListGamesResult(401,new ArrayList<>(),"Error: unauthorized");
        } catch (DataAccessException e) {
            return new Results.ListGamesResult(500, new ArrayList<>(), String.format("Error: %s",e.getMessage()));
        }
        try {
            ArrayList<GameData> games = gameDao.listGames();
            return new Results.ListGamesResult(200,games,null);
        } catch (DataAccessException e) {
            return new Results.ListGamesResult(500, new ArrayList<>(),String.format("Error: %s", e.getMessage()));
        }
    }

    public Results.CreateGameResult createGame(Requests.CreateGameRequest request) {
        try {
            authDao.getAuth(request.auth());
        } catch (BadDataRequestException e) {
            return new Results.CreateGameResult(401,null,"Error: unauthorized");
        } catch (DataAccessException e) {
            return new Results.CreateGameResult(500,null, String.format("Error: %s",e.getMessage()));
        }
        try {
            int gameID = gameDao.getGameID();
            GameData game = new GameData(gameID,null,null,request.gameName(),new ChessGame());
            gameDao.createGame(game);
            return new Results.CreateGameResult(200,gameID,null);
        } catch (DataAccessException e) {
            return new Results.CreateGameResult(500,null, String.format("Error: %s",e.getMessage()));
        }
    }

    public Results.JoinGameResult joinGame(Requests.JoinGameRequest request) {
        AuthData auth;
        try {
            auth = authDao.getAuth(request.auth());
        } catch (BadDataRequestException e) {
            return new Results.JoinGameResult(401,"Error: unauthorized");
        } catch (DataAccessException e) {
            return new Results.JoinGameResult(500, String.format("Error: %s",e.getMessage()));
        }
        try {
            gameDao.updateGame(request.playerColor(), auth.username(),request.gameID());
            return new Results.JoinGameResult(200, null);
        } catch (DataAccessException e) {
            return new Results.JoinGameResult(500, String.format("Error: %s",e.getMessage()));
        } catch (BadDataRequestException e) {
            return new Results.JoinGameResult(403, "Error: already taken");
        }
    }
}
