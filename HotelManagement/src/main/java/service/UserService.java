package service;

import DAO.UserDAO;
import DAO.UserRoleDAO;
import DAO.HotelDAO;
import model.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.SQLException;
import java.util.List;

public class UserService {
    private final UserDAO userDAO;
    private final UserRoleDAO userRoleDAO;
    private final HotelDAO hotelDAO;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserDAO userDAO, UserRoleDAO userRoleDAO, HotelDAO hotelDAO, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.userRoleDAO = userRoleDAO;
        this.hotelDAO = hotelDAO;
        this.passwordEncoder = passwordEncoder;
    }

    public void addUser(User user) throws SQLException {
        userDAO.createUser(user);
    }

    public void createUserWithRole(String username, String password, String roleName, int hotelId) throws SQLException {
        int roleId = userRoleDAO.getRoleId(roleName);
        String hashed = passwordEncoder.encode(password);
        User user = new User(0, username, hashed, roleId, hotelId, roleName);
        addUser(user);
    }

    public void createReceptionist(String username, String password, int hotelId) throws SQLException {
        createUserWithRole(username, password, "receptionist", hotelId);
    }

    public List<User> getReceptionistsByHotelId(int hotelId) throws SQLException {
        return userDAO.getUsersByHotelIdAndRoleId(hotelId, 4); // 4 receptionistin role_id'si
    }

    public void createManager(String username, String password, int hotelId) throws SQLException {
        createUserWithRole(username, password, "manager", hotelId);
    }

    public List<User> getManagersByHotelId(int hotelId) throws SQLException {
        return userDAO.getUsersByHotelIdAndRoleId(hotelId, 3);
    }
}
