package service;

import dataaccess.AuthDaoInterface;
import dataaccess.DataAccessException;
import dataaccess.GameDaoInterface;
import dataaccess.UserDaoInterface;

public class ClearService {

    private AuthDaoInterface authDao;
    private GameDaoInterface gameDao;
    private UserDaoInterface userDao;

    public ClearService(UserDaoInterface userDao, GameDaoInterface gameDao, AuthDaoInterface authDao) {
        this.userDao = userDao;
        this.gameDao = gameDao;
        this.authDao = authDao;
    };

    public Results.ClearResult clear() {
        try {
            userDao.deleteAllUsers();
            gameDao.deleteAllGames();
            authDao.deleteAllAuth();
            return new Results.ClearResult(200,"");
        } catch (DataAccessException e) {
            return new Results.ClearResult(500,"Error:" + e.getMessage());
        }

    };
}
