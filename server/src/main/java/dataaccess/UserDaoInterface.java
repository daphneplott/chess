package dataaccess;

import model.UserData;

import java.util.ArrayList;


public interface UserDaoInterface {

    void createUser(UserData user) throws DataAccessException, BadDataRequestException;

    UserData getUser(String username) throws DataAccessException, BadDataRequestException;

    void deleteAllUsers() throws DataAccessException, BadDataRequestException;

    ArrayList<String> getUsernames() throws DataAccessException;

    ArrayList<UserData> getUsers() throws DataAccessException;
}
