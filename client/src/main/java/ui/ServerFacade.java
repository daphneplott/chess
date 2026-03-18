package ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.AuthData;
import model.GameData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerFacade {

    // Special record classes

    public record LoginRegisterResponse(AuthData auth, int responseCode, ErrorMessage message) {}

    public record ErrorMessage(int code, String message) {}

    public record LogoutJoinResponse(int responseCode) {}

    public record ListGamesResponse(ArrayList<GameData> list, int responseCode) {}

    public record CreateGameResponse(int gameID, int responseCode) {}

    public record GameId(int gameID) {}

    public record GameList(ArrayList<GameData> games) {}

    private final HttpClient client;
    private final String serverUrl;
    private final AuthData fakeAuth = new AuthData("","");
    private final Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();

    public ServerFacade(String serverUrl) {
        client = HttpClient.newHttpClient();
        this.serverUrl = serverUrl;
    }

    public LoginRegisterResponse login(String[] params) {

        // (cmd), username, password
        // Post /session

        for (int i = 0; i < params.length; i++) {
            params[i] = params[i].replace(" ","_");
        }

        String body = String.format("{\"username\": %s, \"password\": %s}", params[1],params[2]);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .method("POST", HttpRequest.BodyPublishers.ofString(body));
        return loginRegisterSend(request);
    }

    private LoginRegisterResponse loginRegisterSend(HttpRequest.Builder request) {
        var builtRequest = request.build();
        try {
            var response = client.send(builtRequest, HttpResponse.BodyHandlers.ofString());
            var status = response.statusCode();
            if (status == 200) {
                var responseBody = response.body();
                if (responseBody != null) {
                    return new LoginRegisterResponse(gson.fromJson(responseBody,AuthData.class),200,new ErrorMessage(0,""));
                }
            } else {
                return new LoginRegisterResponse(fakeAuth,status, gson.fromJson(response.body(), ErrorMessage.class));
            }
        } catch (Exception e) {
            return new LoginRegisterResponse(fakeAuth,500,new ErrorMessage(0,""));
        }
        return new LoginRegisterResponse(fakeAuth,500,new ErrorMessage(0,""));
    }

    public LoginRegisterResponse register(String[] params) {

        // username, password, email
        // post /user

        for (int i = 0; i < params.length; i++) {
            params[i] = params[i].replace(" ", "_");
        }

        String body = String.format("{\"username\": %s, \"password\": %s, \"email\": %s}", params[1], params[2], params[3]);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/user"))
                .method("POST", HttpRequest.BodyPublishers.ofString(body));
        var builtRequest = request.build();
        return loginRegisterSend(request);
    }

    public LogoutJoinResponse logout(AuthData auth) {
        // authorization
        //DELETE /session

        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .method("DELETE", HttpRequest.BodyPublishers.ofString(""))
                .setHeader("authorization",auth.authToken());
        var builtRequest = request.build();
        try {
            var response = client.send(builtRequest, HttpResponse.BodyHandlers.ofString());
            return new LogoutJoinResponse(response.statusCode());
        } catch (Exception e) {
            return new LogoutJoinResponse(500);
        }
    }

    public CreateGameResponse create(String[] params, AuthData auth) {
        // auth, gamename
        // Post /game

        for (int i = 0; i < params.length; i++) {
            params[i] = params[i].replace(" ","_");
        }

        String body = String.format("{\"gameName\": %s}", params[1]);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .method("POST", HttpRequest.BodyPublishers.ofString(body))
                .setHeader("authorization",auth.authToken());
        var builtRequest = request.build();
        try {
            var response = client.send(builtRequest, HttpResponse.BodyHandlers.ofString());
            var status = response.statusCode();
            if (status == 200) {
                var responseBody = response.body();
                if (responseBody != null) {
                    GameId gameId = gson.fromJson(responseBody,GameId.class);
                    return new CreateGameResponse(gameId.gameID,200);
                }
            } else {
                return new CreateGameResponse(-1, status);
            }
        } catch (Exception e) {
            return new CreateGameResponse(-1,500);
        }
        return new CreateGameResponse(-1,500);
    }

    public ListGamesResponse list(AuthData auth) {
        //auth
        // get /game

        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .method("GET", HttpRequest.BodyPublishers.ofString(""))
                .setHeader("authorization",auth.authToken());
        var builtRequest = request.build();
        try {
            var response = client.send(builtRequest, HttpResponse.BodyHandlers.ofString());
            var status = response.statusCode();
            if (status == 200) {
                var responseBody = response.body();
                if (responseBody != null) {
                    GameList gameList = gson.fromJson(responseBody,GameList.class);
                    return new ListGamesResponse(gameList.games,200);
                }
            } else {
                return new ListGamesResponse(new ArrayList<>(), status);
            }
        } catch (Exception e) {
            return new ListGamesResponse(new ArrayList<>(),500);
        }
        return new ListGamesResponse(new ArrayList<>(),500);
    }

    public LogoutJoinResponse join(String[] params, AuthData auth) {
        // color, id, auth
        // put /game

        for (int i = 0; i < params.length; i++) {
            params[i] = params[i].replace(" ","_");
        }

        String body = String.format("{\"playerColor\": %s, \"gameID\": %s}", params[2].toUpperCase(), params[1]);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .method("PUT", HttpRequest.BodyPublishers.ofString(body))
                .setHeader("authorization",auth.authToken());
        var builtRequest = request.build();
        try {
            var response = client.send(builtRequest, HttpResponse.BodyHandlers.ofString());
            return new LogoutJoinResponse(response.statusCode());
        } catch (Exception e) {
            return new LogoutJoinResponse(500);
        }
    }



}

