package dataaccess;

import model.AuthData;


public interface AuthDaoInterface {

    void createAuth(AuthData auth);

    AuthData getAuth(String authToken) throws DataAccessException;

    void updateAuth(String authToken);

    void deleteAuth(String authToken) throws DataAccessException;

    void deleteAllAuth();
}

