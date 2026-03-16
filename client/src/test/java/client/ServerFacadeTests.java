package client;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    static String url;
    static HttpClient client;
    AuthData auth;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        url = String.format("http://localhost:%d", port);
        facade = new ServerFacade(url);
        client = HttpClient.newHttpClient();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearServer() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/db"))
                .method("DELETE", HttpRequest.BodyPublishers.ofString(""));
        var builtRequest = request.build();
        var response = client.send(builtRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200,response.statusCode());

        var response2 = facade.register(new String[]{"register", "user", "1234", "user@email.com"});
        auth = response2.auth();
    }


    @Test
    public void registerSuccess() {
        var response = facade.register(new String[]{"register","new user","5678","newuser@mail.com"});
        Assertions.assertEquals(200,response.responseCode());
        Assertions.assertTrue(response.auth().authToken().length() > 10);
    }

    @Test
    public void registerFailure() {
        var response = facade.register(new String[]{"register","user","5678","different@mail.com"});
        Assertions.assertEquals(403,response.responseCode());
        Assertions.assertTrue(response.auth().authToken().length() < 2);
    }

    @Test
    public void logoutSuccess() {
        var response = facade.logout(auth);
        Assertions.assertEquals(200,response.responseCode());
    }

    @Test
    public void logoutFailure() {
        var response = facade.logout(new AuthData("7777","fake username"));
        Assertions.assertEquals(401,response.responseCode());
    }

    @Test
    public void loginSuccess() {
        facade.logout(auth);
        var response = facade.login(new String[]{"login","user","1234"});
        Assertions.assertEquals(200,response.responseCode());
        Assertions.assertTrue(response.auth().authToken().length() > 10);
    }

    @Test
    public void loginFailure() {
        facade.logout(auth);
        var response = facade.login(new String[]{"login","user","5678"});
        Assertions.assertEquals(401,response.responseCode());
        Assertions.assertTrue(response.auth().authToken().length() < 2);
    }

    @Test
    public void createSuccess() {
        var response = facade.create(new String[]{"create","cool name"},auth);
        Assertions.assertEquals(200,response.responseCode());
        Assertions.assertEquals(1,response.gameID());
    }

    @Test
    public void createFailure() {
        var response = facade.create(new String[]{"create","cool name"}, new AuthData("1234","user"));
        Assertions.assertEquals(401,response.responseCode());
        Assertions.assertEquals(-1,response.gameID());
    }

    @Test
    public void listSuccess() {
        facade.create(new String[]{"create","cool name"},auth);
        facade.create(new String[]{"create","cool name 2"},auth);
        var response = facade.list(auth);
        Assertions.assertEquals(200,response.responseCode());
        ArrayList<GameData> expected = new ArrayList<>();
        expected.add(new GameData(1,null,null,"cool_name",new ChessGame()));
        expected.add(new GameData(2,null,null,"cool_name_2",new ChessGame()));
        Assertions.assertEquals(expected,response.list());
    }

    @Test
    public void listFailure() {
        var response = facade.list(new AuthData("1234","user"));
        Assertions.assertEquals(401,response.responseCode());
        Assertions.assertEquals(new ArrayList<GameData>(), response.list());
    }

    @Test
    public void joinSuccess() {
        var response2 = facade.create(new String[]{"create","cool name"}, auth);
        var response = facade.join(new String[]{"join","1","white"},auth);
        Assertions.assertEquals(200,response.responseCode());
    }

    @Test
    public void joinFailure() {
        var response2 = facade.create(new String[]{"create","cool name"}, auth);
        facade.join(new String[]{"join","1","white"},auth);
        var response1 = facade.register(new String[]{"register","new user","5678","newuser@mail.com"});
        AuthData newAuth = response1.auth();
        var response = facade.join(new String[]{"join","1","white"},newAuth);
        Assertions.assertEquals(403,response.responseCode());
    }
}
