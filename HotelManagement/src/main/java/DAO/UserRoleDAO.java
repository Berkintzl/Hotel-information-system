package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRoleDAO {
    private final Connection connection;

    public UserRoleDAO(Connection connection) {
        this.connection = connection;
    }


    public int getRoleId(String roleName) throws SQLException {
        String sql = "SELECT id FROM UserRole WHERE role_name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roleName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                throw new SQLException("Role not found");
            }
        }
    }
}
