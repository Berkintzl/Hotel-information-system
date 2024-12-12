package service;

import model.User;
import DAO.UserDAO;

import java.sql.SQLException;
import java.util.List;

public class AuthenticationService {
    private final UserDAO userDAO;

    public AuthenticationService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User authenticate(String username, String password) throws SQLException {
        List<User> users = userDAO.getAllUsers();
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
}
