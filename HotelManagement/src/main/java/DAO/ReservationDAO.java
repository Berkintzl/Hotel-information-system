package DAO;

import model.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {
    private final Connection connection;

    public ReservationDAO(Connection connection) {
        this.connection = connection;
    }

    public void createReservation(Reservation reservation) throws SQLException {
        String sql = "INSERT INTO reservations (client_id, reservation_number, room_number, cancellation_type, room_category, start_date, end_date, is_cancelled, hotel_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reservation.getClientId());
            statement.setString(2, reservation.getReservationNumber());
            statement.setString(3, reservation.getRoomNumber());
            statement.setString(4, reservation.getCancellationType());
            statement.setString(5, reservation.getRoomCategory());
            statement.setDate(6, reservation.getStartDate());
            statement.setDate(7, reservation.getEndDate());
            statement.setBoolean(8, reservation.isCancelled());
            statement.setInt(9, reservation.getHotelId());
            statement.executeUpdate();
        }
    }

    public void cancelReservation(String reservationNumber, String cancellationType) throws SQLException {
        String sql = "UPDATE reservations SET is_cancelled = TRUE, cancellation_type = ? WHERE reservation_number = ? AND is_cancelled = FALSE";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cancellationType);
            statement.setString(2, reservationNumber);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No reservations were cancelled. Check if the reservation number is correct and the reservation is not already cancelled.");
            }
        }
    }


    public Reservation getReservationByNumber(String reservationNumber) throws SQLException {
        String sql = "SELECT * FROM reservations WHERE reservation_number = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, reservationNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int clientId = resultSet.getInt("client_id");
                    String roomNumber = resultSet.getString("room_number");
                    String cancellationType = resultSet.getString("cancellation_type");
                    String roomCategory = resultSet.getString("room_category");
                    Date startDate = resultSet.getDate("start_date");
                    Date endDate = resultSet.getDate("end_date");
                    boolean isCancelled = resultSet.getBoolean("is_cancelled");
                    int hotelId = resultSet.getInt("hotel_id");

                    return new Reservation(id, clientId, reservationNumber, roomNumber, cancellationType, roomCategory, startDate, endDate, isCancelled, hotelId);
                }
            }
        }
        return null;
    }

    public List<Reservation> getReservationsByHotelId(int hotelId) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE hotel_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, hotelId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int clientId = resultSet.getInt("client_id");
                    String reservationNumber = resultSet.getString("reservation_number");
                    String roomNumber = resultSet.getString("room_number");
                    String cancellationType = resultSet.getString("cancellation_type");
                    String roomCategory = resultSet.getString("room_category");
                    Date startDate = resultSet.getDate("start_date");
                    Date endDate = resultSet.getDate("end_date");
                    boolean isCancelled = resultSet.getBoolean("is_cancelled");
                    int retrievedHotelId = resultSet.getInt("hotel_id");

                    reservations.add(new Reservation(id, clientId, reservationNumber, roomNumber, cancellationType, roomCategory, startDate, endDate, isCancelled, retrievedHotelId));
                }
            }
        }
        return reservations;
    }

    public List<Reservation> getExpiringReservations() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE end_date = CURDATE() + INTERVAL 1 DAY";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int clientId = resultSet.getInt("client_id");
                String reservationNumber = resultSet.getString("reservation_number");
                String roomNumber = resultSet.getString("room_number");
                String cancellationType = resultSet.getString("cancellation_type");
                String roomCategory = resultSet.getString("room_category");
                Date startDate = resultSet.getDate("start_date");
                Date endDate = resultSet.getDate("end_date");
                boolean isCancelled = resultSet.getBoolean("is_cancelled");
                int hotelId = resultSet.getInt("hotel_id");

                reservations.add(new Reservation(id, clientId, reservationNumber, roomNumber, cancellationType, roomCategory, startDate, endDate, isCancelled, hotelId));
            }
        }
        return reservations;
    }
}
