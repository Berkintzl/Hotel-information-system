package DAO;

import model.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {
    private final Connection connection;

    public ClientDAO(Connection connection) {
        this.connection = connection;
    }

    public int createClient(Client client) throws SQLException {
        String sql = "INSERT INTO clients (name, email, hotel_id) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, client.getName());
            statement.setString(2, client.getEmail());
            statement.setInt(3, client.getHotelId());
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating client failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating client failed, no ID obtained.");
                }
            }
        }
    }

    public List<Client> getAllClients() throws SQLException {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                int hotelId = resultSet.getInt("hotel_id");
                clients.add(new Client(id, name, email, hotelId));
            }
        }
        return clients;
    }

    public List<Client> getClientsByHotelId(int hotelId) throws SQLException {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT c.* FROM clients c WHERE c.hotel_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, hotelId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String email = resultSet.getString("email");
                    clients.add(new Client(id, name, email, hotelId));
                }
            }
        }
        return clients;
    }
}
