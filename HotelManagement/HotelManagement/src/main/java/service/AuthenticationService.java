package service;

import model.User;
import DAO.UserDAO;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.SQLException;
import java.util.List;

public class AuthenticationService {
    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(UserDAO userDAO, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }

    public User authenticate(String username, String password) throws SQLException {
        User user = userDAO.getByUsername(username);
        if (user == null) return null;
        String stored = user.getPassword();
        boolean looksEncoded = stored != null && stored.startsWith("$2") && stored.length() >= 60;
        if (looksEncoded && passwordEncoder.matches(password, stored)) return user;
        if (stored != null && stored.equals(password)) return user;
        return null;
    }
}
