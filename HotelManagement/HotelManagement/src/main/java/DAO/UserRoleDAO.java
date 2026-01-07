package DAO;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRoleDAO {
    private final Connection connection;
    private final DataSource dataSource;

    public UserRoleDAO(Connection connection) {
        this.connection = connection;
        this.dataSource = null;
    }

    public UserRoleDAO(DataSource dataSource) {
        this.connection = null;
        this.dataSource = dataSource;
    }


    public int getRoleId(String roleName) throws SQLException {
        String sql = "SELECT id FROM UserRole WHERE role_name = ?";
        if (connection != null) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, roleName);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                } else {
                    throw new SQLException("Role not found");
                }
            }
        } else {
            try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
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
}
