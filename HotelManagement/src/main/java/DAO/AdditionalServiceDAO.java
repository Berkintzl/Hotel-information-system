package DAO;

import model.AdditionalService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdditionalServiceDAO {
    private final Connection connection;

    public AdditionalServiceDAO(Connection connection) {
        this.connection = connection;
    }

    public void createAdditionalService(AdditionalService service) throws SQLException {
        String sql = "INSERT INTO additionalservices (service_type, season, usage_count, hotel_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, service.getServiceType());
            statement.setString(2, service.getSeason());
            statement.setInt(3, service.getUsageCount());
            statement.setInt(4, service.getHotelId());
            statement.executeUpdate();
        }
    }

    public List<AdditionalService> getAdditionalServicesByHotelId(int hotelId) throws SQLException {
        List<AdditionalService> services = new ArrayList<>();
        String sql = "SELECT * FROM additionalservices WHERE hotel_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, hotelId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String serviceType = resultSet.getString("service_type");
                    String season = resultSet.getString("season");
                    int usageCount = resultSet.getInt("usage_count");
                    services.add(new AdditionalService(id, serviceType, season, usageCount, hotelId));
                }
            }
        }
        return services;
    }
}
