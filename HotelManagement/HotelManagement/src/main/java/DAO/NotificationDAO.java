package DAO;

import model.Notification;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    private final Connection connection;
    private final DataSource dataSource;

    public NotificationDAO(Connection connection) {
        this.connection = connection;
        this.dataSource = null;
    }

    public NotificationDAO(DataSource dataSource) {
        this.connection = null;
        this.dataSource = dataSource;
    }

    public List<Notification> getNotificationsByHotelId(int hotelId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE hotel_id = ?";
        if (connection != null) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, hotelId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String message = resultSet.getString("message");
                        Timestamp createdAt = resultSet.getTimestamp("created_at");
                        notifications.add(new Notification(id, hotelId, message, createdAt));
                    }
                }
            }
        } else {
            try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, hotelId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String message = resultSet.getString("message");
                        Timestamp createdAt = resultSet.getTimestamp("created_at");
                        notifications.add(new Notification(id, hotelId, message, createdAt));
                    }
                }
            }
        }
        return notifications;
    }

    public void createNotification(Notification notification) throws SQLException {
        String sql = "INSERT INTO notifications (hotel_id, message, created_at) VALUES (?, ?, ?)";
        if (connection != null) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, notification.getHotelId());
                statement.setString(2, notification.getMessage());
                statement.setTimestamp(3, notification.getCreatedAt());
                statement.executeUpdate();
            }
        } else {
            try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, notification.getHotelId());
                statement.setString(2, notification.getMessage());
                statement.setTimestamp(3, notification.getCreatedAt());
                statement.executeUpdate();
            }
        }
    }
}
