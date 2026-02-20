package dataaccess;

import model.AuthData;


public interface AuthDaoInterface {

    void createAuth(AuthData auth) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void updateAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    void deleteAllAuth() throws DataAccessException;
}

