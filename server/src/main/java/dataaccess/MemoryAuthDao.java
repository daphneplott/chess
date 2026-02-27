package dataaccess;

import model.AuthData;

import java.util.ArrayList;

public class MemoryAuthDao implements AuthDaoInterface {

    private ArrayList<AuthData> authTokens;
    private ArrayList<String> authValues;

    public MemoryAuthDao() {
        authTokens = new ArrayList<>();
        authValues = new ArrayList<>();
    }

    @Override
    public void createAuth(AuthData auth) {
        authTokens.add(auth);
        authValues.add(auth.authToken());
    };

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        for (AuthData auth : authTokens) {
            if (auth.authToken().equals(authToken)) {
                return auth;
            }
        }
        throw new BadDataRequestException("Auth Token does not exist.");
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        for (AuthData auth : authTokens) {
            if (auth.authToken().equals(authToken)) {
                authTokens.remove(auth);
                authValues.remove(authToken);
                return;
            }
        }
        throw new BadDataRequestException("Auth Token does not exist.");
    };

    @Override
    public void deleteAllAuth() {
        authTokens.clear();
        authValues.clear();
    };

    @Override
    public ArrayList<AuthData> getAuthTokens() {
        return authTokens;
    }

    @Override
    public ArrayList<String> getAuthValues() {
        return authValues;
    }
}
