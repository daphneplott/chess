package dataaccess;

import model.UserData;


public interface UserDaoInterface {

    void createAuth(UserData user);

    UserData getUser(String username);

    void deleteUser(String username);

    void deleteAllUsers();
}