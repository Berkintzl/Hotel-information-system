package DAO;

import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public void createUser(User user) throws SQLException {
        String sql = "INSERT INTO User (username, password, role_id, hotel_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setInt(3, user.getRoleId());
            statement.setInt(4, user.getHotelId());
            statement.executeUpdate();
        }
    }

    public List<User> getUsersByHotelIdAndRoleId(int hotelId, int roleId) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM User WHERE hotel_id = ? AND role_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, hotelId);
            statement.setInt(2, roleId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");
                    int userRoleId = resultSet.getInt("role_id");
                    int userHotelId = resultSet.getInt("hotel_id");
                    users.add(new User(id, username, password, userRoleId, userHotelId, "receptionist"));
                }
            }
        }
        return users;
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.*, r.role_name FROM User u JOIN UserRole r ON u.role_id = r.id";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                int roleId = resultSet.getInt("role_id");
                int hotelId = resultSet.getInt("hotel_id");
                String roleName = resultSet.getString("role_name");
                users.add(new User(id, username, password, roleId, hotelId, roleName));
            }
        }
        return users;
    }
}
