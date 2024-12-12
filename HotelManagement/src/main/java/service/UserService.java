package service;

import DAO.UserDAO;
import DAO.UserRoleDAO;
import DAO.HotelDAO;
import model.User;

import java.sql.SQLException;
import java.util.List;

public class UserService {
    private final UserDAO userDAO;
    private final UserRoleDAO userRoleDAO;
    private final HotelDAO hotelDAO;

    public UserService(UserDAO userDAO, UserRoleDAO userRoleDAO, HotelDAO hotelDAO) {
        this.userDAO = userDAO;
        this.userRoleDAO = userRoleDAO;
        this.hotelDAO = hotelDAO;
    }

    public void addUser(User user) throws SQLException {
        userDAO.createUser(user);
    }

    public void createUserWithRole(String username, String password, String roleName, int hotelId) throws SQLException {
        int roleId = userRoleDAO.getRoleId(roleName);
        User user = new User(0, username, password, roleId, hotelId, roleName);
        addUser(user);
    }

    public void createReceptionist(String username, String password, int hotelId) throws SQLException {
        createUserWithRole(username, password, "receptionist", hotelId);
    }

    public List<User> getReceptionistsByHotelId(int hotelId) throws SQLException {
        return userDAO.getUsersByHotelIdAndRoleId(hotelId, 4); // 4 receptionistin role_id'si
    }
}
