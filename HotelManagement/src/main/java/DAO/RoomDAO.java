package DAO;

import model.Room;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    private final Connection connection;
    private final DataSource dataSource;

    public RoomDAO(Connection connection) {
        this.connection = connection;
        this.dataSource = null;
    }

    public RoomDAO(DataSource dataSource) {
        this.connection = null;
        this.dataSource = dataSource;
    }

    public List<Room> getAvailableRoomsByHotelId(int hotelId) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.id, r.room_number, r.room_category, r.is_occupied, r.hotel_id, " +
                "res.reservation_number " +
                "FROM rooms r " +
                "LEFT JOIN reservations res ON r.room_number = res.room_number AND res.is_cancelled = FALSE " +
                "WHERE r.hotel_id = ?";
        if (connection != null) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, hotelId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String roomNumber = resultSet.getString("room_number");
                        String roomCategory = resultSet.getString("room_category");
                        boolean isOccupied = resultSet.getBoolean("is_occupied");
                        String reservationNumber = resultSet.getString("reservation_number");
                        rooms.add(new Room(id, roomCategory, isOccupied, roomNumber, hotelId, reservationNumber));
                    }
                }
            }
        } else {
            try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, hotelId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String roomNumber = resultSet.getString("room_number");
                        String roomCategory = resultSet.getString("room_category");
                        boolean isOccupied = resultSet.getBoolean("is_occupied");
                        String reservationNumber = resultSet.getString("reservation_number");
                        rooms.add(new Room(id, roomCategory, isOccupied, roomNumber, hotelId, reservationNumber));
                    }
                }
            }
        }
        return rooms;
    }

    public void updateRoomStatus(String roomNumber, boolean isOccupied) throws SQLException {
        String sql = "UPDATE rooms SET is_occupied = ? WHERE room_number = ?";
        if (connection != null) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setBoolean(1, isOccupied);
                statement.setString(2, roomNumber);
                statement.executeUpdate();
            }
        } else {
            try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setBoolean(1, isOccupied);
                statement.setString(2, roomNumber);
                statement.executeUpdate();
            }
        }
    }

    public void setRoomReservation(String roomNumber, String reservationNumber, boolean isOccupied) throws SQLException {
        String sql = "UPDATE rooms SET is_occupied = ?, reservation_number = ? WHERE room_number = ?";
        if (connection != null) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setBoolean(1, isOccupied);
                if (reservationNumber == null) statement.setNull(2, Types.VARCHAR); else statement.setString(2, reservationNumber);
                statement.setString(3, roomNumber);
                statement.executeUpdate();
            }
        } else {
            try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setBoolean(1, isOccupied);
                if (reservationNumber == null) statement.setNull(2, Types.VARCHAR); else statement.setString(2, reservationNumber);
                statement.setString(3, roomNumber);
                statement.executeUpdate();
            }
        }
    }
    public void createRoom(String roomNumber, String roomCategory, int hotelId) throws SQLException {
        String sql = "INSERT INTO rooms (room_number, room_category, is_occupied, hotel_id) VALUES (?, ?, false, ?)";
        if (connection != null) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, roomNumber);
                statement.setString(2, roomCategory);
                statement.setInt(3, hotelId);
                statement.executeUpdate();
            }
        } else {
            try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, roomNumber);
                statement.setString(2, roomCategory);
                statement.setInt(3, hotelId);
                statement.executeUpdate();
            }
        }
    }
    public void deleteRoom(String roomNumber) throws SQLException {
        // First check if the room has any active reservations
        String checkSql = "SELECT COUNT(*) FROM reservations WHERE room_number = ? AND is_cancelled = false";
        if (connection != null) {
            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                checkStmt.setString(1, roomNumber);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Cannot delete room: Room has active reservations");
                }
            }
            // If no active reservations, proceed with deletion
            String sql = "DELETE FROM rooms WHERE room_number = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, roomNumber);
                statement.executeUpdate();
            }
        } else {
            try (Connection conn = dataSource.getConnection()) {
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setString(1, roomNumber);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new SQLException("Cannot delete room: Room has active reservations");
                    }
                }
                String sql = "DELETE FROM rooms WHERE room_number = ?";
                try (PreparedStatement statement = conn.prepareStatement(sql)) {
                    statement.setString(1, roomNumber);
                    statement.executeUpdate();
                }
            }
        }
    }
    public List<Room> getRoomsByHotelId(int hotelId) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE hotel_id = ?";
        if (connection != null) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, hotelId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Room room = new Room(
                                resultSet.getInt("id"),
                                resultSet.getString("room_category"),
                                resultSet.getBoolean("is_occupied"),
                                resultSet.getString("room_number"),
                                hotelId,
                                null
                        );
                        rooms.add(room);
                    }
                }
            }
        } else {
            try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, hotelId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Room room = new Room(
                                resultSet.getInt("id"),
                                resultSet.getString("room_category"),
                                resultSet.getBoolean("is_occupied"),
                                resultSet.getString("room_number"),
                                hotelId,
                                null
                        );
                        rooms.add(room);
                    }
                }
            }
        }
        return rooms;
    }
    public Room getRoomByRoomNumber(String roomNumber) throws SQLException {
        String sql = "SELECT r.id, r.room_number, r.room_category, r.is_occupied, r.hotel_id, " +
                "res.reservation_number " +
                "FROM rooms r " +
                "LEFT JOIN reservations res ON r.room_number = res.room_number AND res.is_cancelled = FALSE " +
                "WHERE r.room_number = ?";
        if (connection != null) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, roomNumber);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String roomCategory = resultSet.getString("room_category");
                        boolean isOccupied = resultSet.getBoolean("is_occupied");
                        int hotelId = resultSet.getInt("hotel_id");
                        String reservationNumber = resultSet.getString("reservation_number");
                        return new Room(id, roomCategory, isOccupied, roomNumber, hotelId, reservationNumber);
                    }
                }
            }
        } else {
            try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, roomNumber);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String roomCategory = resultSet.getString("room_category");
                        boolean isOccupied = resultSet.getBoolean("is_occupied");
                        int hotelId = resultSet.getInt("hotel_id");
                        String reservationNumber = resultSet.getString("reservation_number");
                        return new Room(id, roomCategory, isOccupied, roomNumber, hotelId, reservationNumber);
                    }
                }
            }
        }
        return null;
    }

    public List<Room> getAllRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms";
        if (connection != null) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Room room = new Room(
                            rs.getInt("id"),
                            rs.getString("room_category"),
                            rs.getBoolean("is_occupied"),
                            rs.getString("room_number"),
                            rs.getInt("hotel_id"),
                            rs.getString("reservation_number")
                    );
                    rooms.add(room);
                }
            }
        } else {
            try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Room room = new Room(
                            rs.getInt("id"),
                            rs.getString("room_category"),
                            rs.getBoolean("is_occupied"),
                            rs.getString("room_number"),
                            rs.getInt("hotel_id"),
                            rs.getString("reservation_number")
                    );
                    rooms.add(room);
                }
            }
        }
        return rooms;
    }

}
