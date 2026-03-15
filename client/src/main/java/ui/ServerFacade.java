package ui;

import model.AuthData;
import model.GameData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.ArrayList;

public class ServerFacade {

    // Special record classes

    public record loginRegisterResponse(AuthData auth, int responseCode) {}

    public record logoutJoinObserveResponse(int responseCode) {}

    public record listGamesResponse(ArrayList<GameData> list, int responseCode) {}

    public record createGameResponse(int gameID, int responseCode) {}

    private final HttpClient client;
    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        client = HttpClient.newHttpClient();
        this.serverUrl = serverUrl;
    }

    public loginRegisterResponse login(String[] params) {

        // (cmd), username, password
        // Post /session

        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))

    }

    public loginRegisterResponse register(String[] params) {

        // username, password, email
        // post /user
    }

    public logoutJoinObserveResponse logout(AuthData auth) {
        // authorization
        //DELECTE /session
    }

    public createGameResponse create(String[] params, AuthData auth) {
        // auth, gamename
        // Post /game
    }

    public listGamesResponse list(AuthData auth) {
        //auth
        // get /game

    }

    public logoutJoinObserveResponse join(String[] params, AuthData auth) {
        // color, id, auth
        // put /game
    }

    public logoutJoinObserveResponse observe(String[] params, AuthData auth) {
        //id, auth
    }


}

