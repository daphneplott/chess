package service;

import chess.ChessGame;
import model.AuthData;

public class Requests {

    public record RegisterRequest(String username, String password, String email) {};

    public record LoginRequest(String username, String password) {};

    public record LogoutRequest(String auth) {};

    public record ListGamesRequest(String auth) {};

    public record CreateGameRequest(String auth, String gameName) {};

    public record JoinGameRequest(String auth, ChessGame.TeamColor playerColor, int gameID) {};
}
