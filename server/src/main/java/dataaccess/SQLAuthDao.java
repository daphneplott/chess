package dataaccess;

import model.AuthData;

import java.util.ArrayList;

public class SQLAuthDao implements AuthDaoInterface{

    public SQLAuthDao() {

    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException, BadDataRequestException {
        // insert
    };

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException, BadDataRequestException {
        // query by token
    };

    @Override
    public void deleteAuth(String authToken) throws DataAccessException, BadDataRequestException {
        // update
    };

    @Override
    public void deleteAllAuth() throws DataAccessException, BadDataRequestException {
        //update with TRUNCATE
    };

    @Override
    public ArrayList<AuthData> getAuthTokens() {
        //query, iterate to get list
    };

    @Override
    public ArrayList<String> getAuthValues() {
        //query, iterate to get list
    };
}
