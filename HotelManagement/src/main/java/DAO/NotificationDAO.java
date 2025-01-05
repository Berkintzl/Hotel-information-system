package DAO;

import model.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    private final Connection connection;

    public NotificationDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Notification> getNotificationsByHotelId(int hotelId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE hotel_id = ?";
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
        return notifications;
    }

    public void createNotification(Notification notification) throws SQLException {
        String sql = "INSERT INTO notifications (hotel_id, message, created_at) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, notification.getHotelId());
            statement.setString(2, notification.getMessage());
            statement.setTimestamp(3, notification.getCreatedAt());
            statement.executeUpdate();
        }
    }
}
