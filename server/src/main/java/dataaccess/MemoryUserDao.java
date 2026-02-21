package dataaccess;

import model.UserData;

import javax.xml.crypto.Data;
import java.util.ArrayList;


public class MemoryUserDao implements UserDaoInterface {

    private ArrayList<UserData> users;
    private ArrayList<String> usernames;
    public MemoryUserDao() {
        this.users = new ArrayList<>();
        this.usernames = new ArrayList<>();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if(this.usernames.contains(user.username())) {
            throw new BadDataRequestException("This username is already taken.");
        } else {
            this.users.add(user);
            this.usernames.add(user.username());
        }
    };

    @Override
    public UserData getUser(String username) throws DataAccessException {
        for(UserData user : this.users) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        throw new BadDataRequestException("This username does not exist");
    };

    @Override
    public void deleteUser(String username) throws DataAccessException {
        for (UserData user : this.users) {
            if (user.username().equals(username)) {
                this.users.remove(user);
                this.usernames.remove(user.username());
                return;
            }
        }
        throw new BadDataRequestException("This username does not exist.");
    };

    @Override
    public void deleteAllUsers() {
        this.users.clear();
        this.usernames.clear();
    };
}
