package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.UUID;

public class UserService {

    private UserDaoInterface userDao;
    private AuthDaoInterface authDao;

    public UserService(UserDaoInterface userDao, AuthDaoInterface authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public Results.RegisterResult register(Requests.RegisterRequest request) {
        try {
            String hashedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());
            UserData user = new UserData(request.username(), hashedPassword, request.email());
            userDao.createUser(user);
            String authToken = UUID.randomUUID().toString();
            while (authDao.getAuthValues().contains(authToken)) {
                authToken = UUID.randomUUID().toString();
            }
            AuthData auth = new AuthData(authToken,request.username());
            authDao.createAuth(auth);
            return new Results.RegisterResult(200, request.username(),authToken,null);
        } catch (BadDataRequestException e) {
            return new Results.RegisterResult(403,null,null,"Error: already taken");
        } catch (DataAccessException e) {
            return new Results.RegisterResult(500,null,null,String.format("Error: %s",e.getMessage()));
        }
    }

    public Results.LoginResult login(Requests.LoginRequest request) {
        try {
            String hashedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());
            UserData user = userDao.getUser(request.username());
            if (!user.password().equals(hashedPassword)) {
                return new Results.LoginResult(401,null,null,"Error: unauthorized");
            }
            String authToken = UUID.randomUUID().toString();
            AuthData auth = new AuthData(authToken,request.username());
            authDao.createAuth(auth);
            return new Results.LoginResult(200, request.username(),authToken,null);
        } catch (BadDataRequestException e) {
            return new Results.LoginResult(401,null,null,"Error: unauthorized");
        } catch (DataAccessException e) {
            return new Results.LoginResult(500,null,null,String.format("Error: %s",e.getMessage()));
        }
    }

    public Results.LogoutResult logout(Requests.LogoutRequest request) {
        try {
            authDao.getAuth(request.auth());
        } catch (BadDataRequestException e) {
            return new Results.LogoutResult(401,"Error: unauthorized");
        } catch (DataAccessException e) {
            return new Results.LogoutResult(500, String.format("Error: %s",e.getMessage()));
        }
        try {
            authDao.deleteAuth(request.auth());
            return new Results.LogoutResult(200, null);
        } catch (DataAccessException e) {
            return new Results.LogoutResult(500,String.format("Error: %s", e.getMessage()));
        } catch (BadDataRequestException e) {
            return new Results.LogoutResult(401,"Error: unauthorized");
        }
    }
}
