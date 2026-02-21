package service;

import model.GameData;

import java.util.ArrayList;

public class Results {

    public record ClearResult(int code, String message) {};

    public record RegisterResult(int code, String username, String auth, String message) {};

    public record LoginResult(int code, String username, String auth, String message) {};

    public record LogoutResult(int code, String message) {};

    public record ListGamesResult(int code, ArrayList<GameData> games, String message) {};

    public record CreateGameResult(int code, int gameID, String message) {};

    public record JoinGameResult(int code, String message) {};
}
