package DAO;

import model.AdditionalService;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdditionalServiceDAO {
    private final Connection connection;
    private final DataSource dataSource;

    public AdditionalServiceDAO(Connection connection) {
        this.connection = connection;
        this.dataSource = null;
    }

    public AdditionalServiceDAO(DataSource dataSource) {
        this.connection = null;
        this.dataSource = dataSource;
    }

    public void createAdditionalService(AdditionalService service) throws SQLException {
        String sql = "INSERT INTO additionalservices (service_type, season, usage_count, hotel_id) VALUES (?, ?, ?, ?)";
        if (connection != null) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, service.getServiceType());
                statement.setString(2, service.getSeason());
                statement.setInt(3, service.getUsageCount());
                statement.setInt(4, service.getHotelId());
                statement.executeUpdate();
            }
        } else {
            try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, service.getServiceType());
                statement.setString(2, service.getSeason());
                statement.setInt(3, service.getUsageCount());
                statement.setInt(4, service.getHotelId());
                statement.executeUpdate();
            }
        }
    }

    public List<AdditionalService> getAdditionalServicesByHotelId(int hotelId) throws SQLException {
        List<AdditionalService> services = new ArrayList<>();
        String sql = "SELECT * FROM additionalservices WHERE hotel_id = ?";
        if (connection != null) {
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
        } else {
            try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
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
        }
        return services;
    }
}
