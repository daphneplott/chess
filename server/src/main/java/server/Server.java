package server;

import dataaccess.*;
import io.javalin.*;
import com.google.gson.Gson;
import io.javalin.http.Context;
import model.AuthData;
import model.UserData;
import service.*;


public class Server {

    private final Javalin javalin;
    private UserDaoInterface userDao;
    private GameDaoInterface gameDao;
    private AuthDaoInterface authDao;
    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;
    private Gson gson;


    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.post("/user",this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session",this::logout);
        javalin.get("/game",this::listGames);
        javalin.post("/game",this::createGame);
        javalin.put("/game",this::joinGame);
        javalin.delete("/db",this::clear);

        userDao = new MemoryUserDao();
        gameDao = new MemoryGameDao();
        authDao = new MemoryAuthDao();
        userService = new UserService(userDao, authDao);
        gameService = new GameService(gameDao, authDao);
        clearService = new ClearService(userDao,gameDao,authDao);

        gson = new Gson();
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void register(Context ctx) {
        Requests.RegisterRequest request = gson.fromJson(ctx.body(), Requests.RegisterRequest.class);
        Results.RegisterResult result = userService.register(request);
        ctx.result(gson.toJson(result));
    }

    private void login(Context ctx) {
        Requests.LoginRequest request = gson.fromJson(ctx.body(),Requests.LoginRequest.class);
        Results.LoginResult result = userService.login(request);
        ctx.result(gson.toJson(result));
    }

    private void logout(Context ctx) {
        Requests.LogoutRequest request = new Requests.LogoutRequest(ctx.header("authorization"));
        Results.LogoutResult result = userService.logout(request);
        ctx.result(gson.toJson(result));
    }

    private void listGames(Context ctx) {
        var request = new Requests.ListGamesRequest(ctx.header("authorization"));
        var result = gameService.listGames(request);
        ctx.result(gson.toJson(result));
    }

    private void createGame(Context ctx) {
        Requests.CreateGameRequest requestBody = gson.fromJson(ctx.body(),Requests.CreateGameRequest.class);
        var request = new Requests.CreateGameRequest(
                ctx.header("authorization"),
                requestBody.gameName()
        );
        var result = gameService.createGame(request);
        ctx.result(gson.toJson(result));
    }

    private void joinGame(Context ctx) {
        var requestBody = gson.fromJson(ctx.body(),Requests.JoinGameRequest.class);
        var request = new Requests.JoinGameRequest(
                ctx.header("authorization"),
                requestBody.playerColor(),
                requestBody.gameID()
        );
        var result = gameService.joinGame(request);
        ctx.result(gson.toJson(result));
    }

    private void clear(Context ctx) {
        var result = clearService.clear();
        ctx.result(gson.toJson(result));
    }


}
