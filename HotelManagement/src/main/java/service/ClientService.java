package service;

import DAO.ClientDAO;
import model.Client;

import java.sql.SQLException;
import java.util.List;

public class ClientService {
    private final ClientDAO clientDAO;

    public ClientService(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }

    public int addClient(Client client) throws SQLException {
        return clientDAO.createClient(client);
    }

    public List<Client> getAllClients() throws SQLException {
        return clientDAO.getAllClients();
    }

    public List<Client> getClientsByHotelId(int hotelId) throws SQLException {
        return clientDAO.getClientsByHotelId(hotelId);
    }
}
