package dataaccess;

import model.AuthData;


public interface AuthDaoInterface {

    void createAuth(AuthData auth) throws DataAccessException, BadDataRequestException;

    AuthData getAuth(String authToken) throws DataAccessException, BadDataRequestException;

    void updateAuth(String authToken) throws DataAccessException, BadDataRequestException;

    void deleteAuth(String authToken) throws DataAccessException, BadDataRequestException;

    void deleteAllAuth() throws DataAccessException, BadDataRequestException;
}

