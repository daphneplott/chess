package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import service.*;

import java.util.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UnitTests {
    private static UserData existingUser;
    private static UserData newUser;
    private static UserService userService;
    private static GameService gameService;
    private static ClearService clearService;
    private static String existingAuth;
    private static GameDaoInterface gameDao;
    private static UserDaoInterface userDao;
    private static AuthDaoInterface authDao;


    @BeforeAll
    public static void init() {

        gameDao = new MemoryGameDao();
        userDao = new MemoryUserDao();
        authDao = new MemoryAuthDao();

        userService = new UserService(userDao, authDao);
        gameService = new GameService(gameDao, authDao);
        clearService = new ClearService(userDao, gameDao, authDao);

        existingUser = new UserData("existing user","1234","eu@mail.com");
        newUser = new UserData("new user","5678","new@mail.com");
    }

    @BeforeEach
    public void setup() {
        clearService.clear();

        //one user already logged in
        Results.RegisterResult regResult = userService.register(new Requests.RegisterRequest("existing user","1234","eu@mail.com"));
        existingAuth = regResult.authToken();
    }

    @Test
    public void clearSuccess() {
        Results.ClearResult result = clearService.clear();
        try {
            Assertions.assertEquals(authDao.getAuthTokens(), new ArrayList<AuthData>());
        } catch (DataAccessException e) {Assertions.assertTrue(false);}
        try {
            Assertions.assertEquals(gameDao.listGames(), new ArrayList<>());
        } catch (DataAccessException e) {Assertions.assertTrue(false);}
        try {
            Assertions.assertEquals(userDao.getUsers(), new ArrayList<>());
        }catch (DataAccessException e) {Assertions.assertTrue(false);}
        try {
            Assertions.assertEquals(userDao.getUsernames(), new ArrayList<>());
        }catch (DataAccessException e) {Assertions.assertTrue(false);}
    }

    @Test
    public void registerSuccess() {
        Results.RegisterResult regResult = userService.register(new Requests.RegisterRequest("new user","5678","new@mail.com"));
        Assertions.assertEquals(regResult.code(), 200);
        Assertions.assertEquals(regResult.username(), "new user");
        try {
            Assertions.assertTrue(userDao.getUsernames().contains("new user"));
        } catch (DataAccessException e) {Assertions.assertTrue(false);};
        Assertions.assertNotEquals(existingAuth, regResult.authToken());
        try {
            Assertions.assertTrue(authDao.getAuthTokens().contains(new AuthData(regResult.authToken(),"new user")));
        } catch (DataAccessException e) {Assertions.assertTrue(false);};
    }

    @Test
    public void registerFailure() {
        Results.RegisterResult regResult = userService.register(new Requests.RegisterRequest("existing user","5678","new@mail.com"));
        Assertions.assertEquals(regResult.code(),403);
    }

    @Test
    public void logoutSuccess() {
        Results.LogoutResult logoutResult = userService.logout(new Requests.LogoutRequest(existingAuth));
        Assertions.assertEquals(logoutResult.code(), 200);
        try {
            Assertions.assertEquals(authDao.getAuthTokens(),new ArrayList<AuthData>());
        }catch (DataAccessException e) {Assertions.assertTrue(false);};
    }

    @Test
    public void logoutFailure() {
        String authToken = UUID.randomUUID().toString();
        Assertions.assertNotEquals(authToken,existingAuth);
        Results.LogoutResult logoutResult = userService.logout(new Requests.LogoutRequest(authToken));
        Assertions.assertEquals(logoutResult.code(),401);
    }

    @Test
    public void loginSucess() {
        userService.logout(new Requests.LogoutRequest(existingAuth));
        Results.LoginResult loginResult = userService.login(new Requests.LoginRequest("existing user","1234"));
        Assertions.assertEquals(loginResult.code(),200);
        try {
            Assertions.assertTrue(authDao.getAuthValues().contains(loginResult.authToken()));
        } catch (DataAccessException e) {Assertions.assertTrue(false);}
    }

    @Test
    public void loginFailure() {
        userService.logout(new Requests.LogoutRequest(existingAuth));
        Results.LoginResult loginResult = userService.login(new Requests.LoginRequest("existing user","123"));
        Assertions.assertEquals(loginResult.code(),401);
        try {
            Assertions.assertTrue(authDao.getAuthValues().isEmpty());
        } catch (DataAccessException e) {Assertions.assertTrue(false);}
    }

    @Test
    public void createGameSuccess() throws DataAccessException {
        Results.CreateGameResult createGameResult = gameService.createGame(new Requests.CreateGameRequest(existingAuth,"Test Game"));
        Assertions.assertEquals(createGameResult.code(),200);
        Assertions.assertFalse(gameDao.listGames().isEmpty());
        Results.CreateGameResult createGameResult2 = gameService.createGame(new Requests.CreateGameRequest(existingAuth,"Test Game 2"));
        Assertions.assertEquals(createGameResult2.code(),200);
    }

    @Test
    public void createGameFailure() throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        Assertions.assertNotEquals(authToken,existingAuth);
        Results.CreateGameResult createGameResult = gameService.createGame(new Requests.CreateGameRequest(authToken,"Test Game"));
        Assertions.assertEquals(createGameResult.code(),401);
        Assertions.assertTrue(gameDao.listGames().isEmpty());
    }

    @Test
    public void listGamesSuccess() {
        Results.CreateGameResult createGameResult = gameService.createGame(new Requests.CreateGameRequest(existingAuth,"Test Game"));
        Results.ListGamesResult listGameResult = gameService.listGames(new Requests.ListGamesRequest(existingAuth));
        Assertions.assertEquals(listGameResult.code(),200);
        ArrayList<GameData> expectedGames = new ArrayList<>();
        expectedGames.add(new GameData(1,null,null,"Test Game",new ChessGame()));
        Assertions.assertEquals(listGameResult.games(), expectedGames);

        Results.CreateGameResult createGameResult2 = gameService.createGame(new Requests.CreateGameRequest(existingAuth,"Test Game 2"));
        Results.ListGamesResult listGameResult2 = gameService.listGames(new Requests.ListGamesRequest(existingAuth));
        Assertions.assertEquals(listGameResult2.code(),200);
        expectedGames.add(new GameData(2, null,null,"Test Game 2",new ChessGame()));
        Assertions.assertEquals(listGameResult2.games(),expectedGames);
    }

    @Test
    public void listGamesFailure() throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        Assertions.assertNotEquals(authToken,existingAuth);
        Results.CreateGameResult createGameResult = gameService.createGame(new Requests.CreateGameRequest(existingAuth,"Test Game"));
        Results.ListGamesResult listGamesResult = gameService.listGames(new Requests.ListGamesRequest(authToken));
        Assertions.assertEquals(listGamesResult.code(),401);
        Assertions.assertTrue(listGamesResult.games().isEmpty());
    }

    @Test
    public void joinGameSuccess() throws DataAccessException {
        Results.CreateGameResult createGameResult =
                gameService.createGame(new Requests.CreateGameRequest(existingAuth,"Test Game"));
        Results.JoinGameResult joinGameResult =
                gameService.joinGame(new Requests.JoinGameRequest(existingAuth, ChessGame.TeamColor.WHITE, createGameResult.gameID()));
        Assertions.assertEquals(joinGameResult.code(), 200);
        ArrayList<GameData> expectedGames = new ArrayList<>();
        expectedGames.add(new GameData(1,"existing user",null,"Test Game",new ChessGame()));
        Assertions.assertEquals(gameDao.listGames(), expectedGames);
    }

    @Test
    public void joinGameFailure() throws DataAccessException {
        Results.CreateGameResult createGameResult = gameService.createGame(new Requests.CreateGameRequest(existingAuth,"Test Game"));
        gameService.joinGame(new Requests.JoinGameRequest(existingAuth, ChessGame.TeamColor.WHITE, createGameResult.gameID()));

        Results.RegisterResult regResult =
                userService.register(new Requests.RegisterRequest("new user","5678","new@mail.com"));
        String newAuth = regResult.authToken();

        Results.JoinGameResult joinGameResult =
                gameService.joinGame(new Requests.JoinGameRequest(newAuth,ChessGame.TeamColor.WHITE,createGameResult.gameID()));

        Assertions.assertEquals(joinGameResult.code(), 403);
        ArrayList<GameData> expectedGames = new ArrayList<>();
        expectedGames.add(new GameData(1,"existing user",null,"Test Game",new ChessGame()));
        Assertions.assertEquals(gameDao.listGames(), expectedGames);
    }
}