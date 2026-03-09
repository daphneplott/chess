package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Requests;
import service.Results;

import java.util.ArrayList;
import java.util.UUID;

public class DataAccessTests {

    static AuthDaoInterface authDao;
    static GameDaoInterface gameDao;
    static UserDaoInterface userDao;

    UserData newUser = new UserData("new user","5678","new@mail.com");
    AuthData authData = new AuthData("1234","new user");
    UserData copyUser = new UserData("new user", "a", "cool@mail.com");
    AuthData badAuthData = new AuthData(null,"new user");
    GameData game = new GameData(0,null,null,"Test Game",new ChessGame());
    GameData game2 = new GameData(1,"new user",null,"Test Game2",new ChessGame());


    @BeforeAll
    public static void setUp() {
        try {
            authDao = new SQLAuthDao();
            gameDao = new SQLGameDao();
            userDao = new SQLUserDao();
        } catch (Exception e) {
            System.out.println("Could not configure");
            Assertions.fail();
        }
    }

    @BeforeEach
    public void clear() {
        try {
            authDao.deleteAllAuth();
            gameDao.deleteAllGames();
            userDao.deleteAllUsers();
        } catch (Exception e) {
            System.out.println("Could not clear");
            Assertions.fail();
        }
    }

    // Game - Create, get, update, delete all/, list
    // User - Create/, get/, delete all/, get usernames, get users
    // Auth - Create/, delete/, delete all/, get authTokens, get auth

    @Test
    public void clearGameSuccess() throws DataAccessException {
        gameDao.createGame(game);
        gameDao.deleteAllGames();
        try {
            Assertions.assertEquals(new ArrayList<>(), gameDao.listGames());
        } catch (DataAccessException e) {
            Assertions.fail();}
    }

    @Test
    public void clearUserSuccess() throws DataAccessException {
        userDao.createUser(newUser);
        userDao.deleteAllUsers();
        try {
            Assertions.assertEquals(new ArrayList<>(), userDao.getUsers());
        }catch (DataAccessException e) {
            Assertions.fail();}
        try {
            Assertions.assertEquals(new ArrayList<>(), userDao.getUsernames());
        }catch (DataAccessException e) {
            Assertions.fail();}
    }

    @Test
    public void clearAuthSuccess() throws DataAccessException {
        authDao.createAuth(authData);
        authDao.deleteAllAuth();
        try {
            Assertions.assertEquals(authDao.getAuthTokens(), new ArrayList<AuthData>());
        } catch (DataAccessException e) {Assertions.assertTrue(false);}
    }

    @Test
    public void createUserSuccess() {
        try {
            userDao.createUser(newUser);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            Assertions.assertTrue(userDao.getUsernames().contains("new user"));
        } catch (DataAccessException e) {Assertions.assertTrue(false);};
    }

    @Test
    public void createAuthSuccess() {
        try {
            authDao.createAuth(authData);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            Assertions.assertTrue(authDao.getAuthTokens().contains(authData));
        } catch (DataAccessException e) {Assertions.assertTrue(false);};
    }

    @Test
    public void createUserFailure() {
        try {
            userDao.createUser(newUser);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            userDao.createUser(copyUser);
        } catch (BadDataRequestException e) {
            Assertions.assertTrue(true);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createAuthFailure() {
        try {
            authDao.createAuth(badAuthData);
        } catch (DataAccessException e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void deleteAuthSuccess() throws DataAccessException {
        authDao.createAuth(authData);

        try {authDao.deleteAuth(authData.authToken());}
        catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            Assertions.assertEquals(new ArrayList<AuthData>(), authDao.getAuthTokens());
        }catch (DataAccessException e) {
            Assertions.fail();
        };
    }

    @Test
    public void deleteAuthFailure() throws DataAccessException {
        authDao.createAuth(authData);

        try {
            authDao.deleteAuth("123");
        } catch (DataAccessException e) {
        }
        ;

        var data = new ArrayList<AuthData>();
        data.add(authData);
        try {
            Assertions.assertEquals(data, authDao.getAuthTokens());
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    public void getUserSucess() throws DataAccessException {
        userDao.createUser(newUser);
        try {
            UserData actual = userDao.getUser("new user");
            Assertions.assertEquals(newUser,actual);
        } catch (DataAccessException e) {
            Assertions.fail();}
    }

    @Test
    public void getUserFailure() throws DataAccessException {
        userDao.createUser(newUser);
        try {
            UserData actual = userDao.getUser("fake user");
        } catch (BadDataRequestException e) {
            Assertions.assertTrue(true);
        } catch (DataAccessException e) {
            Assertions.fail();}
    }

    @Test
    public void createGameSuccess() throws DataAccessException {
        int id = gameDao.createGame(game);
        Assertions.assertFalse(gameDao.listGames().isEmpty());
        Assertions.assertEquals(0,id);
    }

    @Test
    public void createGameFailure() throws DataAccessException {
        // I ... don't know what might fail...
    }

    @Test
    public void listGamesSuccess() throws DataAccessException {
        gameDao.createGame(game);

        ArrayList<GameData> actualGames = gameDao.listGames();
        ArrayList<GameData> expectedGames = new ArrayList<>();
        expectedGames.add(new GameData(0,null,null,"Test Game",new ChessGame()));
        Assertions.assertEquals(expectedGames,actualGames);

        gameDao.createGame(game2);
        expectedGames.add(new GameData(1, "new user",null,"Test Game2",new ChessGame()));
        actualGames = gameDao.listGames();
        Assertions.assertEquals(expectedGames,actualGames);
    }

    @Test
    public void listGamesFailure() throws DataAccessException {
        // Also no idea...
    }

    @Test
    public void updateGameSuccess() throws DataAccessException {
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
};