package DAO;

import model.Hotel;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HotelDAO {
    private final Connection connection;
    private final DataSource dataSource;

    public HotelDAO(Connection connection) {
        this.connection = connection;
        this.dataSource = null;
    }

    public HotelDAO(DataSource dataSource) {
        this.connection = null;
        this.dataSource = dataSource;
    }

    public int createHotel(Hotel hotel) throws SQLException {
        String sql = "INSERT INTO Hotel (name, address) VALUES (?, ?)";
        if (connection != null) {
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, hotel.getName());
                statement.setString(2, hotel.getAddress());
                statement.executeUpdate();
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Creating hotel failed, no ID obtained.");
                }
            }
        } else {
            try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, hotel.getName());
                statement.setString(2, hotel.getAddress());
                statement.executeUpdate();
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Creating hotel failed, no ID obtained.");
                }
            }
        }
    }

    public List<Hotel> getAllHotels() throws SQLException {
        List<Hotel> hotels = new ArrayList<>();
        String sql = "SELECT id, name, address FROM Hotel";
        if (connection != null) {
            try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    Hotel hotel = new Hotel(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("address")
                    );
                    hotels.add(hotel);
                }
            }
        } else {
            try (Connection conn = dataSource.getConnection(); Statement statement = conn.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    Hotel hotel = new Hotel(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("address")
                    );
                    hotels.add(hotel);
                }
            }
        }
        return hotels;
    }

    public Hotel getHotelById(int id) throws SQLException {
        String sql = "SELECT id, name, address FROM Hotel WHERE id = ?";
        if (connection != null) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return new Hotel(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("address")
                    );
                }
            }
        } else {
            try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, id);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return new Hotel(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("address")
                    );
                }
            }
        }
        return null;
    }
}


