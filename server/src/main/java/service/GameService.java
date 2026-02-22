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
    static int gameID = 1;

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
            return new Results.ListGamesResult(200,games,"");
        } catch (DataAccessException e) {
            return new Results.ListGamesResult(500, new ArrayList<>(),String.format("Error: %s", e.getMessage()));
        }
    }

    public Results.CreateGameResult createGame(Requests.CreateGameRequest request) {
        try {
            authDao.getAuth(request.auth());
        } catch (BadDataRequestException e) {
            return new Results.CreateGameResult(401,-1,"Error: unauthorized");
        } catch (DataAccessException e) {
            return new Results.CreateGameResult(500,-1, String.format("Error: %s",e.getMessage()));
        }
        try {
            GameData game = new GameData(gameID,"","",request.gameName(),new ChessGame());
            gameID += 1;
            gameDao.createGame(game);
            return new Results.CreateGameResult(200,gameID - 1,"");
        } catch (DataAccessException e) {
            return new Results.CreateGameResult(500,-1, String.format("Error: %s",e.getMessage()));
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
            return new Results.JoinGameResult(200, "");
        } catch (DataAccessException e) {
            return new Results.JoinGameResult(500, String.format("Error: %s",e.getMessage()));
        } catch (BadDataRequestException e) {
            return new Results.JoinGameResult(403, "Error: already taken");
        }
    }
}
