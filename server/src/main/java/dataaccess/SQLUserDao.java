package dataaccess;

import model.UserData;

import java.util.ArrayList;

public class SQLUserDao implements UserDaoInterface {

    public SQLUserDao() {

    };

    @Override
    public void createUser(UserData user) throws DataAccessException, BadDataRequestException {
        // Update
    };

    @Override
    public UserData getUser(String username) throws DataAccessException, BadDataRequestException {
        // query, search by username
    };

    @Override
    public void deleteAllUsers() throws DataAccessException, BadDataRequestException {
        // update
    };

    @Override
    public ArrayList<String> getUsernames() {
        // query, iterate to get list
    };

    @Override
    public ArrayList<UserData> getUsers() {
       // query, iterate to get list
    };

    private final String[] createStatements = {
        """
        CREATE TABLE IF NOT EXISTS user (
        `username` varchar(256) NOT NULL,
        `password` varchar(256) NOT NULL,
        `email` varchar(256) NOT NULL,
        PRIMARY KEY (`username`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """
    };
}

