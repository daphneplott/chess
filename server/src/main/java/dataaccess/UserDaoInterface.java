package dataaccess;

import model.UserData;


public interface UserDaoInterface {

    void createUser(UserData user) throws DataAccessException, BadDataRequestException;

    UserData getUser(String username) throws DataAccessException, BadDataRequestException;

    void deleteUser(String username) throws DataAccessException, BadDataRequestException;

    void deleteAllUsers() throws DataAccessException, BadDataRequestException;
}