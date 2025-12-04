package DAO;

import model.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final Connection connection;
    private final DataSource dataSource;

    public UserDAO(Connection connection) {
        this.connection = connection;
        this.dataSource = null;
    }

    public UserDAO(DataSource dataSource) {
        this.connection = null;
        this.dataSource = dataSource;
    }

    public void createUser(User user) throws SQLException {
        String sql = "INSERT INTO User (username, password, role_id, hotel_id) VALUES (?, ?, ?, ?)";
        if (connection != null) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, user.getUsername());
                statement.setString(2, user.getPassword());
                statement.setInt(3, user.getRoleId());
                statement.setInt(4, user.getHotelId());
                statement.executeUpdate();
            }
        } else {
            try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, user.getUsername());
                statement.setString(2, user.getPassword());
                statement.setInt(3, user.getRoleId());
                statement.setInt(4, user.getHotelId());
                statement.executeUpdate();
            }
        }
    }

    public List<User> getUsersByHotelIdAndRoleId(int hotelId, int roleId) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM User WHERE hotel_id = ? AND role_id = ?";
        if (connection != null) {
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
        } else {
            try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
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
        }
        return users;
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.*, r.role_name FROM User u JOIN UserRole r ON u.role_id = r.id";
        if (connection != null) {
            try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
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
        } else {
            try (Connection conn = dataSource.getConnection(); Statement statement = conn.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
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
        }
        return users;
    }

    public User getByUsername(String username) throws SQLException {
        String sql = "SELECT u.*, r.role_name FROM User u JOIN UserRole r ON u.role_id = r.id WHERE u.username = ?";
        if (connection != null) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String usernameDb = resultSet.getString("username");
                        String password = resultSet.getString("password");
                        int roleId = resultSet.getInt("role_id");
                        int hotelId = resultSet.getInt("hotel_id");
                        String roleName = resultSet.getString("role_name");
                        return new User(id, usernameDb, password, roleId, hotelId, roleName);
                    }
                }
            }
        } else {
            try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String usernameDb = resultSet.getString("username");
                        String password = resultSet.getString("password");
                        int roleId = resultSet.getInt("role_id");
                        int hotelId = resultSet.getInt("hotel_id");
                        String roleName = resultSet.getString("role_name");
                        return new User(id, usernameDb, password, roleId, hotelId, roleName);
                    }
                }
            }
        }
        return null;
    }
}
