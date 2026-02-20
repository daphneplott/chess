package service;

import chess.ChessGame;
import model.AuthData;

public class Requests {

    public record RegisterRequest(String username, String password, String email) {};

    public record LoginRequest(String username, String password) {};

    public record LogoutRequest(AuthData auth) {};

    public record ListGamesRequest(AuthData auth) {};

    public record CreateGameRequest(AuthData auth, String gameName) {};

    public record JoinGameRequest(AuthData auth, ChessGame.TeamColor playerColor, int gameID) {};
}
