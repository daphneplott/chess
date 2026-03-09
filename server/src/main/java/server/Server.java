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

//    public Server() {
//        new Server(false);
//    }

    public Server() {
        Boolean memory = false;

        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.post("/user",this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session",this::logout);
        javalin.get("/game",this::listGames);
        javalin.post("/game",this::createGame);
        javalin.put("/game",this::joinGame);
        javalin.delete("/db",this::clear);

        if (memory) {
            userDao = new MemoryUserDao();
            gameDao = new MemoryGameDao();
            authDao = new MemoryAuthDao();
        } else {
            try {
                userDao = new SQLUserDao();
                gameDao = new SQLGameDao();
                authDao = new SQLAuthDao();
            } catch (DataAccessException e) {
                System.out.println("Unable to create daos");
            }
        }

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
        Results.RegisterResult result;
        Requests.RegisterRequest request = gson.fromJson(ctx.body(), Requests.RegisterRequest.class);
        if (request.username() == null | request.password() == null | request.email() == null) {
            result = new Results.RegisterResult(400,null,null,"Error: bad request");
        } else {
            result = userService.register(request);
        }
        ctx.result(gson.toJson(result));
        ctx.status(result.code());
    }

    private void login(Context ctx) {
        Results.LoginResult result;
        Requests.LoginRequest request = gson.fromJson(ctx.body(),Requests.LoginRequest.class);
        if (request.username() == null | request.password() == null) {
            result = new Results.LoginResult(400,null,null,"Error: bad request");
        } else {
            result = userService.login(request);
        }
        ctx.result(gson.toJson(result));
        ctx.status(result.code());
    }

    private void logout(Context ctx) {
        Results.LogoutResult result;
        Requests.LogoutRequest request = new Requests.LogoutRequest(ctx.header("authorization"));
        if (request.auth() == null) {
            result = new Results.LogoutResult(400,"Error: bad request");
        } else {
            result = userService.logout(request);
        }
        ctx.result(gson.toJson(result));
        ctx.status(result.code());
    }

    private void listGames(Context ctx) {
        Results.ListGamesResult result;
        var request = new Requests.ListGamesRequest(ctx.header("authorization"));
        if (request.auth() == null) {
            result = new Results.ListGamesResult(400, null, "Error: bad request");
        } else {
            result = gameService.listGames(request);
        }
        ctx.result(gson.toJson(result));
        ctx.status(result.code());
    }

    private void createGame(Context ctx) {
        Results.CreateGameResult result;
        Requests.CreateGameRequest requestBody = gson.fromJson(ctx.body(),Requests.CreateGameRequest.class);
        var request = new Requests.CreateGameRequest(
                ctx.header("authorization"),
                requestBody.gameName()
        );
        if (request.gameName() == null) {
            result = new Results.CreateGameResult(400,null,"Error: bad request");
        } else {
            result = gameService.createGame(request);
        }
        ctx.result(gson.toJson(result));
        ctx.status(result.code());
    }

    private void joinGame(Context ctx) {
        Results.JoinGameResult result;
        var requestBody = gson.fromJson(ctx.body(),Requests.JoinGameRequest.class);
        var request = new Requests.JoinGameRequest(
                ctx.header("authorization"),
                requestBody.playerColor(),
                requestBody.gameID()
        );
        if (request.auth() == null | request.playerColor() == null | request.gameID() == null) {
            result = new Results.JoinGameResult(400, "Error: bad request");
        } else {
            result = gameService.joinGame(request);
        }
        ctx.result(gson.toJson(result));
        ctx.status(result.code());
    }

    private void clear(Context ctx) {
        var result = clearService.clear();
        ctx.result(gson.toJson(result));
        ctx.status(result.code());
    }


}
