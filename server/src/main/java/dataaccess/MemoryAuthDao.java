package dataaccess;

import model.AuthData;

import java.util.ArrayList;

public class MemoryAuthDao implements AuthDaoInterface {

    private ArrayList<AuthData> authTokens;

    public MemoryAuthDao() {
        authTokens = new ArrayList<>();
    }

    @Override
    public void createAuth(AuthData auth) {
        authTokens.add(auth);
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
    public void updateAuth(String authToken) {
        return;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        for (AuthData auth : authTokens) {
            if (auth.authToken().equals(authToken)) {
                authTokens.remove(authToken);
                return;
            }
        }
        throw new BadDataRequestException("Auth Token does not exist.");
    };

    @Override
    public void deleteAllAuth() {
        authTokens.clear();
    };
}
