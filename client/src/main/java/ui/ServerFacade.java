package ui;

import model.AuthData;
import model.GameData;

import java.util.ArrayList;

public class ServerFacade {

    // Special record classes

    public record loginRegisterResponse(AuthData auth, int responseCode) {}

    public record logoutJoinObserveResponse(int responseCode) {}

    public record listGamesResponse(ArrayList<GameData> list, int responseCode) {}

    public record createGameResponse(int gameID, int responseCode) {}


    public ServerFacade(String serverUrl) {

    }

    public loginRegisterResponse login(String[] params) {

    }

    public loginRegisterResponse register(String[] params) {

    }

    public logoutJoinObserveResponse logout(AuthData auth) {

    }

    public createGameResponse create(String[] params, AuthData auth) {

    }

    public listGamesResponse list(AuthData auth) {

    }

    public logoutJoinObserveResponse join(String[] params, AuthData auth) {

    }

    public logoutJoinObserveResponse observe(String[] params, AuthData auth) {

    }


}

