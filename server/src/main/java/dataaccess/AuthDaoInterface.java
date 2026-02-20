package dataaccess;

import model.AuthData;


public interface AuthDaoInterface {

    void createAuth(AuthData auth);

    AuthData getAuth(String authToken);

    void updateAuth(String authToken);

    void deleteAuth(String authToken);

    void deleteAllAuth();
}

