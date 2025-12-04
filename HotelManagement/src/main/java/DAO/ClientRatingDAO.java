package DAO;

import model.ClientRating;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientRatingDAO {
    private final Connection connection;
    private final DataSource dataSource;

    public ClientRatingDAO(Connection connection) {
        this.connection = connection;
        this.dataSource = null;
    }

    public ClientRatingDAO(DataSource dataSource) {
        this.connection = null;
        this.dataSource = dataSource;
    }

    public void createClientRating(ClientRating rating) throws SQLException {
        String sql = "INSERT INTO clientratings (client_id, rating, hotel_id) VALUES (?, ?, ?)";
        if (connection != null) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, rating.getClientId());
                statement.setInt(2, rating.getRating());
                statement.setInt(3, rating.getHotelId());
                statement.executeUpdate();
            }
        } else {
            try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, rating.getClientId());
                statement.setInt(2, rating.getRating());
                statement.setInt(3, rating.getHotelId());
                statement.executeUpdate();
            }
        }
    }

    public List<ClientRating> getClientRatingsByHotelId(int hotelId) throws SQLException {
        List<ClientRating> ratings = new ArrayList<>();
        String sql = "SELECT * FROM clientratings WHERE hotel_id = ?";
        if (connection != null) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, hotelId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        int clientId = resultSet.getInt("client_id");
                        int rating = resultSet.getInt("rating");
                        ratings.add(new ClientRating(id, clientId, rating, hotelId));
                    }
                }
            }
        } else {
            try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, hotelId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        int clientId = resultSet.getInt("client_id");
                        int rating = resultSet.getInt("rating");
                        ratings.add(new ClientRating(id, clientId, rating, hotelId));
                    }
                }
            }
        }
        return ratings;
    }
}
